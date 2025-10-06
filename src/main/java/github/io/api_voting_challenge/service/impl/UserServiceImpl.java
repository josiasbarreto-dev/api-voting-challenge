package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest userRequest) {
        log.info("Creating new user with name: {}", userRequest.name());
        if (userRepository.existsByCpf(userRequest.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + userRequest.cpf());
        }

        User user = userRepository.save(userMapper.toEntity(userRequest));
        log.info("User created with ID: {}", user.getId());

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Updating user with ID: {}", id);
        User existingUser = getUser(id);
        if (!Objects.equals(existingUser.getCpf(), userRequest.cpf())){
            throw new CpfModificationNotAllowedException("Cannot change the CPF of an existing User.");
        }

        existingUser.setName(userRequest.name());
        User user = userRepository.save(existingUser);
        log.info("User with ID: {} updated successfully.", user.getId());

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse getById(Long id) {
        log.info("Retrieving user with ID: {}", id);
        User existingUser = getUser(id);

        log.info("User with ID: {} retrieved successfully.", existingUser.getId());

        return userMapper.toDto(existingUser);
    }

    @Override
    public UserResponse getByCpf(String cpf) {
        log.info("Retrieving user with CPF.");
        User user = userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("User not found with CPF: " + cpf));

        log.info("User with CPF retrieved successfully: {}", user.getName());

        return userMapper.toDto(user);
    }

    public Page<UserResponse> getAll(Pageable pageable){
        log.info("Retrieving all users - page: {}, size: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<User> users = userRepository.findAll(pageable);

        log.info("Total users retrieved: {}", users.getTotalElements());

        return users.map(userMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.delete(getUser(id));

        log.info("User with ID: {} deleted successfully.", id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id)
        );
    }
}
