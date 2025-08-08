package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.enums.Role;
import github.io.api_voting_challenge.repository.UserAdminRepository;
import github.io.api_voting_challenge.repository.UserVotingRepository;
import github.io.api_voting_challenge.service.AdminServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminServiceInterface {
    private final UserAdminRepository userAdminRepository;
    private final UserVotingRepository userVotingRepository;
    private final UserMapper userMapper;

    public AdminServiceImpl(UserAdminRepository userAdminRepository, UserMapper userMapper, UserVotingRepository userVotingRepository) {
        this.userAdminRepository = userAdminRepository;
        this.userVotingRepository = userVotingRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public AdminUserResponseDto create(AdminUserRequestDto userRequestDto) {
        if (userAdminRepository.existsByCpf(userRequestDto.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + userRequestDto.cpf());
        }
        AdminUser adminUser = userMapper.toEntity(userRequestDto);
        adminUser.setRole(Role.ADMIN);

        return userMapper.toDto(userAdminRepository.save(adminUser));
    }

    @Override
    @Transactional
    public AdminUserResponseDto update(Long id, AdminUserRequestDto userRequestDto) {
        AdminUser existingUser = getUser(id);
        if (!Objects.equals(existingUser.getCpf(), userRequestDto.cpf())){
            throw new CpfModificationNotAllowedException("Cannot change the CPF of an existing Admin.");
        }
        existingUser.setName(userRequestDto.name());
        existingUser.setEmail(userRequestDto.email());
        existingUser.setPassword(userRequestDto.password());
        existingUser.setRole(Role.ADMIN);

        return userMapper.toDto(userAdminRepository.save(existingUser));
    }

    @Override
    public AdminUserResponseDto getById(Long id) {
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
    public AdminUserResponseDto getByEmail(String email) {
        return userMapper.toDto(
                userAdminRepository.findByEmail(email).orElseThrow(
                        () -> new UserNotFoundException("Admin not found with email: " + email)
                )
        );
    }

    @Override
    public AdminUserResponseDto getByCpf(String cpf) {
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
    public VoterResponseDto createVoter(VoterRequestDto voterRequestDto) {
        if (userVotingRepository.existsByCpf(voterRequestDto.cpf())) {
            throw new CpfAlreadyRegisteredException("CPF already registered: " + voterRequestDto.cpf());
        }
        var voter = userMapper.toEntity(voterRequestDto);
        voter.setRole(Role.USER);
        return userMapper.toDto(userVotingRepository.save(voter));
    }

}
