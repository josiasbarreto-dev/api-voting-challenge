package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.service.AdminService;
import github.io.api_voting_challenge.service.AgendaService;
import github.io.api_voting_challenge.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Operations", description = "Endpoints for managing admin users, agendas, and voting sessions.")
public class AdminController {
    private final AdminService adminService;
    private final VotingSessionService votingSessionService;
    private final AgendaService agendaService;

    @Operation(summary = "Create a new admin user", description = "Creates a new admin user with a unique CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin user created successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request "),
            @ApiResponse(responseCode = "409", description = "Admin user with the given CPF already exists")})
    @PostMapping
    public ResponseEntity<AdminUserResponse> create(@RequestBody @Valid AdminUserRequest requestAdmin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(requestAdmin));
    }

    @Operation(summary = "Update an existing admin user", description = "Updates the information of an admin user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user updated successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> update(@PathVariable Long id, @RequestBody @Valid AdminUserRequest requestAdmin) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.update(id, requestAdmin));
    }

    @Operation(summary = "Get admin user by ID", description = "Retrieves an admin user's details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getById(id));
    }

    @Operation(summary = "Delete an admin user", description = "Deletes an admin user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Admin user deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get admin user by email", description = "Retrieves an admin user's details by their email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/email")
    public ResponseEntity<AdminUserResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getByEmail(email));
    }

    @Operation(summary = "Get admin user by CPF", description = "Retrieves an admin user's details by their CPF.")
    @ApiResponses(value = {@
            ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/cpf")
    public ResponseEntity<AdminUserResponse> getByCpf(@RequestParam String cpf) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getByCpf(cpf));
    }

    @Operation(summary = "Create a new agenda for voting", description = "Allows an admin to create a new agenda. The agenda is initially in an inactive state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agenda created successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @PostMapping("/{id}/agenda")
    public ResponseEntity<AgendaResponse> createAgenda(@RequestBody @Valid AgendaRequest agendaRequest, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.createAgenda(agendaRequest, id));
    }

    @Operation(summary = "Open a new voting session for an agenda", description = "Starts a voting session for a specific agenda. The session duration can be specified, or it defaults to 1 minute.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voting session opened successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or voting session already exists"),
            @ApiResponse(responseCode = "404", description = "Agenda not found")})
    @PostMapping("/agenda/{id}/voting-session")
    public ResponseEntity<VotingSessionResponse> createVotingSession(@PathVariable Long id, @RequestBody VotingSessionRequest votingSessionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionService.openVotingSession(id, votingSessionRequest));
    }

    @Operation(summary = "Create a new voter user", description = "Creates a new voter user. This user can cast a vote in an open session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voter user created successfully", content = @Content(schema = @Schema(implementation = VoterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")})
    @PostMapping("/voters")
    public ResponseEntity<VoterResponse> createVoter(@RequestBody @Valid VoterRequest voterRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createVoter(voterRequest));
    }
}