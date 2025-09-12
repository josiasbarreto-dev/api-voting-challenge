package github.io.api_voting_challenge.controller.docs;

import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="Voting Session Operations", description = "Endpoints for managing Voting Sessions")
public interface VotingSessionControllerDocs {
    @Operation(summary = "Open a new voting session for an agenda", description = "Starts a voting session for a specific agenda. The session duration can be specified, or it defaults to 1 minute.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voting session opened successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or voting session already exists"),
            @ApiResponse(responseCode = "404", description = "Agenda not found")
    })
    ResponseEntity<VotingSessionResponse> create(@RequestBody VotingSessionRequest votingSessionRequest);

    @Operation(summary = "Get a list of all open voting sessions", description = "Retrieves all voting sessions that are currently active and open for voting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of open voting sessions returned successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponse.class)))
    })
    ResponseEntity<Page<VotingSessionResponse>> getOpenVotingSessions(@ParameterObject Pageable pageable);

    @Operation(summary = "Get voting results for a session", description = "Retrieves the final vote count for a specific voting session. This endpoint should be accessed only after the session has ended.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voting results retrieved successfully", content = @Content(schema = @Schema(implementation = VoteResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Voting session not found")
    })
    ResponseEntity<VoteResultResponse> getVotingResults(@PathVariable Long sessionId);
}
