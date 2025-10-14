package github.io.api_voting_challenge.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.controller.UserController;
import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.CpfModificationNotAllowedException;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.TestNoOperationCacheConfig;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("User Controller Integration Tests")
@WebMvcTest({UserController.class, GlobalExceptionHandler.class})
@EnableCaching
@Import(TestNoOperationCacheConfig.class)
@ActiveProfiles("test")
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Deve criar um usuário com sucesso e retornar status 201")
    void shouldCreateUserSuccessfullyAndReturnStatus201() throws Exception {
        UserRequest userRequest = UserFixtures.createValidUserRequest();
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.create(userRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.name").value(userResponse.name()))
                .andExpect(jsonPath("$.cpf").value(userResponse.cpf()));

        verify(userService, times(1)).create(userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status UnProcessable Entity quando criar um usuário com dados inválidos")
    void shouldReturnStatusUnProcessableEntityWhenCreateUserWithInvalidData() throws Exception {
        UserRequest invalidUserRequest = UserFixtures.createInvalidUserRequest();

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Conflict quando criar um usuário com CPF já existente")
    void shouldReturnStatusConflictWhenCreateUserWithExistingCpf() throws Exception {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        when(userService.create(userRequest)).thenThrow(new CpfAlreadyRegisteredException("CPF already registered"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("CPF already registered"));

        verify(userService, times(1)).create(userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso e retornar status 200")
    void shouldUpdateUserSuccessfullyAndReturnStatus200() throws Exception {
        UserRequest userRequest = UserFixtures.createValidUserRequest();
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.update(VALID_ID, userRequest)).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.name").value(userResponse.name()))
                .andExpect(jsonPath("$.cpf").value(userResponse.cpf()));

        verify(userService, times(1)).update(VALID_ID, userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status UnProcessable Entity quando atualizar um usuário com dados inválidos")
    void shouldReturnStatusUnProcessableEntityWhenUpdateUserWithInvalidData() throws Exception {
        UserRequest invalidUserRequest = UserFixtures.createInvalidUserRequest();

        mockMvc.perform(put("/api/v1/users/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Proíbido ao tentar atualizar o CPF de um usuário")
    void shouldReturnStatusForbiddenWhenTryingToUpdateUserCpf() throws Exception {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        when(userService.update(VALID_ID, userRequest))
                .thenThrow(new CpfModificationNotAllowedException("Cannot change the CPF of an existing User."));

        mockMvc.perform(put("/api/v1/users/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Cannot change the CPF of an existing User."));

        verify(userService, times(1)).update(VALID_ID, userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar atualizar um usuário com ID inexistente")
    void shouldReturnStatusNotFoundWhenTryingToUpdateUserWithNonExistentId() throws Exception {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        when(userService.update(INVALID_ID, userRequest))
                .thenThrow(new UserNotFoundException("User not found with ID: " + INVALID_ID));

        mockMvc.perform(put("/api/v1/users/{id}", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + INVALID_ID));

        verify(userService, times(1)).update(INVALID_ID, userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário por ID com sucesso e retornar status 200")
    void shouldGetUserByIdSuccessfullyAndReturnStatus200() throws Exception {
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.getById(VALID_ID)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.name").value(userResponse.name()))
                .andExpect(jsonPath("$.cpf").value(userResponse.cpf()));

        verify(userService, times(1)).getById(VALID_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar recuperar um usuário com ID inexistente")
    void shouldReturnStatusNotFoundWhenTryingToGetUserWithNonExistentId() throws Exception {
        when(userService.getById(INVALID_ID))
                .thenThrow(new UserNotFoundException("User not found with ID: " + INVALID_ID));

        mockMvc.perform(get("/api/v1/users/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + INVALID_ID));

        verify(userService, times(1)).getById(INVALID_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário por CPF com sucesso e retornar status 200")
    void shouldGetUserByCpfSuccessfullyAndReturnStatus200() throws Exception {
        UserResponse userResponse = UserFixtures.createUserResponse();

        when(userService.getByCpf(VALID_CPF)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users")
                        .param("cpf", VALID_CPF)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.name").value(userResponse.name()))
                .andExpect(jsonPath("$.cpf").value(userResponse.cpf()));

        verify(userService, times(1)).getByCpf(VALID_CPF);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar recuperar um usuário com CPF inexistente")
    void shouldReturnStatusNotFoundWhenTryingToGetUserWithNonExistentCpf() throws Exception {
        when(userService.getByCpf(INVALID_CPF))
                .thenThrow(new UserNotFoundException("User not found with CPF: " + INVALID_CPF));

        mockMvc.perform(get("/api/v1/users")
                        .param("cpf", INVALID_CPF)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with CPF: " + INVALID_CPF));

        verify(userService, times(1)).getByCpf(INVALID_CPF);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de usuários com sucesso e retornar status 200")
    void shouldReturnPaginatedListOfUsersSuccessfullyAndReturnStatus200() throws Exception {
        var pageable = Pageable.ofSize(10).withPage(0);
        var userResponsePage = UserFixtures.createUserResponsePage(10, pageable);

        when(userService.getAll(pageable)).thenReturn(userResponsePage);

        mockMvc.perform(get("/api/v1/users/all")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(userResponsePage.getContent().size()))
                .andExpect(jsonPath("$.totalElements").value(userResponsePage.getTotalElements()))
                .andExpect(jsonPath("$.totalPages").value(userResponsePage.getTotalPages()))
                .andExpect(jsonPath("$.number").value(userResponsePage.getNumber()))
                .andExpect(jsonPath("$.size").value(userResponsePage.getSize()));

        verify(userService, times(1)).getAll(pageable);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso e retornar status 204")
    void shouldDeleteUserSuccessfullyAndReturnStatus204() throws Exception {
        doNothing().when(userService).delete(VALID_ID);

        mockMvc.perform(delete("/api/v1/users/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(VALID_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar deletar um usuário com ID inexistente")
    void shouldReturnStatusNotFoundWhenTryingToDeleteUserWithNonExistentId() throws Exception {
        doThrow(new UserNotFoundException("User not found with ID: " + INVALID_ID))
                .when(userService).delete(INVALID_ID);

        mockMvc.perform(delete("/api/v1/users/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + INVALID_ID));

        verify(userService, times(1)).delete(INVALID_ID);
        verifyNoMoreInteractions(userService);
    }
}
