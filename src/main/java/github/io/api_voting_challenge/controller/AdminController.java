package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.service.impl.AdminServiceImpl;
import github.io.api_voting_challenge.service.impl.AgendaServiceImpl;
import github.io.api_voting_challenge.service.impl.VotingSessionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Operations", description = "Endpoints for managing admin users, agendas, and voting sessions.")
public class AdminController {
    private final AdminServiceImpl adminServiceImpl;
    private final AgendaServiceImpl agendaServiceImpl;
    private final VotingSessionServiceImpl votingSessionServiceImpl;

    public AdminController(AdminServiceImpl adminServiceImpl, VotingSessionServiceImpl votingSessionServiceImpl, AgendaServiceImpl agendaServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
        this.votingSessionServiceImpl = votingSessionServiceImpl;
        this.agendaServiceImpl = agendaServiceImpl;
    }

    @Operation(summary = "Create a new admin user", description = "Creates a new admin user with a unique CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin user created successfully", content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request "),
            @ApiResponse(responseCode = "409", description = "Admin user with the given CPF already exists")})
    @PostMapping
    public ResponseEntity<AdminUserResponseDto> create(@RequestBody @Valid AdminUserRequestDto requestAdmin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.create(requestAdmin));
    }

    @Operation(summary = "Update an existing admin user", description = "Updates the information of an admin user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user updated successfully", content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponseDto> update(@PathVariable Long id, @RequestBody AdminUserRequestDto userAdmin) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.update(id, userAdmin));
    }

    @Operation(summary = "Get admin user by ID", description = "Retrieves an admin user's details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getById(id));
    }

    @Operation(summary = "Delete an admin user", description = "Deletes an admin user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Admin user deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get admin user by email", description = "Retrieves an admin user's details by their email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/email")
    public ResponseEntity<AdminUserResponseDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getByEmail(email));
    }

    @Operation(summary = "Get admin user by CPF", description = "Retrieves an admin user's details by their CPF.")
    @ApiResponses(value = {@
            ApiResponse(responseCode = "200", description = "Admin user found successfully", content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @GetMapping("/cpf")
    public ResponseEntity<AdminUserResponseDto> getByCpf(@RequestParam String cpf) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getByCpf(cpf));
    }

    @Operation(summary = "Create a new agenda for voting", description = "Allows an admin to create a new agenda. The agenda is initially in an inactive state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agenda created successfully", content = @Content(schema = @Schema(implementation = AgendaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Admin user not found")})
    @PostMapping("/{id}/agenda")
    public ResponseEntity<AgendaResponseDto> createAgenda(@RequestBody AgendaRequestDto agendaRequestDto, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaServiceImpl.createAgenda(agendaRequestDto, id));
    }

    @Operation(summary = "Open a new voting session for an agenda", description = "Starts a voting session for a specific agenda. The session duration can be specified, or it defaults to 1 minute.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voting session opened successfully", content = @Content(schema = @Schema(implementation = VotingSessionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or voting session already exists"),
            @ApiResponse(responseCode = "404", description = "Agenda not found")})
    @PostMapping("/agenda/{id}/voting-session")
    public ResponseEntity<VotingSessionResponseDto> createVotingSession(@PathVariable Long id, @RequestBody VotingSessionRequestDto votingSessionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionServiceImpl.openVotingSession(id, votingSessionRequestDto));
    }

    @Operation(summary = "Create a new voter user", description = "Creates a new voter user. This user can cast a vote in an open session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voter user created successfully", content = @Content(schema = @Schema(implementation = VoterResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists")})
    @PostMapping("/voters")
    public ResponseEntity<VoterResponseDto> createVoter(@RequestBody @Valid VoterRequestDto voterRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.createVoter(voterRequestDto));
    }
}