package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "USER_BY_ID_CACHE", key = "#result.id()"),
                    @CachePut(value = "USER_BY_CPF_CACHE", key = "#result.cpf()")
            },
            evict = {
                    @CacheEvict(value = "USER_PAGE_CACHE", allEntries = true)
            }
    )
    public UserResponse create(UserRequest userRequest) {
        log.info("Creating new user with name: {}", userRequest.name());
        if (userRepository.existsByCpf(userRequest.cpf())) {
            throw new BusinessException("CPF already registered: " + userRequest.cpf(), HttpStatus.CONFLICT);
        }

        User user = userRepository.save(userMapper.toEntity(userRequest));
        log.info("User created with ID: {}", user.getId());

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "USER_BY_ID_CACHE", key = "#result.id()"),
                    @CachePut(value = "USER_BY_CPF_CACHE", key = "#result.cpf()")
            },
            evict = {
                    @CacheEvict(value = "USER_PAGE_CACHE", allEntries = true)
            }
    )
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Updating user with ID: {}", id);
        User existingUser = findExistingUser(id);

        existingUser.validateCpfImmutability(userRequest.cpf());
        existingUser.updateName(userRequest.name());

        return userMapper.toDto(existingUser);
    }

    @Override
    @Cacheable(value = "USER_BY_ID_CACHE", key = "#id", unless = "#result == null")
    public UserResponse getById(Long id) {
        log.info("Retrieving user with ID: {}", id);
        User existingUser = findExistingUser(id);

        log.info("User with ID: {} retrieved successfully.", existingUser.getId());

        return userMapper.toDto(existingUser);
    }

    @Override
    @Cacheable(value = "USER_BY_CPF_CACHE", key = "#cpf", unless = "#result == null")
    public UserResponse getByCpf(String cpf) {
        log.info("Retrieving user with CPF.");
        User user = userRepository.findByCpf(cpf).orElseThrow(
                () -> new BusinessException("User not found with CPF: " + cpf, HttpStatus.NOT_FOUND));

        log.info("User with CPF retrieved successfully: {}", user.getName());

        return userMapper.toDto(user);
    }

    @Override
    @Cacheable(
            value = "USER_PAGE_CACHE",
            key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':sort:' + #pageable.sort.toString()",
            unless = "#result == null || #result.isEmpty()")
    public Page<UserResponse> getAll(Pageable pageable) {
        log.info("Retrieving all users - page: {}, size: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<User> users = userRepository.findAll(pageable);

        log.info("Total users retrieved: {}", users.getTotalElements());

        return users.map(userMapper::toDto);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "USER_BY_ID_CACHE", key = "#id"),
                    @CacheEvict(value = "USER_BY_CPF_CACHE", allEntries = true),
                    @CacheEvict(value = "USER_PAGE_CACHE", allEntries = true)
            }
    )
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.delete(findExistingUser(id));

        log.info("User with ID: {} deleted successfully.", id);
    }

    public User findExistingUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BusinessException("User not found with ID: " + id, HttpStatus.NOT_FOUND)
        );
    }
}
