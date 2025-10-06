package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.request.VotingSessionRequest;
import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VotingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VotingSessionServiceImpl implements VotingSessionService {
    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;
    private final VotingSessionMapper votingSessionMapper;

    @Override
    public VotingSessionResponse openVotingSession(VotingSessionRequest votingSessionRequest) {
        log.info("Opening voting session for agenda ID: {}", votingSessionRequest.agendaId());
        Agenda agenda = getAgenda(votingSessionRequest.agendaId());

        log.info("Validating agenda status for ID: {}", votingSessionRequest.agendaId());
        if (agenda.getStatus() != Status.PENDING) {
            log.error("Cannot open voting session. Agenda ID: {} has status: {}", votingSessionRequest.agendaId(), agenda.getStatus());
            throw new IllegalStateException("Voting session can only be created for agendas with status PENDING.");
        }

        int durationMinutes = votingSessionRequest.durationInMinutes() != null ? votingSessionRequest.durationInMinutes() : 1;
        log.error("Setting voting session duration to {} minutes for agenda ID: {}", durationMinutes, votingSessionRequest.agendaId());

        LocalDateTime now = LocalDateTime.now();

        VotingSession votingSession = VotingSession.builder()
                .agenda(agenda)
                .durationInMinutes(durationMinutes)
                .startTime(now)
                .endTime(now.plusMinutes(durationMinutes))
                .build();

        agenda.setStatus(Status.IN_PROGRESS);
        agenda.setVotingSession(votingSession);

        log.info("Saving voting session for agenda ID: {}", votingSessionRequest.agendaId());
        Agenda savedAgenda = agendaRepository.save(agenda);

        log.info("Voting session opened successfully with ID: {} for agenda ID: {}", savedAgenda.getVotingSession().getId(), votingSessionRequest.agendaId());
        return votingSessionMapper.toDto(savedAgenda.getVotingSession());
    }

    private Agenda getAgenda(Long agendaId) {
        return agendaRepository.findById(agendaId).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + agendaId)
        );
    }
}
