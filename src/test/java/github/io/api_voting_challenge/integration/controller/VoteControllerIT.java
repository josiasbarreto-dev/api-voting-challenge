package github.io.api_voting_challenge.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.controller.VoteController;
import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.fixtures.TestNoOperationCacheConfig;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_SESSION_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_SESSION_ID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("Vote Controller Integration Tests")
@WebMvcTest({VoteController.class, GlobalExceptionHandler.class})
@EnableCaching
@Import(TestNoOperationCacheConfig.class)
@ActiveProfiles("test")
public class VoteControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VoteService voteService;

    @Test
    @DisplayName("Deve registrar um voto com sucesso e retornar status 200")
    void shouldRegisterVoteSuccessfullyAndReturnStatus200() throws Exception {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        doNothing().when(voteService).registerVote(VALID_SESSION_ID ,voteRequest);
        mockMvc.perform(post("/api/v1/voting-sessions/{sessionId}/vote", VALID_SESSION_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        verify(voteService, times(1)).registerVote(VALID_SESSION_ID, voteRequest);
        verifyNoMoreInteractions(voteService);
    }

    @Test
    @DisplayName("Deve retornar status UnProcessable Entity quando o payload for inválido")
    void shouldReturnStatusUnProcessableEntityWhenPayloadIsInvalid() throws Exception {
        VoteRequest invalidVoteRequest = VoteFixtures.createInvalidVoteRequest();

        mockMvc.perform(post("/api/v1/voting-sessions/{sessionId}/vote", VALID_SESSION_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidVoteRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(voteService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found quando a sessão de votação não existir")
    void shouldReturnStatusNotFoundWhenVotingSessionDoesNotExist() throws Exception {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        doThrow(new VotingSessionNotFoundException("Voting session not found")).
                when(voteService).registerVote(INVALID_SESSION_ID, voteRequest);

        mockMvc.perform(post("/api/v1/voting-sessions/{sessionId}/vote", INVALID_SESSION_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isNotFound());

        verify(voteService, times(1)).registerVote(INVALID_SESSION_ID, voteRequest);
        verifyNoMoreInteractions(voteService);
    }
}
