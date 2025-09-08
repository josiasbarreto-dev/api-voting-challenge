package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class VotingSessionServiceImpl implements VotingSessionService {
    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;
    private final VotingSessionMapper votingSessionMapper;

    @Override
    public VotingSessionResponse openVotingSession(Long id, VotingSessionRequest votingSessionRequest) {
        Agenda agenda = agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );

        if (agenda.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Voting session can only be created for agendas with status PENDING.");
        }

        int durationMinutes = votingSessionRequest.durationInMinutes() != null ? votingSessionRequest.durationInMinutes() : 1;
        LocalDateTime now = LocalDateTime.now();

        VotingSession votingSession = VotingSession.builder()
                .agenda(agenda)
                .durationInMinutes(durationMinutes)
                .startTime(now)
                .endTime(now.plusMinutes(durationMinutes))
                .build();

        agenda.setStatus(Status.IN_PROGRESS);
        agenda.setVotingSession(votingSession);

        Agenda savedAgenda = agendaRepository.save(agenda);

        return votingSessionMapper.toDto(savedAgenda.getVotingSession());
    }
}
