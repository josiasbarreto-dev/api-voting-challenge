package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AdminUserRequest;
import github.io.api_voting_challenge.dto.AdminUserResponse;
import github.io.api_voting_challenge.dto.VoterRequest;
import github.io.api_voting_challenge.dto.VoterResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.enums.Role;
import github.io.api_voting_challenge.repository.UserAdminRepository;
import github.io.api_voting_challenge.repository.UserVotingRepository;
import github.io.api_voting_challenge.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserAdminRepository userAdminRepository;
    private final UserVotingRepository userVotingRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AdminUserResponse create(AdminUserRequest userRequest) {
        if (userAdminRepository.existsByCpf(userRequest.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + userRequest.cpf());
        }
        AdminUser adminUser = userMapper.toEntity(userRequest);
        adminUser.setRole(Role.ADMIN);

        return userMapper.toDto(userAdminRepository.save(adminUser));
    }

    @Override
    @Transactional
    public AdminUserResponse update(Long id, AdminUserRequest userRequest) {
        AdminUser existingUser = getUser(id);
        if (!Objects.equals(existingUser.getCpf(), userRequest.cpf())){
            throw new CpfModificationNotAllowedException("Cannot change the CPF of an existing Admin.");
        }
        existingUser.setName(userRequest.name());
        existingUser.setEmail(userRequest.email());
        existingUser.setPassword(userRequest.password());
        existingUser.setRole(Role.ADMIN);

        return userMapper.toDto(userAdminRepository.save(existingUser));
    }

    @Override
    public AdminUserResponse getById(Long id) {
        return userMapper.toDto(
                userAdminRepository.findById(id).orElseThrow(
                        () -> new UserNotFoundException("Admin not found with ID: " + id)
                )
        );
    }

    @Override
    public void delete(Long id) {
        userAdminRepository.delete(getUser(id));
    }

    @Override
    public AdminUserResponse getByEmail(String email) {
        return userMapper.toDto(
                userAdminRepository.findByEmail(email).orElseThrow(
                        () -> new UserNotFoundException("Admin not found with email: " + email)
                )
        );
    }

    @Override
    public AdminUserResponse getByCpf(String cpf) {
        return userMapper.toDto(
                userAdminRepository.findByCpf(cpf).orElseThrow(
                        () -> new UserNotFoundException("Admin not found with CPF: " + cpf)
                )
        );
    }

    private AdminUser getUser(Long id) {
        return userAdminRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Admin not found with ID: " + id)
        );
    }

    @Override
    public VoterResponse createVoter(VoterRequest voterRequest) {
        if (userVotingRepository.existsByCpf(voterRequest.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + voterRequest.cpf());
        }
        var voter = userMapper.toEntity(voterRequest);
        voter.setRole(Role.USER);
        return userMapper.toDto(userVotingRepository.save(voter));
    }
}
