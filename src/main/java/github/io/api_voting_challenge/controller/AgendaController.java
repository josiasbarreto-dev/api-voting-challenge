package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.AgendaControllerDocs;
import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import github.io.api_voting_challenge.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agendas")
public class AgendaController implements AgendaControllerDocs {
    private final AgendaService agendaService;

    @Override
    @PostMapping
    public ResponseEntity<AgendaResponse> create(@RequestBody @Valid AgendaRequest agendaRequest) {
        log.info("Received request to create agenda: {}", agendaRequest.title());
        AgendaResponse agendaResponse = agendaService.create(agendaRequest);
        log.info("Agenda with id {} created successfully.", agendaResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaResponse);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> update(@PathVariable Long id, @RequestBody @Valid AgendaRequest agendaRequest) {
        log.info("Received request to update agenda with id {}: {}", id, agendaRequest.title());
        AgendaResponse agendaResponse = agendaService.update(id, agendaRequest);
        log.info("Agenda with id {} updated successfully.", agendaResponse.id());
        return ResponseEntity.ok(agendaResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> getById(@PathVariable Long id) {
        log.info("Received request to get agenda with id: {}", id);
        AgendaResponse agendaResponse = agendaService.getById(id);
        log.info("Agenda with id {} retrieved successfully.", agendaResponse.id());
        return ResponseEntity.ok(agendaResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<AgendaResponse>> getAll(Pageable pageable) {
        log.info("Received request to get all agendas - page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<AgendaResponse> agendaResponse = agendaService.getAll(pageable);
        log.info("Agendas retrieved successfully: {}", agendaResponse.getTotalElements());
        return ResponseEntity.ok(agendaResponse);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request to delete agenda with id: {}", id);
        agendaService.delete(id);
        log.info("Agenda with id {} deleted successfully.", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
