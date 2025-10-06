package github.io.api_voting_challenge.unit.controller;

import github.io.api_voting_challenge.controller.VotingSessionController;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;
import github.io.api_voting_challenge.dto.request.VotingSessionRequest;
import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.service.VoteService;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import github.io.api_voting_challenge.service.VotingSessionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_SESSION_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_SESSION_ID;
import static github.io.api_voting_challenge.fixtures.VotingSessionFixtures.createVotingSessionResponsePage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Voting Session Controller Unit Tests")
public class VotingSessionControllerUnitTest {
    @Mock
    private VotingSessionService votingSessionService;

    @Mock
    private VoteService voteService;

    @Mock
    private VotingSessionScheduler votingSessionScheduler;

    @InjectMocks
    private VotingSessionController votingSessionController;


    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar o status 201")
    void shouldCreateVotingSessionSuccessfully() {
        VotingSessionRequest votingSessionRequest = VotingSessionFixtures.createValidVotingSessionRequest();
        VotingSessionResponse votingSessionResponse = VotingSessionFixtures.createVotingSessionResponse();

        when(votingSessionService.openVotingSession(votingSessionRequest)).thenReturn(votingSessionResponse);
        ResponseEntity<VotingSessionResponse> response = votingSessionController.create(votingSessionRequest);

        assertEquals(votingSessionResponse, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar uma sessão de votação para uma pauta inexistente")
    void shouldThrowExceptionWhenCreatingVotingSessionForNonExistentAgenda() {
        VotingSessionRequest votingSessionRequest = VotingSessionFixtures.createInvalidVotingSessionRequest();
        String message = "Agenda not found";

        when(votingSessionService.openVotingSession(votingSessionRequest)).thenThrow(new AgendaNotFoundException(message));

        Exception exception = assertThrows(
                AgendaNotFoundException.class, () -> votingSessionController.create(votingSessionRequest)
        );

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve buscar os resultados da votação e retornar sucesso")
    void shouldFetchVotingResultsAndReturnSuccess() {
        VoteResultResponse voteResultResponse = VoteFixtures.createVoteResultResponse();

        when(voteService.calculateVotingResult(VALID_SESSION_ID)).thenReturn(voteResultResponse);
        ResponseEntity<VoteResultResponse> response = votingSessionController.getVotingResults(VALID_SESSION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(voteResultResponse, response.getBody());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando tentar buscar resultados de votação de uma sessão inexistente")
    void shouldThrowExceptionWhenFetchingResultsForNonExistentSession() {
        String errorMessage = "Sessão de votação não encontrada com ID: " + INVALID_SESSION_ID;

        when(voteService.calculateVotingResult(INVALID_SESSION_ID)).thenThrow(new VotingSessionNotFoundException(errorMessage));

        Exception exception = assertThrows(VotingSessionNotFoundException.class,
                () -> votingSessionController.getVotingResults(INVALID_SESSION_ID)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(voteService, times(1)).calculateVotingResult(INVALID_SESSION_ID);
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve buscar a primeira página de sessões ativas com sucesso")
    void shouldFetchFirstPageOfActiveVotingSessionsAndReturnSuccess() {
        Page<VotingSessionResponse> votingSessionResponsesPage = createVotingSessionResponsePage(15, Pageable.ofSize(10));
        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(votingSessionResponsesPage);

        ResponseEntity<Page<VotingSessionResponse>> result = votingSessionController.getOpenVotingSessions(Pageable.ofSize(10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(votingSessionResponsesPage, result.getBody());
        Assertions.assertNotNull(result.getBody());
        assertEquals(15, result.getBody().getTotalElements());
        assertEquals(10, result.getBody().getContent().size());
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar uma página vazia se não houver sessões ativas")
    void shouldReturnEmptyPageWhenNoActiveSessionsFound() {
        Page<VotingSessionResponse> emptyPage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);
        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<VotingSessionResponse>> result = votingSessionController.getOpenVotingSessions(Pageable.ofSize(10));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        assertEquals(0, result.getBody().getTotalElements());
        assertEquals(0, result.getBody().getContent().size());
    }
}
