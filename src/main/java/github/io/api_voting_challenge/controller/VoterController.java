package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.service.impl.VoteServiceImpl;
import github.io.api_voting_challenge.service.impl.VotingSessionSchedulerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/voter")
@Tag(name = "Voter Operations", description = "Endpoints for voters to interact with voting sessions and results.")
public class VoterController {
    private final VotingSessionSchedulerImpl votingSessionSchedulerImpl;
    private final VoteServiceImpl voteServiceImpl;

    public VoterController(VotingSessionSchedulerImpl votingSessionSchedulerImpl, VoteServiceImpl voteServiceImpl) {
        this.votingSessionSchedulerImpl = votingSessionSchedulerImpl;
        this.voteServiceImpl = voteServiceImpl;
    }

    @Operation(summary = "Get a list of all open voting sessions", description = "Retrieves all voting sessions that are currently active and open for voting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of open voting sessions returned successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponseDto.class)))
    })
    @GetMapping("/open-sessions")
    public ResponseEntity<List<VotingSessionResponseDto>> getOpenVotingSessions() {
        List<VotingSessionResponseDto> openSessions = votingSessionSchedulerImpl.getOpenVotingSessions();
        return ResponseEntity.ok(openSessions);
    }

    @Operation(summary = "Register a vote for a specific session", description = "Allows a user to cast a 'Sim' or 'Nao' vote in an active voting session. Each user can vote only once per session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user has already voted"),
            @ApiResponse(responseCode = "404", description = "Voting session not found or is closed")})
    @PostMapping("/voting-session/{sessionId}/vote")
    public ResponseEntity<Void> vote(@Parameter(description = "ID of the voting session") @PathVariable Long sessionId, @Parameter(description = "Unique ID of the user casting the vote", required = true) @RequestHeader("X-User-Id") Long userId, @RequestBody VoteRequestDTO voteRequest) {
        voteServiceImpl.registerVote(sessionId, userId, voteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get voting results for a session", description = "Retrieves the final vote count for a specific voting session. This endpoint should be accessed only after the session has ended.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voting results retrieved successfully", content = @Content(schema = @Schema(implementation = VoteResultResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Voting session not found")})
    @GetMapping("/voting-session/{sessionId}/results")
    public ResponseEntity<VoteResultResponseDTO> getVotingResults(@PathVariable Long sessionId) {
        return ResponseEntity.ok(voteServiceImpl.calculateVotingResult(sessionId));
    }
}
