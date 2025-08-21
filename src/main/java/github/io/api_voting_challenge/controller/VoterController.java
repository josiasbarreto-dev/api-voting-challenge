package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.service.VoteServiceInterface;
import github.io.api_voting_challenge.service.VotingSessionSchedulerInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voters")
@Tag(name = "Voter Operations", description = "Endpoints for voters to interact with voting sessions and results.")
public class VoterController {
    private final VotingSessionSchedulerInterface votingSessionScheduler;
    private final VoteServiceInterface voteService;

    @Operation(summary = "Get a list of all open voting sessions", description = "Retrieves all voting sessions that are currently active and open for voting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of open voting sessions returned successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponseDto.class)))
    })
    @GetMapping("/open-sessions")
    public ResponseEntity<Page<VotingSessionResponseDto>> getOpenVotingSessions(@ParameterObject Pageable pageable) {
        Page<VotingSessionResponseDto> openSessions = votingSessionScheduler.getOpenVotingSessions(pageable);
        return ResponseEntity.ok(openSessions);
    }

    @Operation(summary = "Register a vote for a specific session", description = "Allows a user to cast a 'Sim' or 'Nao' vote in an active voting session. Each user can vote only once per session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user has already voted"),
            @ApiResponse(responseCode = "404", description = "Voting session not found or is closed")})
    @PostMapping("/voting-session/{sessionId}/vote")
    public ResponseEntity<Void> vote(@Parameter(description = "ID of the voting session") @PathVariable Long sessionId, @Parameter(description = "Unique ID of the user casting the vote", required = true) @RequestHeader("X-User-Id") Long userId, @RequestBody @Valid VoteRequestDTO voteRequest) {
        voteService.registerVote(sessionId, userId, voteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get voting results for a session", description = "Retrieves the final vote count for a specific voting session. This endpoint should be accessed only after the session has ended.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voting results retrieved successfully", content = @Content(schema = @Schema(implementation = VoteResultResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Voting session not found")})
    @GetMapping("/voting-session/{sessionId}/results")
    public ResponseEntity<VoteResultResponseDTO> getVotingResults(@PathVariable Long sessionId) {
        return ResponseEntity.ok(voteService.calculateVotingResult(sessionId));
    }
}
