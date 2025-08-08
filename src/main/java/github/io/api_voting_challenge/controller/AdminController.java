package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.dto.*;
import github.io.api_voting_challenge.service.impl.AdminServiceImpl;
import github.io.api_voting_challenge.service.impl.AgendaServiceImpl;
import github.io.api_voting_challenge.service.impl.VotingSessionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminServiceImpl adminServiceImpl;
    private final AgendaServiceImpl agendaServiceImpl;
    private final VotingSessionServiceImpl votingSessionServiceImpl;

    public AdminController(AdminServiceImpl adminServiceImpl, VotingSessionServiceImpl votingSessionServiceImpl, AgendaServiceImpl agendaServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
        this.votingSessionServiceImpl = votingSessionServiceImpl;
        this.agendaServiceImpl = agendaServiceImpl;
    }

    @PostMapping
    ResponseEntity<AdminUserResponseDto> create(@RequestBody @Valid AdminUserRequestDto requestAdmin){
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.create(requestAdmin));
    }

    @PutMapping("/{id}")
    ResponseEntity<AdminUserResponseDto> update(@PathVariable Long id, @RequestBody AdminUserRequestDto userAdmin) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.update(id, userAdmin));
    }

    @GetMapping("/{id}")
    ResponseEntity<AdminUserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        adminServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/email")
    ResponseEntity<AdminUserResponseDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getByEmail(email));
    }

    @GetMapping("/cpf")
    ResponseEntity<AdminUserResponseDto> getByCpf(@RequestParam String cpf) {
        return ResponseEntity.status(HttpStatus.OK).body(adminServiceImpl.getByCpf(cpf));
    }

    @PostMapping("/{id}/agenda")
    ResponseEntity<AgendaResponseDto> createAgenda(@RequestBody AgendaRequestDto agendaRequestDto, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaServiceImpl.createAgenda(agendaRequestDto, id));
    }

    @PostMapping("/agenda/{id}/voting-session")
    ResponseEntity<VotingSessionResponseDto> createVotingSession(@PathVariable Long id, @RequestBody VotingSessionRequestDto votingSessionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingSessionServiceImpl.openVotingSession(id, votingSessionRequestDto));
    }

    @PostMapping("/voters")
    ResponseEntity<VoterResponseDto> createVoter(@RequestBody @Valid VoterRequestDto voterRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.createVoter(voterRequestDto));
    }
}
