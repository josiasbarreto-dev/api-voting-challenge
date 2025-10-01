package github.io.api_voting_challenge.controller.docs;

import github.io.api_voting_challenge.dto.VoteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="Vote Operations", description = "Endpoints for managing Votes")
public interface VoteControllerDocs {

    @Operation(summary = "Register a vote for a specific session", description = "Allows a user to cast a 'Sim' or 'Nao' vote in an active voting session. Each user can vote only once per session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or user has already voted"),
            @ApiResponse(responseCode = "404", description = "Voting session not found or is closed"),
            @ApiResponse(responseCode = "422", description = "Validation error – one or more fields are invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> vote(@PathVariable Long sessionId, @RequestBody @Valid VoteRequest voteRequest);
}
