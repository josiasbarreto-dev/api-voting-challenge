package github.io.api_voting_challenge.unit.controller;

import github.io.api_voting_challenge.controller.VoteController;
import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_SESSION_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Vote Controller Unit Tests")
public class VoteControllerUnitTest {
    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteController voteController;

    @Test
    @DisplayName("Deve registrar um voto com sucesso e retornar o status 200")
    void shouldRegisterVoteSucessfullyAndReturnStatus200(){
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        doNothing().when(voteService).registerVote(VALID_SESSION_ID, voteRequest);
        ResponseEntity<Void> response = voteController.vote(VALID_SESSION_ID, voteRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoMoreInteractions(voteService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar um voto com Sessão de Votação Inexistente")
    void shouldThrowExceptionWhenRegisteringVoteWithNonExistentSessionId(){
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        String errorMensage = "Voting session not found";
        doThrow(new BusinessException(errorMensage, HttpStatus.NOT_FOUND)).when(voteService).registerVote(INVALID_SESSION_ID, voteRequest);

        Exception exception = assertThrows(
                BusinessException.class,
                () -> voteController.vote(INVALID_SESSION_ID, voteRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verify(voteService).registerVote(INVALID_SESSION_ID, voteRequest);
        verifyNoMoreInteractions(voteService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar um voto com Id do Usuário Inexistente")
    void shouldThrowExceptionWhenRegisteringVoteWithNonExistentUserId(){
        VoteRequest voteRequest = VoteFixtures.createInvalidVoteRequest();

        String errorMensage = "User not found";
        doThrow(new BusinessException(errorMensage, HttpStatus.NOT_FOUND)).when(voteService).registerVote(VALID_SESSION_ID, voteRequest);

        Exception exception = assertThrows(
                BusinessException.class,
                () -> voteController.vote(VALID_SESSION_ID, voteRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verify(voteService).registerVote(VALID_SESSION_ID, voteRequest);
        verifyNoMoreInteractions(voteService);
    }

    @Test
    @DisplayName("Deve retornar status UnProcessable Entity quando o payload for inválido")
    void shouldReturnStatusUnProcessableEntityWhenPayloadIsInvalid(){
        VoteRequest invalidVoteRequest = VoteFixtures.createInvalidVoteRequest();

        doNothing().when(voteService).registerVote(VALID_SESSION_ID, invalidVoteRequest);
        ResponseEntity<Void> response = voteController.vote(VALID_SESSION_ID, invalidVoteRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoMoreInteractions(voteService);
    }
}
