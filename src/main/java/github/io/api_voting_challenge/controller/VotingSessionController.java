package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import github.io.api_voting_challenge.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class VotingSessionController {
    private VotingSessionScheduler votingSessionScheduler;
    private VotingSessionService votingSessionService;

    @Operation(summary = "Get a list of all open voting sessions", description = "Retrieves all voting sessions that are currently active and open for voting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of open voting sessions returned successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<VotingSessionResponse>> getOpenVotingSessions(@ParameterObject Pageable pageable) {
        Page<VotingSessionResponse> openSessions = votingSessionScheduler.getOpenVotingSessions(pageable);
        return ResponseEntity.ok(openSessions);
    }

    @Operation(summary = "Open a new voting session for an agenda", description = "Starts a voting session for a specific agenda. The session duration can be specified, or it defaults to 1 minute.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voting session opened successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or voting session already exists"),
            @ApiResponse(responseCode = "404", description = "Agenda not found")})
    @PostMapping
    public ResponseEntity<VotingSessionResponse> createVotingSession(@RequestBody VotingSessionRequest votingSessionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionService.openVotingSession(votingSessionRequest));
    }
}
