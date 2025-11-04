package github.io.api_voting_challenge.unit.model;

import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.enums.VoteOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Unit")
@DisplayName("Vote Model Unit Tests")
public class VoteUnitTest {
    @Test
    @DisplayName("Deve criar um voto com sucesso")
    void shouldCreateVoteSuccessfully() {
        User user = UserFixtures.createValidUserEntity();
        Agenda agenda = AgendaFixtures.createAgenda();
        VoteOption voteOption = VoteOption.YES;

        Vote vote = Vote.createVote(user, agenda, voteOption);

        assertEquals(user, vote.getUser());
        assertEquals(agenda, vote.getAgenda());
        assertEquals(voteOption, vote.getVoteOption());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar um voto com valor nulo para Usuário, Pauta ou Opção de Voto")
    void shouldThrowExceptionWhenCreatingVoteWithNullUserAgendaOrVoteOption() {
        Agenda agenda = AgendaFixtures.createAgenda();
        User user = UserFixtures.createValidUserEntity();
        VoteOption voteOption = VoteOption.NO;
        // Lança exceção quando a Agenda está nula
        BusinessException exceptionAgendaNull = assertThrows(
                BusinessException.class,
                () -> Vote.createVote(user, null, voteOption)
        );

        assertEquals("Agenda cannot be null", exceptionAgendaNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionAgendaNull.getStatus());

        // Lança exceção quando o User está nulo
        BusinessException exceptionUserNull = assertThrows(
                BusinessException.class,
                () -> Vote.createVote(null, agenda, voteOption)
        );
        assertEquals("User cannot be null", exceptionUserNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionUserNull.getStatus());

        // Lança exceção quando o VoteOption está nulo
        BusinessException exceptionVoteOptionNull = assertThrows(
                BusinessException.class,
                () -> Vote.createVote(user, agenda, null)
        );
        assertEquals("Vote option cannot be null", exceptionVoteOptionNull.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionVoteOptionNull.getStatus());
    }
}
