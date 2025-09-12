package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.VotingSessionControllerDocs;
import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import github.io.api_voting_challenge.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voting-sessions")
public class VotingSessionController implements VotingSessionControllerDocs {
    private final VotingSessionService votingSessionService;
    private final VotingSessionScheduler votingSessionScheduler;

    @Override
    @PostMapping
    public ResponseEntity<VotingSessionResponse> create(@RequestBody VotingSessionRequest votingSessionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionService.openVotingSession(votingSessionRequest));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<VotingSessionResponse>> getOpenVotingSessions(@ParameterObject Pageable pageable) {
        Page<VotingSessionResponse> openSessions = votingSessionScheduler.getOpenVotingSessions(pageable);
        return ResponseEntity.ok(openSessions);
    }

    @Override
    @GetMapping("/{sessionId}/results")
    public ResponseEntity<VoteResultResponse> getVotingResults(@PathVariable Long sessionId) {
        return ResponseEntity.ok(votingSessionService.calculateVotingResult(sessionId));
    }
}
