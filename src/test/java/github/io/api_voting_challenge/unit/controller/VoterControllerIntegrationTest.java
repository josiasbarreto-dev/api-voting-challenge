package github.io.api_voting_challenge.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.controller.VoterController;
import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.exception.VotingSessionNotFoundException;
import github.io.api_voting_challenge.service.VoteServiceInterface;
import github.io.api_voting_challenge.service.VotingSessionSchedulerInterface;
import github.io.api_voting_challenge.unit.fixtures.VoteFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.unit.fixtures.TestConstants.INVALID_ID;
import static github.io.api_voting_challenge.unit.fixtures.TestConstants.VALID_ID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({VoterController.class, GlobalExceptionHandler.class})
public class VoterControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteServiceInterface voteService;

    @MockitoBean
    private VotingSessionSchedulerInterface votingSessionScheduler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve buscar a primeira página de sessões ativas com sucesso")
    void shouldFetchFirstPageOfActiveVotingSessionsAndReturnStatus200() throws Exception {
        mockMvc.perform(get("/open-sessions")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType("application/json"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));

        verify(votingSessionScheduler).getOpenVotingSessions(any());
        verifyNoInteractions(voteService);
    }

    @Test
    @DisplayName("Deve buscar a segunda página de sessões ativas")
    void shouldFetchSecondPageOfActiveVotingSessions() throws Exception {
        mockMvc.perform(get("/open-sessions")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType("application/json"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    @DisplayName("Deve permitir o registro de votos e retornar status 201")
    void shouldAllowVoteRegistrationAndReturnStatus201() throws Exception {
        VoteRequestDTO voteRequestDTO = VoteFixtures.createValidVoteRequestDto();

        doNothing().when(voteService).registerVote(VALID_ID, VALID_ID, voteRequestDTO);
        mockMvc.perform(post("/api/v1/voters/voting-session/{sessionId}/vote", VALID_ID)
                        .header("X-User-Id", VALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequestDTO)))
                .andExpect(status().isCreated());

        verify(voteService).registerVote(VALID_ID, VALID_ID, voteRequestDTO);
        verifyNoInteractions(votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve retornar status not found ao tentar registrar voto em sessão inexistente")
    void shouldReturnNotFoundWhenTryingToRegisterVoteInNonExistentSession() throws Exception {
        VoteRequestDTO voteRequestDTO = VoteFixtures.createValidVoteRequestDto();

        doThrow(new VotingSessionNotFoundException("Voting session not found with ID: " + INVALID_ID))
                .when(voteService).registerVote(INVALID_ID, VALID_ID, voteRequestDTO);

        mockMvc.perform(post("/api/v1/voters/voting-session/{sessionId}/vote", INVALID_ID)
                        .header("X-User-Id", VALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequestDTO)))
                .andExpect(status().isNotFound());

        verify(voteService).registerVote(INVALID_ID, VALID_ID, voteRequestDTO);
        verifyNoInteractions(votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve retornar status not found ao tentar registrar voto com usuário inexistente")
    void shouldReturnNotFoundWhenTryingToRegisterVoteWithNonExistentUser() throws Exception {
        VoteRequestDTO voteRequestDTO = VoteFixtures.createValidVoteRequestDto();

        doThrow(new VotingSessionNotFoundException("Voting session not found with ID: " + VALID_ID))
                .when(voteService).registerVote(VALID_ID, INVALID_ID, voteRequestDTO);

        mockMvc.perform(post("/api/v1/voters/voting-session/{sessionId}/vote", VALID_ID)
                        .header("X-User-Id", INVALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequestDTO)))
                .andExpect(status().isNotFound());

        verify(voteService).registerVote(VALID_ID, INVALID_ID, voteRequestDTO);
        verifyNoInteractions(votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve retornar status bad request ao tentar registrar voto com payload inválido")
    void shouldReturnBadRequestWhenTryingToRegisterVoteWithInvalidPayload() throws Exception {
        VoteRequestDTO voteRequestDTO = VoteFixtures.createInvalidVoteRequestDto();

        mockMvc.perform(post("/api/v1/voters/voting-session/{sessionId}/vote", VALID_ID)
                        .header("X-User-Id", VALID_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(voteRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(voteService, votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve mostrar o resultado da votação e retornar status 200")
    void shouldShowVotingResultsAndReturnStatus200() throws Exception {
        VoteResultResponseDTO voteResultResponseDTO = VoteFixtures.createVoteResultResponseDto();

        when(voteService.calculateVotingResult(VALID_ID)).thenReturn(voteResultResponseDTO);
        mockMvc.perform(get("/api/v1/voters/voting-session/{sessionId}/results", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(voteResultResponseDTO.message()))
                .andExpect(jsonPath("$.yesVotes").value(voteResultResponseDTO.yesVotes()))
                .andExpect(jsonPath("$.noVotes").value(voteResultResponseDTO.noVotes()));

        verify(voteService).calculateVotingResult(VALID_ID);
        verifyNoInteractions(votingSessionScheduler);
    }

    @Test
    @DisplayName("Deve retornar status not  found ao tentar buscar resultador de votação com sessão em progresso")
    void shouldReturnNotFoundWhenTryingToFetchVotingResultsWithSessionInProgress() throws Exception {
        doThrow(new VotingSessionNotFoundException("Voting session not found with ID: " + VALID_ID))
                .when(voteService).calculateVotingResult(VALID_ID);

        mockMvc.perform(get("/api/v1/voters/voting-session/{sessionId}/results", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(voteService).calculateVotingResult(VALID_ID);
        verifyNoInteractions(votingSessionScheduler);
    }
}
