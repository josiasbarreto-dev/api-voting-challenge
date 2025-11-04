package github.io.api_voting_challenge.controller.docs;

import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Agenda Operations", description = "Endpoints for managing Agenda")
public interface AgendaControllerDocs {
    @Operation(summary = "Create a new agenda for voting", description = "Allows an user to create a new agenda. The agenda is initially in an inactive state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agenda created successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "422", description = "Validation error – one or more fields are invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<AgendaResponse> create(@RequestBody @Valid AgendaRequest agendaRequest);

    @Operation(summary = "Update an existing agenda", description = "Updates the information of an agenda by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda updated successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Agenda not found"),
            @ApiResponse(responseCode = "422", description = "Validation error – one or more fields are invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<AgendaResponse> update(@PathVariable Long id, @RequestBody @Valid AgendaRequest agendaRequest);

    @Operation(summary = "Get agenda by ID", description = "Retrieves an agenda's details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda found successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agenda not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<AgendaResponse> getById(@PathVariable Long id);

    @Operation(summary = "Get a list of all agendas", description = "Retrieves all agendas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of agendas returned successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Page<AgendaResponse>> getAll(Pageable pageable);

    @Operation(summary = "Delete an agenda by ID", description = "Deletes an agenda by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agenda deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Agenda not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> delete(@PathVariable Long id);
}
