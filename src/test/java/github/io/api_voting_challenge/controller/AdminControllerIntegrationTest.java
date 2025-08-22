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
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createValidAdminUserRequestDto();
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        when(adminService.create(adminUserRequestDto)).thenReturn(adminUserResponseDto);

        mockMvc.perform(post("/api/v1/admin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(adminUserRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(adminUserResponseDto.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponseDto.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponseDto.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponseDto.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponseDto.role().name()));

        verify(adminService).create(adminUserRequestDto);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar Unprocessable Entity ao criar usuário administrador com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingAdminUserWithInvalidData() throws Exception {
        AdminUserRequestDto invalidAdminUserRequestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        mockMvc.perform(post("/api/v1/admin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidAdminUserRequestDto)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("Deve retornar erro Conflict ao criar usuário administrador com CPF já cadastrado")
    void shouldReturnConflictWhenCreatingAdminUserWithExistingCpf() throws Exception {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createValidAdminUserRequestDto();
        when(adminService.create(adminUserRequestDto)).thenThrow(new CpfAlreadyRegisteredException("CPF already registered"));

        mockMvc.perform(post("/api/v1/admin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequestDto)))
                        .andDo(print())
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.message").value("CPF already registered"));

        verify(adminService).create(adminUserRequestDto);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário administrador com sucesso e retornar status 200")
    void shouldUpdateAdminUserSuccessfullyAndReturn200() throws Exception {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createAdminUserUpdateRequestDto();
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDtoAfterUpdate();
        when(adminService.update(VALID_ID, adminUserRequestDto)).thenReturn(adminUserResponseDto);

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(adminUserRequestDto)))
                .andExpect(status().isOk());

        verify(adminService).update(VALID_ID, adminUserRequestDto);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao atualizar usuário administrador com dados inválidos")
    void shouldReturnUnprocessableEntityWhenUpdatingAdminUserWithInvalidData() throws Exception {
        AdminUserRequestDto invalidAdminUserRequestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAdminUserRequestDto)))
                        .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("Deve retornar status Bad Request ao atualizar usuário administrador com CPF diferente do original")
    void shouldReturnBadRequestWhenUpdatingAdminUserWithDifferentCpf() throws Exception {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createAdminUserUpdateRequestDto();
        when(adminService.update(VALID_ID, adminUserRequestDto))
                .thenThrow(new CpfAlreadyRegisteredException("Cannot change the CPF of an existing Admin."));

        mockMvc.perform(put("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequestDto)))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.message").value("Cannot change the CPF of an existing Admin."));

        verify(adminService).update(VALID_ID, adminUserRequestDto);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar atualizar usuário administrador inexistente")
    void shouldReturnNotFoundWhenUpdatingNonExistentAdminUser() throws Exception {
        AdminUserRequestDto adminUserRequestDto = AdminFixtures.createAdminUserUpdateRequestDto();
        when(adminService.update(INVALID_ID, adminUserRequestDto))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(put("/api/v1/admin/{id}", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminUserRequestDto)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).update(INVALID_ID, adminUserRequestDto);
        verifyNoInteractions(votingSessionService, agendaService);
    }

    @Test
    @DisplayName("Deve recuperar um usuário administrador por ID com sucesso e retornar status 200")
    void shouldGetAdminUserByIdSuccessfullyAndReturn200() throws Exception {
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        when(adminService.getById(VALID_ID)).thenReturn(adminUserResponseDto);

        mockMvc.perform(get("/api/v1/admin/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponseDto.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponseDto.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponseDto.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponseDto.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponseDto.role().name()));

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
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        when(adminService.getByEmail(VALID_EMAIL)).thenReturn(adminUserResponseDto);

        mockMvc.perform(get("/api/v1/admin/email")
                        .param("email", VALID_EMAIL)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponseDto.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponseDto.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponseDto.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponseDto.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponseDto.role().name()));

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
        AdminUserResponseDto adminUserResponseDto = AdminFixtures.createAdminUserResponseDto();
        when(adminService.getByCpf(VALID_CPF)).thenReturn(adminUserResponseDto);

        mockMvc.perform(get("/api/v1/admin/cpf")
                        .param("cpf", VALID_CPF)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUserResponseDto.id()))
                .andExpect(jsonPath("$.name").value(adminUserResponseDto.name()))
                .andExpect(jsonPath("$.cpf").value(adminUserResponseDto.cpf()))
                .andExpect(jsonPath("$.email").value(adminUserResponseDto.email()))
                .andExpect(jsonPath("$.role").value(adminUserResponseDto.role().name()));

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
        AgendaRequestDto agendaRequestDto = AgendaFixtures.createValidAgendaRequestDto();
        AgendaResponseDto agendaResponseDto = AgendaFixtures.createAgendaResponseDto();

        when(agendaService.createAgenda(agendaRequestDto, VALID_ID)).thenReturn(agendaResponseDto);

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(agendaResponseDto.id()))
                .andExpect(jsonPath("$.title").value(agendaResponseDto.title()))
                .andExpect(jsonPath("$.description").value(agendaResponseDto.description()))
                .andExpect(jsonPath("$.status").value(agendaResponseDto.status()))
                .andExpect(jsonPath("$.creationDate").value(agendaResponseDto.creationDate().toString()))
                .andExpect(jsonPath("$.createdBy").value(agendaResponseDto.createdBy()));

        verify(agendaService).createAgenda(agendaRequestDto, VALID_ID);
        verifyNoInteractions(adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao criar pauta com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingAgendaWithInvalidData() throws Exception {
        AgendaRequestDto invalidAgendaRequestDto = AgendaFixtures.createInvalidAgendaRequestDto();

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAgendaRequestDto)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(agendaService, adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao criar pauta para usuário administrador inexistente")
    void shouldReturnNotFoundWhenCreatingAgendaForNonExistentAdminUser() throws Exception {
        AgendaRequestDto agendaRequestDto = AgendaFixtures.createValidAgendaRequestDto();
        when(agendaService.createAgenda(agendaRequestDto, INVALID_ID))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/{id}/agenda", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(agendaService).createAgenda(agendaRequestDto, INVALID_ID);
        verifyNoInteractions(adminService, votingSessionService);
    }

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar status 201")
    void shouldCreateVotingSessionSuccessfullyAndReturn201() throws Exception {
        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createValidVotingSessionRequestDto();
        VotingSessionResponseDto votingSessionResponseDto = VotingSessionFixtures.createVotingSessionResponseDto();

        when(votingSessionService.openVotingSession(VALID_ID, votingSessionRequestDto))
                .thenReturn(votingSessionResponseDto);

        mockMvc.perform(post("/api/v1/admin/agenda/{id}/voting-session", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(votingSessionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(votingSessionResponseDto.id()))
                .andExpect(jsonPath("$.durationInMinutes").value(votingSessionResponseDto.durationInMinutes()))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.status").value(votingSessionResponseDto.status().name()))
                .andExpect(jsonPath("$.agendaId").value(votingSessionResponseDto.agendaId()));

    verify(votingSessionService).openVotingSession(VALID_ID, votingSessionRequestDto);
    verifyNoInteractions(adminService, agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao criar sessão de votação para pauta inexistente")
    void shouldReturnNotFoundWhenCreatingVotingSessionForNonExistentAgenda() throws Exception {
        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createValidVotingSessionRequestDto();
        when(votingSessionService.openVotingSession(INVALID_ID, votingSessionRequestDto))
                .thenThrow(new UserNotFoundException("Agenda not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/agenda/{id}/voting-session", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(votingSessionRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Agenda not found with ID: " + INVALID_ID));

        verify(votingSessionService).openVotingSession(INVALID_ID, votingSessionRequestDto);
        verifyNoInteractions(adminService, agendaService);
    }

    @Test
    @DisplayName("Deve criar usuário votante com sucesso e retornar status 201")
    void shouldCreateVoterSuccessfullyAndReturn201() throws Exception {
        VoterRequestDto voterRequestDto = VoterFixtures.createValidVoterRequestDto();
        VoterResponseDto voterResponseDto = VoterFixtures.createVoterResponseDto();

        when(adminService.createVoter(voterRequestDto)).thenReturn(voterResponseDto);

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(voterResponseDto.id()))
                .andExpect(jsonPath("$.name").value(voterResponseDto.name()))
                .andExpect(jsonPath("$.cpf").value(voterResponseDto.cpf()))
                .andExpect(jsonPath("$.role").value(voterResponseDto.role().name()));

        verify(adminService).createVoter(voterRequestDto);
        verifyNoInteractions(agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao criar usuário votante com dados inválidos")
    void shouldReturnUnprocessableEntityWhenCreatingVoterWithInvalidData() throws Exception {
        VoterRequestDto invalidVoterRequestDto = VoterFixtures.createInvalidVoterRequestDto();

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidVoterRequestDto)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(adminService, agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Conflict ao criar usuário votante com CPF já cadastrado")
    void shouldReturnConflictWhenCreatingVoterWithExistingCpf() throws Exception {
        VoterRequestDto voterRequestDto = VoterFixtures.createValidVoterRequestDto();
        when(adminService.createVoter(voterRequestDto))
                .thenThrow(new CpfAlreadyRegisteredException("CPF already registered"));

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("CPF already registered"));

        verify(adminService).createVoter(voterRequestDto);
        verifyNoInteractions(agendaService, votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar criar usuário votante para administrador inexistente")
    void shouldReturnNotFoundWhenCreatingVoterForNonExistentAdminUser() throws Exception {
        VoterRequestDto voterRequestDto = VoterFixtures.createValidVoterRequestDto();
        when(adminService.createVoter(voterRequestDto))
                .thenThrow(new UserNotFoundException("Admin not found with ID: " + INVALID_ID));

        mockMvc.perform(post("/api/v1/admin/voters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voterRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Admin not found with ID: " + INVALID_ID));

        verify(adminService).createVoter(voterRequestDto);
        verifyNoInteractions(agendaService, votingSessionService);
    }
}