package github.io.api_voting_challenge.unit.impl;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.service.impl.AdminServiceImpl;
import github.io.api_voting_challenge.unit.fixtures.AdminFixtures;
import github.io.api_voting_challenge.unit.fixtures.VoterFixtures;
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

import static github.io.api_voting_challenge.unit.fixtures.TestConstants.*;
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
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createValidAdminUserRequestDto();

        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();
        adminUserEntity.setRole(Role.ADMIN);

        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();

        when(userAdminRepository.existsByCpf(adminUserRequestDto.cpf())).thenReturn(false);
        when(userMapper.toEntity(adminUserRequestDto)).thenReturn(adminUserEntity);
        when(userAdminRepository.save(adminUserEntity)).thenReturn(adminUserEntity);
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponseDto);

        var result = adminServiceImpl.create(adminUserRequestDto);

        assertNotNull(result);
        assertEquals(adminUserResponseDto, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário admin com CPF já cadastrado")
    void shouldThrowExceptionWhenCreatingAdminUserWithExistingCpf() {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createValidAdminUserRequestDto();

        when(userAdminRepository.existsByCpf(adminUserRequestDto.cpf())).thenReturn(true);
        CpfAlreadyRegisteredException exception = assertThrows(
                CpfAlreadyRegisteredException.class, () -> {
                    adminServiceImpl.create(adminUserRequestDto);
                });

        String message = String.format("CPF already registered: %s", adminUserRequestDto.cpf());
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve atualizar um usuário admin com sucesso")
    void shouldUpdateAdminUserSuccessfully() {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createAdminUserUpdateRequestDto();

        AdminUser existingAdminUser = AdminFixtures.createValidAdminUserEntity();

        AdminUserResponseDto updatedAdminUserResponseDto = AdminFixtures.createAdminUserResponseDtoAfterUpdate();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdminUser));
        when(userAdminRepository.save(existingAdminUser)).thenReturn(existingAdminUser);
        when(userMapper.toDto(existingAdminUser)).thenReturn(updatedAdminUserResponseDto);

        AdminUserResponseDto result = adminServiceImpl.update(VALID_ID, adminUserRequestDto);

        assertNotNull(result);
        assertEquals(updatedAdminUserResponseDto, result);
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário admin com CPF diferente do existente")
    void shouldThrowExceptionWhenUpdatingAdminUserWithDifferentCpf() {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        AdminUser existingAdminUser = AdminFixtures.buildValidAdminUserEntity().build();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdminUser));
        Exception exception = assertThrows(
                CpfModificationNotAllowedException.class, () -> {
                    adminServiceImpl.update(VALID_ID, adminUserRequestDto);
                });
        String message = "Cannot change the CPF of an existing Admin.";
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário admin com id inválido")
    void shouldThrowExceptionWhenUpdatingAdminUserWithInvalidId() {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createValidAdminUserRequestDto();

        when(userAdminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    adminServiceImpl.update(INVALID_ID, adminUserRequestDto);
                });
        String message = String.format("Admin not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userAdminRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário admin por ID com sucesso")
    void shouldGetAdminUserByIdSuccessfully() {
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponseDto);

        AdminUserResponseDto result = adminServiceImpl.getById(VALID_ID);

        assertNotNull(result);
        assertEquals(adminUserResponseDto, result);
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
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponseDto);

        AdminUserResponseDto result = adminServiceImpl.getByEmail(VALID_EMAIL);

        assertNotNull(result);
        assertEquals(adminUserResponseDto, result);
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
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        AdminUser adminUserEntity = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(adminUserEntity));
        when(userMapper.toDto(adminUserEntity)).thenReturn(adminUserResponseDto);

        AdminUserResponseDto result = adminServiceImpl.getByCpf(VALID_CPF);

        assertNotNull(result);
        assertEquals(adminUserResponseDto, result);
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
        VoterRequestDto voterRequestDto = VoterFixtures.createValidVoterRequestDto();

        when(userVotingRepository.existsByCpf(voterRequestDto.cpf())).thenReturn(false);

        VotingUser voterUserEntity = VoterFixtures.createValidVotingUserEntity();
        voterUserEntity.setRole(Role.USER);

        VoterResponseDto voterResponseDto = VoterFixtures.createVoterResponseDto();

        when(userMapper.toEntity(voterRequestDto)).thenReturn(voterUserEntity);
        when(userVotingRepository.save(voterUserEntity)).thenReturn(voterUserEntity);
        when(userMapper.toDto(voterUserEntity)).thenReturn(voterResponseDto);

        var result = adminServiceImpl.createVoter(voterRequestDto);

        assertNotNull(result);
        assertEquals(voterResponseDto, result);
        verifyNoMoreInteractions(userVotingRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário votante com CPF já cadastrado")
    void shouldThrowExceptionWhenCreatingVotingUserWithExistingCpf() {
        VoterRequestDto voterRequestDto = VoterFixtures.createValidVoterRequestDto();

        when(userVotingRepository.existsByCpf(voterRequestDto.cpf())).thenReturn(true);
        CpfAlreadyRegisteredException exception = assertThrows(
                CpfAlreadyRegisteredException.class, () -> {
                    adminServiceImpl.createVoter(voterRequestDto);
                });

        String message = String.format("CPF already registered: %s", voterRequestDto.cpf());
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userVotingRepository, userMapper);
    }
}

