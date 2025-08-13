package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.exception.VotingSessionClosedException;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.service.impl.VoteServiceImpl;
import github.io.api_voting_challenge.service.impl.VotingSessionSchedulerImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Voter Controller Test")
public class VoterControllerTest {
    @InjectMocks
    private VoterController voterController;

    @Mock
    private VoteServiceImpl voteServiceImpl;

    @Mock
    private VotingSessionSchedulerImpl votingSessionSchedulerImpl;

    private static final Long VALID_SESSION_ID = 1L;
    private static final Long INVALID_SESSION_ID = 99L;
    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 99L;

    @Test
    @DisplayName("Deve buscar as sessões de votações ativas e retornar sucesso")
    void shouldFetchActiveVotingSessionsAndReturnSuccess() {
        List<VotingSessionResponseDto> activeSessions = VotingSessionFixtures.createVotingSessionResponseDtoList();

        when(votingSessionSchedulerImpl.getOpenVotingSessions()).thenReturn(activeSessions);
        ResponseEntity<List<VotingSessionResponseDto>> result = voterController.getOpenVotingSessions();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(activeSessions, result.getBody());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve permitir que um usuário vote com sucesso em Pautas com Sessão Aberta Disponível")
    void shouldAllowUserToVoteSuccessfullyInOpenVotingSession() {
        VoteRequestDTO voteRequest = VoteFixtures.createValidVoteRequestDto();

        doNothing().when(voteServiceImpl).registerVote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);
        ResponseEntity<Void> response = voterController.vote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve permitir que o usuário vote 'Não' em Pautas com Sessão Aberta Disponível")
    void shouldAllowUserToVoteNoSuccessfullyInOpenVotingSession() {
        VoteRequestDTO voteRequest = VoteFixtures.createValidVoteRequestDtoBuilder().voteOption(VoteOption.NO).build();

        doNothing().when(voteServiceImpl).registerVote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);
        ResponseEntity<Void> response = voterController.vote(VALID_USER_ID, VALID_USER_ID, voteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o usuário tentar votar em uma sessão fechada")
    void shouldThrowExceptionWhenUserTriesToVoteInClosedSession() {
        VoteRequestDTO voteRequest = VoteFixtures.createValidVoteRequestDto();
        String message = "Sessão de votação já encerrada";

        doThrow(new VotingSessionClosedException(message))
                .when(voteServiceImpl)
                .registerVote(INVALID_SESSION_ID, VALID_USER_ID, voteRequest);

        Exception exception = assertThrows(VotingSessionClosedException.class,
                () -> voterController.vote(INVALID_SESSION_ID, VALID_USER_ID, voteRequest)
        );

        assertEquals(message, exception.getMessage());
        verify(voteServiceImpl, times(1)).registerVote(INVALID_SESSION_ID, VALID_USER_ID, voteRequest);
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve buscar os resultados da votação e retornar sucesso")
    void shouldFetchVotingResultsAndReturnSuccess() {
        VoteResultResponseDTO voteResultResponse = VoteFixtures.createVoteResultResponseDto();

        when(voteServiceImpl.calculateVotingResult(VALID_SESSION_ID)).thenReturn(voteResultResponse);
        ResponseEntity<VoteResultResponseDTO> response = voterController.getVotingResults(VALID_SESSION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(voteResultResponse, response.getBody());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando tentar buscar resultados de votação de uma sessão inexistente")
    void shouldThrowExceptionWhenFetchingResultsForNonExistentSession() {
        String message = "Sessão de votação não encontrada com ID: " + INVALID_SESSION_ID;

        when(voteServiceImpl.calculateVotingResult(INVALID_SESSION_ID)).thenThrow(new VotingSessionNotFoundException(message));

        Exception exception = assertThrows(VotingSessionNotFoundException.class,
                () -> voterController.getVotingResults(INVALID_SESSION_ID)
        );

        assertEquals(message, exception.getMessage());
        verify(voteServiceImpl, times(1)).calculateVotingResult(INVALID_SESSION_ID);
        verifyNoMoreInteractions(voteServiceImpl);
    }
}
