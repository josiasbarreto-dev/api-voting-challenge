package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AdminUserRequest;
import github.io.api_voting_challenge.dto.AdminUserResponse;
import github.io.api_voting_challenge.dto.VoterRequest;
import github.io.api_voting_challenge.dto.VoterResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.AdminFixtures;
import github.io.api_voting_challenge.fixtures.VoterFixtures;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.VotingUser;
import github.io.api_voting_challenge.model.enums.Role;
import github.io.api_voting_challenge.repository.UserAdminRepository;
import github.io.api_voting_challenge.repository.UserVotingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl Tests")
public class AdminServiceImplTest {
    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    @Mock
    private UserAdminRepository userAdminRepository;

    @Mock
    private UserVotingRepository userVotingRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Deve criar um usuário admin com sucesso")
    void shouldCreateAdminUserSuccessfully() {
        AdminUserRequest adminUserRequest = AdminFixtures.createValidAdminUserRequest();

        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();
        adminUserEntity.setRole(Role.ADMIN);

        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();

        when(userAdminRepository.existsByCpf(adminUserRequest.cpf())).thenReturn(false);
        when(userAdminRepository.save(adminUserEntity)).thenReturn(adminUserEntity);
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponse);

        var result = adminServiceImpl.create(adminUserRequest);

        assertNotNull(result);
        assertEquals(adminUserResponse, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário admin com CPF já cadastrado")
    void shouldThrowExceptionWhenCreatingAdminUserWithExistingCpf() {
        AdminUserRequest adminUserRequest = AdminFixtures.createValidAdminUserRequest();

        when(userAdminRepository.existsByCpf(adminUserRequest.cpf())).thenReturn(true);
        CpfAlreadyRegisteredException exception = assertThrows(
                CpfAlreadyRegisteredException.class, () -> {
                    adminServiceImpl.create(adminUserRequest);
                });

        String message = String.format("CPF already registered: %s", adminUserRequest.cpf());
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve atualizar um usuário admin com sucesso")
    void shouldUpdateAdminUserSuccessfully() {
        AdminUserRequest adminUserRequest = AdminFixtures.createAdminUserUpdateRequest();

        AdminUser existingAdminUser = AdminFixtures.createValidAdminUserEntity();

        AdminUserResponse updatedAdminUserResponse = AdminFixtures.createAdminUserResponseAfterUpdate();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdminUser));
        when(userAdminRepository.save(existingAdminUser)).thenReturn(existingAdminUser);
        when(userMapper.toDto(existingAdminUser)).thenReturn(updatedAdminUserResponse);

        AdminUserResponse result = adminServiceImpl.update(VALID_ID, adminUserRequest);

        assertNotNull(result);
        assertEquals(updatedAdminUserResponse, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário admin com CPF diferente do existente")
    void shouldThrowExceptionWhenUpdatingAdminUserWithDifferentCpf() {
        AdminUserRequest adminUserRequest = AdminFixtures.createInvalidAdminUserRequest();

        AdminUser existingAdminUser = AdminFixtures.buildValidAdminUserEntity().build();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdminUser));
        Exception exception = assertThrows(
                CpfModificationNotAllowedException.class, () -> {
                    adminServiceImpl.update(VALID_ID, adminUserRequest);
                });
        String message = "Cannot change the CPF of an existing Admin.";
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário admin com id inválido")
    void shouldThrowExceptionWhenUpdatingAdminUserWithInvalidId() {
        AdminUserRequest adminUserRequest = AdminFixtures.createValidAdminUserRequest();

        when(userAdminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.update(INVALID_ID, adminUserRequest);
                });
        String message = String.format("Admin not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário admin por ID com sucesso")
    void shouldGetAdminUserByIdSuccessfully() {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponse);

        AdminUserResponse result = adminServiceImpl.getById(VALID_ID);

        assertNotNull(result);
        assertEquals(adminUserResponse, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário admin por ID inválido")
    void shouldThrowExceptionWhenGettingAdminUserByInvalidId() {
        when(userAdminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.getById(INVALID_ID);
                });
        String message = String.format("Admin not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve deletar usuário admin com sucesso")
    void shouldDeleteAdminUserSuccessfully() {
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(adminUserEntity));
        doNothing().when(userAdminRepository).delete(adminUserEntity);

        adminServiceImpl.delete(VALID_ID);

        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário admin com ID inválido")
    void shouldThrowExceptionWhenDeletingAdminUserWithInvalidId() {
        when(userAdminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.delete(INVALID_ID);
                });
        String message = String.format("Admin not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário admin por email com sucesso")
    void shouldGetAdminUserByEmailSuccessfully() {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponse);

        AdminUserResponse result = adminServiceImpl.getByEmail(VALID_EMAIL);

        assertNotNull(result);
        assertEquals(adminUserResponse, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário admin por email inválido")
    void shouldThrowExceptionWhenGettingAdminUserByInvalidEmail() {
        when(userAdminRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.getByEmail(INVALID_EMAIL);
                });
        String message = String.format("Admin not found with email: %s", INVALID_EMAIL);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário admin por CPF com sucesso")
    void shouldGetAdminUserByCpfSuccessfully() {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponse);

        AdminUserResponse result = adminServiceImpl.getByCpf(VALID_CPF);

        assertNotNull(result);
        assertEquals(adminUserResponse, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário admin por CPF inválido")
    void shouldThrowExceptionWhenGettingAdminUserByInvalidCpf() {
        when(userAdminRepository.findByCpf(INVALID_CPF)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.getByCpf(INVALID_CPF);
                });
        String message = String.format("Admin not found with CPF: %s", INVALID_CPF);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve criar um usuário votante com sucesso")
    void shouldCreateVotingUserSuccessfully() {
        VoterRequest voterRequest = VoterFixtures.createValidVoterRequest();

        when(userVotingRepository.existsByCpf(voterRequest.cpf())).thenReturn(false);

        VotingUser voterUserEntity = VoterFixtures.createValidVotingUserEntity();
        voterUserEntity.setRole(Role.USER);

        VoterResponse voterResponse = VoterFixtures.createVoterResponse();

        when(userMapper.toEntity(voterRequest)).thenReturn(voterUserEntity);
        when(userVotingRepository.save(voterUserEntity)).thenReturn(voterUserEntity);
        when(userMapper.toDto(voterUserEntity)).thenReturn(voterResponse);

        var result = adminServiceImpl.createVoter(voterRequest);

        assertNotNull(result);
        assertEquals(voterResponse, result);
        verifyNoMoreInteractions(userVotingRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário votante com CPF já cadastrado")
    void shouldThrowExceptionWhenCreatingVotingUserWithExistingCpf() {
        VoterRequest voterRequest = VoterFixtures.createValidVoterRequest();

        when(userVotingRepository.existsByCpf(voterRequest.cpf())).thenReturn(true);
        CpfAlreadyRegisteredException exception = assertThrows(
                CpfAlreadyRegisteredException.class, () -> {
                    adminServiceImpl.createVoter(voterRequest);
                });

        String message = String.format("CPF already registered: %s", voterRequest.cpf());
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userVotingRepository, userMapper);
    }
}

