package github.io.api_voting_challenge.unit.service;

import github.io.api_voting_challenge.dto.UserRequest;
import github.io.api_voting_challenge.dto.UserResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.mapper.UserMapper;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Impl Unit Tests")
public class UserServiceImplUnitTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();
        User userEntity = UserFixtures.createValidUserEntity();
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userRepository.existsByCpf(userRequest.cpf())).thenReturn(false);
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.create(userRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).existsByCpf(userRequest.cpf());
        verify(userRepository).save(userEntity);
        verify(userMapper).toEntity(userRequest);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário com CPF já cadastrado")
    void shouldThrowExceptionWhenCreatingUserWithExistingCpf() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        when(userRepository.existsByCpf(userRequest.cpf())).thenReturn(true);

        CpfAlreadyRegisteredException exception = assertThrows(
                CpfAlreadyRegisteredException.class, () -> {
                    userService.create(userRequest);
                });

        String message = String.format("CPF already registered: %s", userRequest.cpf());
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        UserRequest userRequestToUpdate = UserFixtures.createValidUserRequest();        User userEntity = UserFixtures.createValidUserEntity();
        UserResponse updatedUserResponse = UserFixtures.createUpdatedUserResponse();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(updatedUserResponse);

        UserResponse result = userService.update(VALID_ID, userRequestToUpdate);

        assertNotNull(result);
        assertEquals(updatedUserResponse, result);
        verify(userRepository).findById(VALID_ID);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário com CPF diferente do existente")
    void shouldThrowExceptionWhenUpdatingUserWithDifferentCpf() {
        UserRequest userRequestToUpdate = UserFixtures.createUserUpdateRequest();
        User userEntity = UserFixtures.createValidUserEntity();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(userEntity));
        Exception exception = assertThrows(
                CpfModificationNotAllowedException.class, () -> {
                    userService.update(VALID_ID, userRequestToUpdate);
                });

        String message = "Cannot change the CPF of an existing User.";
        assertEquals(message, exception.getMessage());

        verify(userRepository).findById(VALID_ID);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário com id inválido")
    void shouldThrowExceptionWhenUpdatingUserWithInvalidId() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        when(userRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    userService.update(INVALID_ID, userRequest);
                });

        String message = String.format("User not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());

        verify(userRepository).findById(INVALID_ID);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldGetUserByIdSuccessfully() {
        UserResponse userResponse = UserFixtures.createUserResponse();
        User userEntity = UserFixtures.createValidUserEntity();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.getById(VALID_ID);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findById(VALID_ID);
        verify(userMapper).toDto(userEntity);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário por ID inválido")
    void shouldThrowExceptionWhenGettingUserByInvalidId() {
        when(userRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    userService.getById(INVALID_ID);
                });
        String message = String.format("User not found with ID: %d", INVALID_ID);
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF com sucesso")
    void shouldGetUserByCpfSuccessfully() {
        UserResponse userResponse = UserFixtures.createUserResponse();
        User userEntity = UserFixtures.createValidUserEntity();

        when(userRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.getByCpf(VALID_CPF);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findByCpf(VALID_CPF);
        verify(userMapper).toDto(userEntity);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário por CPF inválido")
    void shouldThrowExceptionWhenGettingUserByInvalidCpf() {
        when(userRepository.findByCpf(INVALID_CPF)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    userService.getByCpf(INVALID_CPF);
                });
        String message = "User not found with CPF: "+ INVALID_CPF;
        assertEquals(message, exception.getMessage());

        verify(userRepository).findByCpf(INVALID_CPF);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de usuários com sucesso")
    void shouldGetAllUsersSuccessfully() {
        Pageable pageable = PageRequest.of(0, 5);

        List<User> entityList = UserFixtures.createEntityUserResponseList(5);
        Page<User> userPage = new PageImpl<>(entityList, pageable, entityList.size());

        List<UserResponse> responseList = UserFixtures.createUserResponseList(5);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        for (int i = 0; i < entityList.size(); i++) {
            when(userMapper.toDto(entityList.get(i))).thenReturn(responseList.get(i));
        }

        Page<UserResponse> result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertThat(result.getContent()).isEqualTo(responseList);

        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(entityList.size())).toDto(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        User userEntity = UserFixtures.createValidUserEntity();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(userEntity);

        userService.delete(VALID_ID);

        verify(userRepository).findById(VALID_ID);
        verify(userRepository).delete(userEntity);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário admin com ID inválido")
    void shouldThrowExceptionWhenDeletingAdminUserWithInvalidId() {
        when(userRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                UserNotFoundException.class, () -> {
                    userService.delete(INVALID_ID);
                });
        String message = "User not found with ID: " + INVALID_ID;
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}

