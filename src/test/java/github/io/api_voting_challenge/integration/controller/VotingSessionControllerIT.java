package github.io.api_voting_challenge.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.controller.VotingSessionController;
import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.service.VoteService;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import github.io.api_voting_challenge.service.VotingSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_SESSION_ID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("Voting Session Controller Integration Tests")
@WebMvcTest({VotingSessionController.class, GlobalExceptionHandler.class})
public class VotingSessionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VotingSessionService votingSessionService;

    @MockitoBean
    private VotingSessionScheduler votingSessionScheduler;

    @MockitoBean
    private VoteService voteService;

    @Test
    @DisplayName("Deve criar uma sessão de votação com sucesso e retornar status 201")
    void shouldCreateVotingSessionSuccessfullyAndReturnStatus201() throws Exception {
        VotingSessionRequest votingSessionRequest = VotingSessionFixtures.createValidVotingSessionRequest();
        VotingSessionResponse votingSessionResponse = VotingSessionFixtures.createVotingSessionResponse();

        when(votingSessionService.openVotingSession(votingSessionRequest)).thenReturn(votingSessionResponse);
        mockMvc.perform(post("/api/v1/voting-sessions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(votingSessionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(votingSessionResponse.id()))
                .andExpect(jsonPath("$.durationInMinutes").value(votingSessionResponse.durationInMinutes()))
                .andExpect(jsonPath("$.startTime").value(votingSessionResponse.startTime()))
                .andExpect(jsonPath("$.endTime").value(votingSessionResponse.endTime()))
                .andExpect(jsonPath("$.status").value(votingSessionResponse.status().toString()))
                .andExpect(jsonPath("$.agendaId").value(votingSessionResponse.agendaId()));

        verify(votingSessionService, times(1)).openVotingSession(votingSessionRequest);
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de sessões de votação abertas com sucesso e retornar status 200")
    void shouldReturnPaginatedListOfOpenVotingSessionsSuccessfullyAndReturnStatus200() throws Exception {
        var pageable = Pageable.ofSize(10).withPage(0);
        var votingSessionResponsePage = VotingSessionFixtures.createVotingSessionResponsePage(10, pageable);

        when(votingSessionScheduler.getOpenVotingSessions(pageable)).thenReturn(votingSessionResponsePage);

        mockMvc.perform(get("/api/v1/voting-sessions")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(votingSessionResponsePage.getContent().size()))
                .andExpect(jsonPath("$.totalElements").value(votingSessionResponsePage.getTotalElements()))
                .andExpect(jsonPath("$.totalPages").value(votingSessionResponsePage.getTotalPages()))
                .andExpect(jsonPath("$.number").value(votingSessionResponsePage.getNumber()))
                .andExpect(jsonPath("$.size").value(votingSessionResponsePage.getSize()));

        verify(votingSessionScheduler, times(1)).getOpenVotingSessions(pageable);
        verifyNoMoreInteractions(votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve buscar o resultado de uma sessão de votação com sucesso e retornar status 200")
    void shouldFetchVotingSessionResultSuccessfullyAndReturnStatus200() throws Exception {
        var voteResultResponse = VoteFixtures.createVoteResultResponse();

        when(voteService.calculateVotingResult(VALID_SESSION_ID)).thenReturn(voteResultResponse);

        mockMvc.perform(get("/api/v1/voting-sessions/{sessionId}/results", VALID_SESSION_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(voteResultResponse.message()))
                .andExpect(jsonPath("$.yesVotes").value(voteResultResponse.yesVotes()))
                .andExpect(jsonPath("$.noVotes").value(voteResultResponse.noVotes()));

        verify(voteService, times(1)).calculateVotingResult(VALID_SESSION_ID);
        verifyNoMoreInteractions(votingSessionService);
    }

    @Test
    @DisplayName("Deve retornar Status Not Found ao buscar o resultado de uma sessão de votação inexistente")
    void shouldReturnStatusNotFoundWhenFetchingResultOfNonExistentVotingSession() throws Exception {
    when(voteService.calculateVotingResult(VALID_ID)).thenThrow(new VotingSessionNotFoundException("Voting session not found"));

        mockMvc.perform(get("/api/v1/voting-sessions/{sessionId}/results", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Voting session not found"));

        verify(voteService, times(1)).calculateVotingResult(VALID_ID);
        verifyNoMoreInteractions(votingSessionService);
    }
}
