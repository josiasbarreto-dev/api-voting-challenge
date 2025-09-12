package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.AgendaControllerDocs;
import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agendas")
public class AgendaController implements AgendaControllerDocs {
    private final AgendaService agendaService;

    @Override
    @PostMapping
    public ResponseEntity<AgendaResponse> create(@RequestBody @Valid AgendaRequest agendaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.create(agendaRequest));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponse> update(@PathVariable Long id, @RequestBody @Valid AgendaRequest agendaRequest) {
        return ResponseEntity.ok(agendaService.update(id, agendaRequest));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.getById(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<AgendaResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(agendaService.getAll(pageable));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agendaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
