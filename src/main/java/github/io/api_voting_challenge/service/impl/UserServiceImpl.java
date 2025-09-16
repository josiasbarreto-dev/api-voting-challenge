package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.UserRequest;
import github.io.api_voting_challenge.dto.UserResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest userRequest) {
        if (userRepository.existsByCpf(userRequest.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + userRequest.cpf());
        }

        User userEntity = userMapper.toEntity(userRequest);

        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest userRequest) {
        User existingUser = getUser(id);
        if (!Objects.equals(existingUser.getCpf(), userRequest.cpf())){
            throw new CpfModificationNotAllowedException("Cannot change the CPF of an existing User.");
        }

        existingUser.setName(userRequest.name());

        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public UserResponse getById(Long id) {
        var existingUser = getUser(id);
        return userMapper.toDto(existingUser);
    }

    @Override
    public UserResponse getByCpf(String cpf) {
        return userMapper.toDto(userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("User not found with CPF: " + cpf)));
    }

    public Page<UserResponse> getAll(Pageable pageable){
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id)
        );
    }
}
