package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VotingSessionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VotingSessionSchedulerImpl implements VotingSessionScheduler {
    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VotingSessionMapper votingSessionMapper;

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkExpiredVotingSessions() {
        log.info("Checking for expired voting sessions...");
        LocalDateTime now = LocalDateTime.now();

        List<VotingSession> expiredSessions = getExpiredSessions(now);

        for (VotingSession session : expiredSessions) {
            log.info("Getting agenda for voting session id: {}", session.getId());
            Agenda agenda = session.getAgenda();

            agenda.setStatus(Status.CLOSED);
            log.info("Agenda id: {} status updated to CLOSED", agenda.getId());

            agendaRepository.save(agenda);
            log.info("Agenda id: {} saved successfully", agenda.getId());
        }
    }

    private List<VotingSession> getExpiredSessions(LocalDateTime now) {
        return votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS);
    }

    @Override
    public Page<VotingSessionResponse> getOpenVotingSessions(Pageable pageable) {
        return votingSessionRepository.findByEndTimeAfter(LocalDateTime.now(), pageable)
                .map(votingSessionMapper::toDto);
    }
}
