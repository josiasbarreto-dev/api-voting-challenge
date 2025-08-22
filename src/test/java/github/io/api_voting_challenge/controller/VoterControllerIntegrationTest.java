package github.io.api_voting_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.service.VoteServiceInterface;
import github.io.api_voting_challenge.service.VotingSessionSchedulerInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoterController.class)
public class VoterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingSessionSchedulerInterface votingSessionScheduler;

    @MockitoBean
    private VoteServiceInterface voteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve buscar a primeira página de sessões ativas com sucesso e retornar status 200")
    void shouldFetchFirstPageOfActiveVotingSessionsAndReturnStatus200() throws Exception {
        List<VotingSessionResponseDto> sessions = VotingSessionFixtures.createVotingSessionResponseDtoList(5);

        Page<VotingSessionResponseDto> mockPage = new PageImpl<>(sessions, PageRequest.of(0, 5), 15);

        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/voters/open-sessions")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));

        verify(votingSessionScheduler).getOpenVotingSessions(any(Pageable.class));
        verifyNoInteractions(voteService);
    }

    @Test
    @DisplayName("Deve buscar a segunda página de sessões ativas com sucesso")
    void shouldFetchSecondPageOfActiveVotingSessions() throws Exception {
        List<VotingSessionResponseDto> sessions = VotingSessionFixtures.createVotingSessionResponseDtoList(5);

        Page<VotingSessionResponseDto> mockPage = new PageImpl<>(sessions, PageRequest.of(1, 5), 15);
        when(votingSessionScheduler.getOpenVotingSessions(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/voters/open-sessions")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));

        verify(votingSessionScheduler).getOpenVotingSessions(any(Pageable.class));
        verifyNoInteractions(voteService);
    }

    @Test
    @DisplayName("Deve retornar 422 ao tentar registrar voto com payload inválido")
    void shouldReturnUnprocessableEntityWhenTryingToRegisterVoteWithInvalidPayload() throws Exception {
        VoteRequestDTO invalidRequest = new VoteRequestDTO(null);
        Long sessionId = 1L;
        Long userId = 1L;

        mockMvc.perform(post("/api/v1/voters/voting-session/{sessionId}/vote", sessionId)
                        .header("X-User-Id", String.valueOf(userId))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.details.voteOption").value("Vote option cannot be null."));

        verifyNoInteractions(voteService);
        verifyNoInteractions(votingSessionScheduler);
    }
}