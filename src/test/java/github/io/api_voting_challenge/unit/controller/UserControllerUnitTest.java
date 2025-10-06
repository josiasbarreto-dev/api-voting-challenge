package github.io.api_voting_challenge.unit.controller;

import github.io.api_voting_challenge.controller.UserController;
import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller Unit Tests")
public class UserControllerUnitTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Deve criar um usuário com sucesso e retornar o status 201")
    void shouldCreateUserSuccessfullyAndReturnStatus201() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.create(userRequest)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.create(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar um usuário com dados inválidos")
    void shouldThrowExceptionWhenCreatingUserWithInvalidData() {
        UserRequest invalidUserRequest = UserFixtures.createInvalidUserRequest();

        String errorMensage = "Validation failed for one or more fields";
        when(userService.create(invalidUserRequest)).thenThrow(new IllegalArgumentException(errorMensage));
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> userController.create(invalidUserRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso e retornar o status 200")
    void shouldUpdateUserSuccessfullyAndReturnStatus200(){
        UserRequest userRequest = UserFixtures.createUserUpdateRequest();
        UserResponse userResponse = UserFixtures.createUpdatedUserResponse();

        when(userService.update(VALID_ID, userRequest)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.update(VALID_ID, userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).update(VALID_ID, userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um usuário inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentUser(){
        UserRequest userRequest = UserFixtures.createUserUpdateRequest();

        String errorMensage = "User not found with id: " + INVALID_ID;
        when(userService.update(INVALID_ID, userRequest)).thenThrow(new IllegalArgumentException(errorMensage));
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> userController.update(INVALID_ID, userRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID com sucesso e retornar o status 200")
    void shouldGetUserByIdSuccessfullyAndReturnStatus200() {
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.getById(VALID_ID)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).getById(VALID_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar um usuário por ID inexistente")
    void shouldThrowExceptionWhenGettingUserByNonExistentId() {
        String errorMensage = "User not found with id: " + INVALID_ID;
        when(userService.getById(INVALID_ID)).thenThrow(new UserNotFoundException(errorMensage));
        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> userController.getById(INVALID_ID)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve buscar um usuário por CPF com sucesso e retornar o status 200")
    void shouldGetUserByCpfSuccessfullyAndReturnStatus200() {
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.getByCpf(VALID_CPF)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.getByCpf(VALID_CPF);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).getByCpf(VALID_CPF);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar um usuário por CPF inexistente")
    void shouldThrowExceptionWhenGettingUserByNonExistentCpf() {
        String errorMensage = "User not found with CPF: " + INVALID_CPF;
        when(userService.getByCpf(INVALID_CPF)).thenThrow(new UserNotFoundException(errorMensage));
        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> userController.getByCpf(INVALID_CPF)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de usuários com sucesso e retornar o status 200")
    void shouldListUsersSuccessfullyAndReturnStatus200() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponse> userPage = UserFixtures.createUserResponsePage(15, pageable);

        when(userService.getAll(pageable)).thenReturn(userPage);
        ResponseEntity<Page<UserResponse>> response = userController.list(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userPage, response.getBody());
        verify(userService).getAll(pageable);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso e retornar o status 204")
    void shouldDeleteUserSuccessfullyAndReturnStatus204() {
        doNothing().when(userService).delete(VALID_ID);
        ResponseEntity<Void> response = userController.delete(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).delete(VALID_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        String errorMensage = "User not found with id: " + INVALID_ID;
        doThrow(new UserNotFoundException(errorMensage)).when(userService).delete(INVALID_ID);
        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> userController.delete(INVALID_ID)
        );

        assertEquals(errorMensage, exception.getMessage());
        verify(userService).delete(INVALID_ID);
        verifyNoMoreInteractions(userService);
    }
}
