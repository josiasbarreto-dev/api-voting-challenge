package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.exception.CpfAlreadyRegisteredException;
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
    private AdminServiceInterface adminService;

    @Mock
    private AgendaServiceInterface agendaService;

    @Mock
    private VotingSessionServiceInterface votingSessionService;

    @Test
    @DisplayName("Deve criar um usuário administrador com sucesso e retornar o status 201")
    void shouldCreateAdminUserSuccessfully() {
        AdminUserRequestDto requestDto = AdminFixtures.createValidAdminUserRequestDto();
        AdminUserResponseDto responseFixture = AdminFixtures.createAdminUserResponseDto();

        when(adminService.create(requestDto)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponseDto> response = adminController.create(requestDto);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com nome inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidName() {
        AdminUserRequestDto requestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        String message = "Invalid name";
        when(adminService.create(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com CPF inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidCpf() {
        AdminUserRequestDto requestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        String message = "Invalid CPF";
        when(adminService.create(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com email inválido")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidEmail() {
        AdminUserRequestDto requestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        String message = "Invalid email";
        when(adminService.create(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário administrador com senha inválida")
    void shouldThrowExceptionWhenCreatingAdminUserWithInvalidPassword() {
        AdminUserRequestDto requestDto = AdminFixtures.createInvalidAdminUserRequestDto();

        String message = "Invalid password";
        when(adminService.create(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.create(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário administrador com sucesso e retornar o status 200")
    void shouldUpdateAdminUserSuccessfully() {
        AdminUserRequestDto requestDto = AdminFixtures.createAdminUserUpdateRequestDto();
        AdminUserResponseDto responseFixture = AdminFixtures.createAdminUserResponseDtoAfterUpdate();

        when(adminService.update(VALID_ID, requestDto)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponseDto> response = adminController.update(VALID_ID, requestDto);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminService).update(VALID_ID, requestDto);
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar um usuário administrador inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentAdminUser() {
        AdminUserRequestDto requestDto = AdminFixtures.createAdminUserUpdateRequestDto();

        when(adminService.update(INVALID_ID, requestDto)).thenThrow(new IllegalArgumentException(MESSAGE_ADMIN_NOT_FOUND));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.update(INVALID_ID, requestDto));

        assertEquals(MESSAGE_ADMIN_NOT_FOUND, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar atualizar o cpf de um usuário administrador já existente")
    void shouldThrowExceptionWhenUpdatingAdminUserWithExistingCpf() {
        AdminUserRequestDto requestDto = AdminFixtures.createAdminUserUpdateRequestDto();

        String message = "Voter with this CPF already exists";
        when(adminService.update(VALID_ID, requestDto)).thenThrow(new CpfAlreadyRegisteredException(message));

        Exception exception = assertThrows(CpfAlreadyRegisteredException.class, () -> adminController.update(VALID_ID, requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve obter um usuário administrador por ID com sucesso e retornar o status 200")
    void shouldGetAdminUserByIdSuccessfully() {
        AdminUserResponseDto responseFixture = AdminFixtures.createAdminUserResponseDto();

        when(adminService.getById(VALID_ID)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponseDto> response = adminController.getById(VALID_ID);

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
        AdminUserResponseDto responseFixture = AdminFixtures.createAdminUserResponseDto();

        when(adminService.getByEmail(VALID_EMAIL)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponseDto> response = adminController.getByEmail(VALID_EMAIL);

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
        AdminUserResponseDto responseFixture = AdminFixtures.createAdminUserResponseDto();

        when(adminService.getByCpf(VALID_CPF)).thenReturn(responseFixture);
        ResponseEntity<AdminUserResponseDto> response = adminController.getByCpf(VALID_CPF);

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
        AgendaRequestDto requestDto = AgendaFixtures.createValidAgendaRequestDto();
        AgendaResponseDto responseFixture = AgendaFixtures.createAgendaResponseDto();

        when(agendaService.createAgenda(requestDto, VALID_ID)).thenReturn(responseFixture);
        ResponseEntity<AgendaResponseDto> response = adminController.createAgenda(requestDto, VALID_ID);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma pauta de votação com título inválido")
    void shouldThrowExceptionWhenCreatingVotingAgendaWithInvalidTitle() {
        AgendaRequestDto requestDto = AgendaFixtures.createInvalidAgendaRequestDto();

        String message = "title and description cannot be empty";
        when(agendaService.createAgenda(requestDto, VALID_ID)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createAgenda(requestDto, VALID_ID));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar o status 201")
    void shouldCreateVotingSessionSuccessfully() {
        VotingSessionRequestDto requestDto = VotingSessionFixtures.createValidVotingSessionRequestDto();
        VotingSessionResponseDto responseFixture = VotingSessionFixtures.createVotingSessionResponseDto();

        when(votingSessionService.openVotingSession(VALID_ID, requestDto)).thenReturn(responseFixture);
        ResponseEntity<VotingSessionResponseDto> response = adminController.createVotingSession(VALID_ID, requestDto);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma sessão de votação para uma pauta inexistente")
    void shouldThrowExceptionWhenCreatingVotingSessionForNonExistentAgenda() {
        VotingSessionRequestDto requestDto = VotingSessionFixtures.createValidVotingSessionRequestDto();
        String message = "Agenda not found";

        when(votingSessionService.openVotingSession(INVALID_ID, requestDto)).thenThrow(new AgendaNotFoundException(message));

        Exception exception = assertThrows(AgendaNotFoundException.class, () -> adminController.createVotingSession(INVALID_ID, requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve criar um usuário votante com sucesso e retornar o status 201")
    void shouldCreateVoterUserSuccessfully() {
        VoterRequestDto requestDto = VoterFixtures.createValidVoterRequestDto();
        VoterResponseDto responseFixture = VoterFixtures.createVoterResponseDto();

        when(adminService.createVoter(requestDto)).thenReturn(responseFixture);
        ResponseEntity<VoterResponseDto> response = adminController.createVoter(requestDto);

        assertEquals(responseFixture, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com CPF inválido")
    void shouldThrowExceptionWhenCreatingVoterUserWithInvalidCpf() {
        VoterRequestDto requestDto = VoterFixtures.createInvalidVoterRequestDto();

        String message = "Invalid CPF";
        when(adminService.createVoter(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createVoter(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com nome inválido")
    void shouldThrowExceptionWhenCreatingVoterUserWithInvalidName() {
        VoterRequestDto requestDto = VoterFixtures.createInvalidVoterRequestDto();

        String message = "Invalid name";
        when(adminService.createVoter(requestDto)).thenThrow(new IllegalArgumentException(message));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> adminController.createVoter(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um usuário votante com CPF já existente")
    void shouldThrowExceptionWhenCreatingVoterUserWithExistingCpf() {
        VoterRequestDto requestDto = VoterFixtures.createValidVoterRequestDto();
        String message = "Voter with this CPF already exists";

        when(adminService.createVoter(requestDto)).thenThrow(new CpfAlreadyRegisteredException(message));

        Exception exception = assertThrows(CpfAlreadyRegisteredException.class, () -> adminController.createVoter(requestDto));

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(adminService);
    }
}
