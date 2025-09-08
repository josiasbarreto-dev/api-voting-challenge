package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.AdminFixtures;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VoterFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.service.AdminService;
import github.io.api_voting_challenge.service.AgendaService;
import github.io.api_voting_challenge.service.VotingSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Controller Test")
public class AdminControllerTest {
    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Mock
    private AgendaService agendaService;

    @Mock
    private VotingSessionService votingSessionService;

    @Test
    @DisplayName("Deve criar um usuário administrador com sucesso e retornar o status 201")
    void shouldCreateAdminUserSuccessfully() {
        AdminUserRequest request = AdminFixtures.createValidAdminUserRequest();
        AdminUserResponse responseFixture = AdminFixtures.createAdminUserResponse();

        when(adminService.create(request)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponse> response = adminController.create(request);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com nome inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidName() {
        AdminUserRequest request = AdminFixtures.createInvalidAdminUserRequest();

        String message = "Invalid name";
        when(adminService.create(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com CPF inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidCpf() {
        AdminUserRequest request = AdminFixtures.createInvalidAdminUserRequest();

        String message = "Invalid CPF";
        when(adminService.create(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com email inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidEmail() {
        AdminUserRequest request = AdminFixtures.createInvalidAdminUserRequest();

        String message = "Invalid email";
        when(adminService.create(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com senha inválida")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidPassword() {
        AdminUserRequest request = AdminFixtures.createInvalidAdminUserRequest();

        String message = "Invalid password";
        when(adminService.create(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário administrador com sucesso e retornar o status 200")
    void shouldUpdateAdminUserSuccessfully() {
        AdminUserRequest request = AdminFixtures.createAdminUserUpdateRequest();
        AdminUserResponse responseFixture = AdminFixtures.createAdminUserResponseAfterUpdate();

        when(adminService.update(VALID_ID, request)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponse> response = adminController.update(VALID_ID, request);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminService).update(VALID_ID, request);
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar um usuário administrador inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentAdminUser() {
        AdminUserRequest request = AdminFixtures.createAdminUserUpdateRequest();

        when(adminService.update(INVALID_ID, request)).thenThrow(new IllegalArgumentException(MESSAGE_ADMIN_NOT_FOUND));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.update(INVALID_ID, request));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar o cpf de um usuário administrador já existente")
    void shouldThrowExceptionWhenUpdatingAdminUserWithExistingCpf() {
        AdminUserRequest request = AdminFixtures.createAdminUserUpdateRequest();

        String message = "Voter with this CPF already exists";
        when(adminService.update(VALID_ID, request)).thenThrow(new CpfAlreadyRegisteredException(message));

        Exception exception = assertThrows(CpfAlreadyRegisteredException.class, () -> adminController.update(VALID_ID, request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve obter um usuário administrador por ID com sucesso e retornar o status 200")
    void shouldGetAdminUserByIdSuccessfully() {
        AdminUserResponse responseFixture = AdminFixtures.createAdminUserResponse();

        when(adminService.getById(VALID_ID)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponse> response = adminController.getById(VALID_ID);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar obter um usuário administrador inexistente por ID")
    void shouldThrowExceptionWhenGettingNonExistentAdminUserById() {
        when(adminService.getById(INVALID_ID)).thenThrow(new UserNotFoundException(MESSAGE_ADMIN_NOT_FOUND));

        Exception exception = assertThrows(UserNotFoundException.class, () -> adminController.getById(INVALID_ID));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve excluir um usuário administrador com sucesso e retornar o status 204")
    void shouldDeleteAdminUserSuccessfully() {
        doNothing().when(adminService).delete(VALID_ID);
        ResponseEntity<Void> response = adminController.delete(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar excluir um usuário administrador inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentAdminUser() {
        doThrow(new UserNotFoundException(MESSAGE_ADMIN_NOT_FOUND)).when(adminService).delete(INVALID_ID);

        Exception exception = assertThrows(UserNotFoundException.class, () -> adminController.delete(INVALID_ID));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve obter um usuário administrador por email com sucesso e retornar o status 200")
    void shouldGetAdminUserByEmailSuccessfully() {
        AdminUserResponse responseFixture = AdminFixtures.createAdminUserResponse();

        when(adminService.getByEmail(VALID_EMAIL)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponse> response = adminController.getByEmail(VALID_EMAIL);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar obter um usuário administrador inexistente por email")
    void shouldThrowExceptionWhenGettingNonExistentAdminUserByEmail() {
        when(adminService.getByEmail(INVALID_EMAIL)).thenThrow(new UserNotFoundException(MESSAGE_ADMIN_NOT_FOUND));

        Exception exception = assertThrows(UserNotFoundException.class, () -> adminController.getByEmail(INVALID_EMAIL));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve obter um usuário administrador por CPF com sucesso e retornar o status 200")
    void shouldGetAdminUserByCpfSuccessfully(){
        AdminUserResponse responseFixture = AdminFixtures.createAdminUserResponse();

        when(adminService.getByCpf(VALID_CPF)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponse> response = adminController.getByCpf(VALID_CPF);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar obter um usuário administrador inexistente por cpf")
    void shouldThrowExceptionWhenAdminUserNotFoundByCpf(){
        when(adminService.getByCpf(INVALID_CPF)).thenThrow(new UserNotFoundException(MESSAGE_ADMIN_NOT_FOUND));

        Exception exception = assertThrows(UserNotFoundException.class, () -> adminController.getByCpf(INVALID_CPF));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve criar uma pauta de votação com sucesso e retornar o status 201")
    void shouldCreateVotingAgendaSuccessfully() {
        AgendaRequest request = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse responseFixture = AgendaFixtures.createAgendaResponse();

        when(agendaService.createAgenda(request, VALID_ID)).thenReturn(responseFixture);
        ResponseEntity<AgendaResponse> response = adminController.createAgenda(request, VALID_ID);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma pauta de votação com título inválido")
    void shouldThrowExceptionWhenCreatingVotingAgendaWithInvalidTitle() {
        AgendaRequest request = AgendaFixtures.createInvalidAgendaRequest();

        String message = "title and description cannot be empty";
        when(agendaService.createAgenda(request, VALID_ID)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createAgenda(request, VALID_ID));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar o status 201")
    void shouldCreateVotingSessionSuccessfully() {
        VotingSessionRequest request = VotingSessionFixtures.createValidVotingSessionRequest();
        VotingSessionResponse responseFixture = VotingSessionFixtures.createVotingSessionResponse();

        when(votingSessionService.openVotingSession(VALID_ID, request)).thenReturn(responseFixture);
        ResponseEntity<VotingSessionResponse> response = adminController.createVotingSession(VALID_ID, request);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma sessão de votação para uma pauta inexistente")
    void shouldThrowExceptionWhenCreatingVotingSessionForNonExistentAgenda() {
        VotingSessionRequest request = VotingSessionFixtures.createValidVotingSessionRequest();
        String message = "Agenda not found";

        when(votingSessionService.openVotingSession(INVALID_ID, request)).thenThrow(new AgendaNotFoundException(message));

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> adminController.createVotingSession(INVALID_ID, request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve criar um usuário votante com sucesso e retornar o status 201")
    void shouldCreateVoterUserSuccessfully() {
        VoterRequest request = VoterFixtures.createValidVoterRequest();
        VoterResponse responseFixture = VoterFixtures.createVoterResponse();

        when(adminService.createVoter(request)).thenReturn(responseFixture);
        ResponseEntity<VoterResponse> response = adminController.createVoter(request);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com CPF inválido")
    void shouldThrowExceptionWhenCreatingVoterUserWithInvalidCpf() {
        VoterRequest request = VoterFixtures.createInvalidVoterRequest();

        String message = "Invalid CPF";
        when(adminService.createVoter(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createVoter(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com nome inválido")
    void shouldThrowExceptionWhenCreatingVoterUserWithInvalidName() {
        VoterRequest request = VoterFixtures.createInvalidVoterRequest();

        String message = "Invalid name";
        when(adminService.createVoter(request)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createVoter(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com CPF já existente")
    void shouldThrowExceptionWhenCreatingVoterUserWithExistingCpf() {
        VoterRequest request = VoterFixtures.createValidVoterRequest();
        String message = "Voter with this CPF already exists";

        when(adminService.createVoter(request)).thenThrow(new CpfAlreadyRegisteredException(message));

        Exception exception = assertThrows(CpfAlreadyRegisteredException.class, () -> adminController.createVoter(request));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }
}
