package github.io.api_voting_challenge.unit.model;

import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
@DisplayName("Voting Session Model Unit Tests")
public class VotingSessionUnitTest {
    @Test
    @DisplayName("Deve atualizar a hora de término de uma sessão de votação com sucesso")
    void shouldUpdateEndTimeSuccessfully() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        LocalDateTime newEndTime = LocalDateTime.now().plusHours(2);
        votingSession.updateEndTime(newEndTime);

        assert (votingSession.getEndTime().isEqual(newEndTime));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar a hora de término para nulo")
    void shouldThrowExceptionWhenUpdatingEndTimeToNull() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> votingSession.updateEndTime(null)
        );

        String expectedMessage = "End time cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Deve atualizar a pauta de uma sessão de votação com sucesso")
    void shouldUpdateAgendaSuccessfully() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        Agenda agenda = AgendaFixtures.createAgenda();
        votingSession.updateAgenda(agenda);

        assertEquals(agenda, votingSession.getAgenda());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar a pauta para nulo")
    void shouldThrowExceptionWhenUpdatingAgendaToNull() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> votingSession.updateAgenda(null)
        );

        String expectedMessage = "Agenda cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso")
    void shouldCreateVotingSessionSuccessfully() {
        Agenda agenda = AgendaFixtures.createAgenda();
        int durationInMinutes = 45;

        VotingSession votingSession = VotingSession.createSession(agenda, durationInMinutes);

        assertEquals(agenda, votingSession.getAgenda());
        assertEquals(durationInMinutes, votingSession.getDurationInMinutes());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar uma sessão de votação com pauta nula")
    void shouldThrowExceptionWhenCreatingVotingSessionWithNullAgenda() {
        int durationInMinutes = 45;

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> VotingSession.createSession(null, durationInMinutes)
        );

        String expectedMessage = "Agenda cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        assertEquals(422, exception.getStatus().value());
    }

    @Test
    @DisplayName("Deve verificar se a sessão de votação está aberta")
    void shouldCheckIfVotingSessionIsOpen() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();

        boolean isOpen = votingSession.isOpen();
        assertTrue(isOpen);
    }
}