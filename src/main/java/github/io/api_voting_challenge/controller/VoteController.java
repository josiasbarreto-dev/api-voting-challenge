package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.VoteControllerDocs;
import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voting-sessions")
public class VoteController implements VoteControllerDocs {
    private final VoteService voteService;

    @Override
    @PostMapping("/{sessionId}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long sessionId, @RequestBody @Valid VoteRequest voteRequest) {
        log.info("Received request to register vote for session {}: {}", sessionId, voteRequest.userId());
        voteService.registerVote(sessionId, voteRequest);
        log.info("Vote registered successfully for session : {}", sessionId);
        return ResponseEntity.ok().build();
    }
}
