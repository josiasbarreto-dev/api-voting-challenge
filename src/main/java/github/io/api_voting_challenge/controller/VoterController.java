package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.service.impl.VoteServiceImpl;
import github.io.api_voting_challenge.service.impl.VotingSessionSchedulerImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/voter")
public class VoterController {
    private final VotingSessionSchedulerImpl votingSessionSchedulerImpl;
    private final VoteServiceImpl voteServiceImpl;

    public VoterController(VotingSessionSchedulerImpl votingSessionSchedulerImpl, VoteServiceImpl voteServiceImpl) {
        this.votingSessionSchedulerImpl = votingSessionSchedulerImpl;
        this.voteServiceImpl = voteServiceImpl;
    }

    @GetMapping("/open-sessions")
    public ResponseEntity<List<VotingSessionResponseDto>> getOpenVotingSessions() {
        List<VotingSessionResponseDto> openSessions = votingSessionSchedulerImpl.getOpenVotingSessions();
        return ResponseEntity.ok(openSessions);
    }

    @PostMapping("/voting-session/{sessionId}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long sessionId,
                                                @RequestHeader("X-User-Id") Long userId,
                                                @RequestBody VoteRequestDTO voteRequest
    ) {
        voteServiceImpl.registerVote(sessionId, userId, voteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/voting-session/{sessionId}/results")
    public ResponseEntity<VoteResultResponseDTO> getVotingResults(@PathVariable Long sessionId) {
        return ResponseEntity.ok(voteServiceImpl.calculateVotingResult(sessionId));
    }
}
