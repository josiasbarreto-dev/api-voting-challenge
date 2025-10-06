package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.VotingSessionControllerDocs;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;
import github.io.api_voting_challenge.dto.request.VotingSessionRequest;
import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import github.io.api_voting_challenge.service.VoteService;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import github.io.api_voting_challenge.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voting-sessions")
public class VotingSessionController implements VotingSessionControllerDocs {
    private final VotingSessionService votingSessionService;
    private final VotingSessionScheduler votingSessionScheduler;
    private final VoteService voteService;

    @Override
    @PostMapping
    public ResponseEntity<VotingSessionResponse> create(@RequestBody VotingSessionRequest votingSessionRequest) {
        log.info("Received request to open voting session: {}", votingSessionRequest.agendaId());
        VotingSessionResponse votingSessionResponse = votingSessionService.openVotingSession(votingSessionRequest);
        log.info("Voting session opened successfully: {}", votingSessionResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<VotingSessionResponse>> getOpenVotingSessions(@ParameterObject Pageable pageable) {
        log.info("Received request to get all open voting sessions - page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<VotingSessionResponse> openSessions = votingSessionScheduler.getOpenVotingSessions(pageable);
        log.info("Open voting sessions retrieved successfully: {}", openSessions.getTotalElements());
        return ResponseEntity.ok(openSessions);
    }

    @Override
    @GetMapping("/{sessionId}/results")
    public ResponseEntity<VoteResultResponse> getVotingResults(@PathVariable Long sessionId) {
        log.info("Received request to get voting results for session id: {}", sessionId);
        VoteResultResponse resultResponse = voteService.calculateVotingResult(sessionId);
        log.info("Voting results retrieved successfully for session id: {}", sessionId);
        return ResponseEntity.ok(resultResponse);
    }
}
