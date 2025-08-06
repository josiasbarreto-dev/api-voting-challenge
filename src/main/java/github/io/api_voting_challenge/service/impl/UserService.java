package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.model.Role;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.service.UserServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByCpf(user.getCpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + user.getCpf());
        }
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        var existingUser = getById(id);
        if (!Objects.equals(existingUser.getCpf(), user.getCpf())){
            throw new CpfModificationNotAllowedException("Cannot change the CPF of an existing user.");
        }
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        return userRepository.save(existingUser);
   }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id)
        );
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found with email: " + email)
        );
    }

    @Override
    public User getByCpf(String cpf) {
        return userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("User not found with CPF: " + cpf)
        );
    }
}
