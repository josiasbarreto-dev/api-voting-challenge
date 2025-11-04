package github.io.api_voting_challenge.unit.model;

import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
@DisplayName("Agenda Model Unit Tests")
public class AgendaUnitTest {
    @Test
    @DisplayName("Deve atualizar o título de uma Pauta com sucesso")
    void shouldUpdateAgendaTitleSuccessfully() {
        Agenda agenda = AgendaFixtures.createAgenda();
        String newTitle = "New Agenda Title";

        agenda.updateTitle(newTitle);

        assertEquals(newTitle, agenda.getTitle());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar o título de uma Pauta com valor nulo ou em branco")
    void shouldThrowExceptionWhenUpdatingAgendaTitleWithNullOrBlankValue() {
        Agenda agenda = AgendaFixtures.createAgenda();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> agenda.updateTitle(" ")
        );
        assertEquals("Title cannot be null or blank", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());

        BusinessException exceptionNull = assertThrows(
                BusinessException.class,
                () -> agenda.updateTitle(null)
        );
        assertEquals("Title cannot be null or blank", exceptionNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionNull.getStatus());
    }

    @Test
    @DisplayName("Deve atualizar a descrição de uma Pauta com sucesso")
    void shouldUpdateAgendaDescriptionSuccessfully() {
        Agenda agenda = AgendaFixtures.createAgenda();
        String newDescription = "New Agenda Description";

        agenda.updateDescription(newDescription);

        assertEquals(newDescription, agenda.getDescription());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar a descrição de uma Pauta com valor nulo ou em branco")
    void shouldThrowExceptionWhenUpdatingAgendaDescriptionWithNullOrBlankValue() {
        Agenda agenda = AgendaFixtures.createAgenda();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> agenda.updateDescription(" ")
        );
        assertEquals("Description cannot be null or blank", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());

        BusinessException exceptionNull = assertThrows(
                BusinessException.class,
                () -> agenda.updateDescription(null)
        );
        assertEquals("Description cannot be null or blank", exceptionNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionNull.getStatus());
    }

    @Test
    @DisplayName("Deve atualizar o status de uma Pauta com sucesso")
    void shouldUpdateAgendaStatusSuccessfully() {
        Agenda agenda = AgendaFixtures.createAgenda();
        agenda.updateStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, agenda.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar o status de uma Pauta com valor nulo")
    void shouldThrowExceptionWhenUpdatingAgendaStatusWithNullValue() {
        Agenda agenda = AgendaFixtures.createAgenda();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> agenda.updateStatus(null)
        );
        assertEquals("Status cannot be null", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
    }

    @Test
    @DisplayName("Deve abrir uma Sessão de Votação com sucesso")
    void shouldOpenVotingSessionSuccessfully() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Agenda Title")
                .description("Agenda Description")
                .status(Status.PENDING)
                .votingSession(votingSession)
                .build();

        agenda.openSession(votingSession);

        assertEquals(Status.IN_PROGRESS, agenda.getStatus());
        assertEquals(votingSession, agenda.getVotingSession());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar abrir uma Sessão de Votação com valor nulo ou status inválido")
    void shouldThrowExceptionWhenOpeningVotingSessionWithNullOrInvalidStatus() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        Agenda agendaWithInvalidStatus = Agenda.builder()
                .id(1L)
                .title("Agenda Title")
                .description("Agenda Description")
                .status(Status.IN_PROGRESS)
                .votingSession(null)
                .build();

        BusinessException exceptionNull = assertThrows(
                BusinessException.class,
                () -> agendaWithInvalidStatus.openSession(null)
        );
        assertEquals("Cannot open voting session. Agenda must be in PENDING status and voting session cannot be null.", exceptionNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionNull.getStatus());

        BusinessException exceptionInvalidStatus = assertThrows(
                BusinessException.class,
                () -> agendaWithInvalidStatus.openSession(votingSession)
        );
        assertEquals("Cannot open voting session. Agenda must be in PENDING status and voting session cannot be null.", exceptionInvalidStatus.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionInvalidStatus.getStatus());
    }

    @Test
    @DisplayName("Deve validar se a Pauta está apta para abrir uma Sessão de Votação")
    void shouldValidateIfAgendaIsValidForOpeningVotingSession() {
        Agenda pendingAgenda = Agenda.builder()
                .id(1L)
                .title("Agenda Title")
                .description("Agenda Description")
                .status(Status.PENDING)
                .build();

        Agenda inProgressAgenda = Agenda.builder()
                .id(2L)
                .title("Agenda Title")
                .description("Agenda Description")
                .status(Status.IN_PROGRESS)
                .build();

        Agenda closedAgenda = Agenda.builder()
                .id(3L)
                .title("Agenda Title")
                .description("Agenda Description")
                .status(Status.CLOSED)
                .build();

        assertTrue(pendingAgenda.isValidForSession());
        assertFalse(inProgressAgenda.isValidForSession());
        assertFalse(closedAgenda.isValidForSession());
    }
}
