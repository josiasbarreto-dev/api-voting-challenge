package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.VoteControllerDocs;
import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voting-sessions")
public class VoteController implements VoteControllerDocs {
    private final VoteService voteService;

    @Override
    @PostMapping("/{sessionId}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long sessionId, @RequestBody @Valid VoteRequest voteRequest) {
        voteService.registerVote(sessionId, voteRequest);
        return ResponseEntity.ok().build();
    }
}
