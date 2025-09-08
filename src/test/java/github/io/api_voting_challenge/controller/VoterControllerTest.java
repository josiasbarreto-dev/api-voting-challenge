package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.exception.VotingSessionClosedException;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.service.VotingSessionSchedulerInterface;
import github.io.api_voting_challenge.service.impl.VoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static github.io.api_voting_challenge.fixtures.VotingSessionFixtures.createVotingSessionResponsePage;
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
    private VotingSessionSchedulerInterface votingSessionScheduler;


    @Test
    @DisplayName("Deve buscar a primeira página de sessões ativas com sucesso")
    void shouldFetchFirstPageOfActiveVotingSessionsAndReturnSuccess() {
        Page<VotingSessionResponse> mockedPage = createVotingSessionResponsePage(15, Pageable.ofSize(10));
        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(mockedPage);

        ResponseEntity<Page<VotingSessionResponse>> result = voterController.getOpenVotingSessions(Pageable.ofSize(10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockedPage, result.getBody());
        assertEquals(15, result.getBody().getTotalElements());
        assertEquals(10, result.getBody().getContent().size());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve retornar uma página vazia se não houver sessões ativas")
    void shouldReturnEmptyPageWhenNoActiveSessionsFound() {
        Page<VotingSessionResponse> emptyPage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);
        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<VotingSessionResponse>> result = voterController.getOpenVotingSessions(Pageable.ofSize(10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().getTotalElements());
        assertEquals(0, result.getBody().getContent().size());
    }

    @Test
    @DisplayName("Deve permitir que um usuário vote com sucesso em Pautas com Sessão Aberta Disponível")
    void shouldAllowUserToVoteSuccessfullyInOpenVotingSession() {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        doNothing().when(voteServiceImpl).registerVote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);
        ResponseEntity<Void> response = voterController.vote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve permitir que o usuário vote 'Não' em Pautas com Sessão Aberta Disponível")
    void shouldAllowUserToVoteNoSuccessfullyInOpenVotingSession() {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequestBuilder().voteOption(VoteOption.NO).build();

        doNothing().when(voteServiceImpl).registerVote(VALID_SESSION_ID, VALID_USER_ID, voteRequest);
        ResponseEntity<Void> response = voterController.vote(VALID_USER_ID, VALID_USER_ID, voteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(voteServiceImpl);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o usuário tentar votar em uma sessão fechada")
    void shouldThrowExceptionWhenUserTriesToVoteInClosedSession() {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();
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
        VoteResultResponse voteResultResponse = VoteFixtures.createVoteResultResponse();

        when(voteServiceImpl.calculateVotingResult(VALID_SESSION_ID)).thenReturn(voteResultResponse);
        ResponseEntity<VoteResultResponse> response = voterController.getVotingResults(VALID_SESSION_ID);

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
