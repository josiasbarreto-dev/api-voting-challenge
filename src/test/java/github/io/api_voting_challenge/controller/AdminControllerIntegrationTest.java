package github.io.api_voting_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.AdminFixtures;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VoterFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.service.AdminServiceInterface;
import github.io.api_voting_challenge.service.AgendaServiceInterface;
import github.io.api_voting_challenge.service.VotingSessionServiceInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminController.class, GlobalExceptionHandler.class})
public class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminServiceInterface adminService;

    @MockitoBean
    private VotingSessionServiceInterface votingSessionService;

    @MockitoBean
    private AgendaServiceInterface agendaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um usuário administrador com sucesso e retornar status 201")
    void shouldCreateAdminUserSuccessfullyAndReturn201() throws Exception {
        AdminUserRequest adminUserRequest = AdminFixtures.createValidAdminUserRequest();
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        when(adminService.create(adminUserRequest)).thenReturn(adminUserResponse);

        mockMvc.perform(post("/api/v1/admin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(adminUserResponse.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponse.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponse.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponse.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponse.role().name()));

        verify(adminService).create(adminUserRequest);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar Unprocessable Entity ao criar usuário administrador com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingAdminUserWithInvalidData() throws Exception {
        AdminUserRequest invalidAdminUserRequest = AdminFixtures.createInvalidAdminUserRequest();

        mockMvc.perform(post("/api/v1/admin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidAdminUserRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("Deve retornar erro Conflict ao criar usuário administrador com CPF já cadastrado")
    void shouldReturnConflictWhenCreatingAdminUserWithExistingCpf() throws Exception {
        AdminUserRequest adminUserRequest = AdminFixtures.createValidAdminUserRequest();
        when(adminService.create(adminUserRequest)).thenThrow(new CpfAlreadyRegisteredException("CPF already registered"));

        mockMvc.perform(post("/api/v1/admin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                        .andDo(print())
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.message").value("CPF already registered"));

        verify(adminService).create(adminUserRequest);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário administrador com sucesso e retornar status 200")
    void shouldUpdateAdminUserSuccessfullyAndReturn200() throws Exception {
        AdminUserRequest adminUserRequest = AdminFixtures.createAdminUserUpdateRequest();
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponseAfterUpdate();
        when(adminService.update(VALID_ID, adminUserRequest)).thenReturn(adminUserResponse);

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isOk());

        verify(adminService).update(VALID_ID, adminUserRequest);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao atualizar usuário administrador com dados inválidos")
    void shouldReturnUnprocessableEntityWhenUpdatingAdminUserWithInvalidData() throws Exception {
        AdminUserRequest invalidAdminUserRequest = AdminFixtures.createInvalidAdminUserRequest();

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAdminUserRequest)))
                        .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("Deve retornar status Bad Request ao atualizar usuário administrador com CPF diferente do original")
    void shouldReturnBadRequestWhenUpdatingAdminUserWithDifferentCpf() throws Exception {
        AdminUserRequest adminUserRequest = AdminFixtures.createAdminUserUpdateRequest();
        when(adminService.update(VALID_ID, adminUserRequest))
                .thenThrow(new CpfAlreadyRegisteredException("Cannot change the CPF of an existing Admin."));

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.message").value("Cannot change the CPF of an existing Admin."));

        verify(adminService).update(VALID_ID, adminUserRequest);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar atualizar usuário administrador inexistente")
    void shouldReturnNotFoundWhenUpdatingNonExistentAdminUser() throws Exception {
        AdminUserRequest adminUserRequest = AdminFixtures.createAdminUserUpdateRequest();
        when(adminService.update(INVALID_ID, adminUserRequest))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(put("/api/v1/admin/{id}", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).update(INVALID_ID, adminUserRequest);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário administrador por ID com sucesso e retornar status 200")
    void shouldGetAdminUserByIdSuccessfullyAndReturn200() throws Exception {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        when(adminService.getById(VALID_ID)).thenReturn(adminUserResponse);

        mockMvc.perform(get("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponse.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponse.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponse.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponse.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponse.role().name()));

        verify(adminService).getById(VALID_ID);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar recuperar usuário administrador inexistente por ID")
    void shouldReturnNotFoundWhenGettingNonExistentAdminUserById() throws Exception {
        when(adminService.getById(INVALID_ID))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(get("/api/v1/admin/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).getById(INVALID_ID);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve excluir um usuário administrador com sucesso e retornar status 204")
    void shouldDeleteAdminUserSuccessfullyAndReturn204() throws Exception {
        doNothing().when(adminService).delete(VALID_ID);

        mockMvc.perform(delete("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(adminService).delete(VALID_ID);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar excluir usuário administrador inexistente")
    void shouldReturnNotFoundWhenDeletingNonExistentAdminUser() throws Exception {
        doThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID))
                .when(adminService).delete(INVALID_ID);

        mockMvc.perform(delete("/api/v1/admin/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).delete(INVALID_ID);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário administrador por email com sucesso e retornar status 200")
    void shouldGetAdminUserByEmailSuccessfullyAndReturn200() throws Exception {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        when(adminService.getByEmail(VALID_EMAIL)).thenReturn(adminUserResponse);

        mockMvc.perform(get("/api/v1/admin/email")
                        .param("email", VALID_EMAIL)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponse.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponse.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponse.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponse.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponse.role().name()));

        verify(adminService).getByEmail(VALID_EMAIL);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar recuperar usuário administrador inexistente por email")
    void shouldReturnNotFoundWhenGettingNonExistentAdminUserByEmail() throws Exception {
        when(adminService.getByEmail(INVALID_EMAIL))
                .thenThrow(new UserNotFoundException("Admin not found with email: " + INVALID_EMAIL));

        mockMvc.perform(get("/api/v1/admin/email")
                        .param("email", INVALID_EMAIL)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with email: " + INVALID_EMAIL));

        verify(adminService).getByEmail(INVALID_EMAIL);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário administrador por CPF com sucesso e retornar status 200")
    void shouldGetAdminUserByCpfSuccessfullyAndReturn200() throws Exception {
        AdminUserResponse adminUserResponse = AdminFixtures.createAdminUserResponse();
        when(adminService.getByCpf(VALID_CPF)).thenReturn(adminUserResponse);

        mockMvc.perform(get("/api/v1/admin/cpf")
                        .param("cpf", VALID_CPF)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponse.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponse.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponse.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponse.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponse.role().name()));

        verify(adminService).getByCpf(VALID_CPF);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar recuperar usuário administrador inexistente por CPF")
    void shouldReturnNotFoundWhenGettingNonExistentAdminUserByCpf() throws Exception {
        when(adminService.getByCpf(INVALID_CPF))
                .thenThrow(new UserNotFoundException("Admin not found with CPF: " + INVALID_CPF));

        mockMvc.perform(get("/api/v1/admin/cpf")
                        .param("cpf", INVALID_CPF)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with CPF: " + INVALID_CPF));

        verify(adminService).getByCpf(INVALID_CPF);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar status 201")
    void shouldCreateAgendaSuccessfullyAndReturn201() throws Exception {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.createAgenda(agendaRequest, VALID_ID)).thenReturn(agendaResponse);

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(agendaResponse.id()))
                .andExpect(jsonPath("$.title").value(agendaResponse.title()))
                .andExpect(jsonPath("$.description").value(agendaResponse.description()))
                .andExpect(jsonPath("$.status").value(agendaResponse.status()))
                .andExpect(jsonPath("$.creationDate").value(agendaResponse.creationDate().toString()))
                .andExpect(jsonPath("$.createdBy").value(agendaResponse.createdBy()));

        verify(agendaService).createAgenda(agendaRequest, VALID_ID);
        verifyNoInteractions(adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao criar pauta com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingAgendaWithInvalidData() throws Exception {
        AgendaRequest invalidAgendaRequest = AgendaFixtures.createInvalidAgendaRequest();

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAgendaRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(agendaService, adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao criar pauta para usuário administrador inexistente")
    void shouldReturnNotFoundWhenCreatingAgendaForNonExistentAdminUser() throws Exception {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        when(agendaService.createAgenda(agendaRequest, INVALID_ID))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(agendaService).createAgenda(agendaRequest, INVALID_ID);
        verifyNoInteractions(adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar status 201")
    void shouldCreateVotingSessionSuccessfullyAndReturn201() throws Exception {
        VotingSessionRequest votingSessionRequest = VotingSessionFixtures.createValidVotingSessionRequest();
        VotingSessionResponse votingSessionResponse = VotingSessionFixtures.createVotingSessionResponse();

        when(votingSessionService.openVotingSession(VALID_ID, votingSessionRequest))
                .thenReturn(votingSessionResponse);

        mockMvc.perform(post("/api/v1/admin/agenda/{id}/voting-session", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(votingSessionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(votingSessionResponse.id()))
                .andExpect(jsonPath("$.durationInMinutes").value(votingSessionResponse.durationInMinutes()))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.status").value(votingSessionResponse.status().name()))
                .andExpect(jsonPath("$.agendaId").value(votingSessionResponse.agendaId()));

    verify(votingSessionService).openVotingSession(VALID_ID, votingSessionRequest);
    verifyNoInteractions(adminService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao criar sessão de votação para pauta inexistente")
    void shouldReturnNotFoundWhenCreatingVotingSessionForNonExistentAgenda() throws Exception {
        VotingSessionRequest votingSessionRequest = VotingSessionFixtures.createValidVotingSessionRequest();
        when(votingSessionService.openVotingSession(INVALID_ID, votingSessionRequest))
                .thenThrow(new UserNotFoundException("Agenda not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/agenda/{id}/voting-session", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(votingSessionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Agenda not found with ID: " + INVALID_ID));

        verify(votingSessionService).openVotingSession(INVALID_ID, votingSessionRequest);
        verifyNoInteractions(adminService, agendaService);
    }

    @Test
    @DisplayName("Deve criar usuário votante com sucesso e retornar status 201")
    void shouldCreateVoterSuccessfullyAndReturn201() throws Exception {
        VoterRequest voterRequest = VoterFixtures.createValidVoterRequest();
        VoterResponse voterResponse = VoterFixtures.createVoterResponse();

        when(adminService.createVoter(voterRequest)).thenReturn(voterResponse);

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(voterResponse.id()))
                .andExpect(jsonPath("$.name").value(voterResponse.name()))
                .andExpect(jsonPath("$.cpf").value(voterResponse.cpf()))
                .andExpect(jsonPath("$.role").value(voterResponse.role().name()));

        verify(adminService).createVoter(voterRequest);
        verifyNoInteractions(agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao criar usuário votante com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingVoterWithInvalidData() throws Exception {
        VoterRequest invalidVoterRequest = VoterFixtures.createInvalidVoterRequest();

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidVoterRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService, agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Conflict ao criar usuário votante com CPF já cadastrado")
    void shouldReturnConflictWhenCreatingVoterWithExistingCpf() throws Exception {
        VoterRequest voterRequest = VoterFixtures.createValidVoterRequest();
        when(adminService.createVoter(voterRequest))
                .thenThrow(new CpfAlreadyRegisteredException("CPF already registered"));

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("CPF already registered"));

        verify(adminService).createVoter(voterRequest);
        verifyNoInteractions(agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar criar usuário votante para administrador inexistente")
    void shouldReturnNotFoundWhenCreatingVoterForNonExistentAdminUser() throws Exception {
        VoterRequest voterRequest = VoterFixtures.createValidVoterRequest();
        when(adminService.createVoter(voterRequest))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).createVoter(voterRequest);
        verifyNoInteractions(agendaService, votingSessionService);
    }
}