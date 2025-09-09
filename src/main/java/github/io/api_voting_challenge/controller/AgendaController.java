package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agendas")
public class AgendaController {
    private AgendaService agendaService;

    @Operation(summary = "Create a new agenda for voting", description = "Allows an admin to create a new agenda. The agenda is initially in an inactive state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agenda created successfully", content = @Content(schema = @Schema(implementation = AgendaResponse.class)))})
    @PostMapping
    public ResponseEntity<AgendaResponse> createAgenda(@RequestBody @Valid AgendaRequest agendaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.createAgenda(agendaRequest));
    }
}
