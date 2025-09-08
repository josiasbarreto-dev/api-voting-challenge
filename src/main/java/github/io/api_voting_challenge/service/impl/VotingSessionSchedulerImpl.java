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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VotingSessionSchedulerImpl implements VotingSessionScheduler {
    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VotingSessionMapper votingSessionMapper;

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkExpiredVotingSessions() {
        LocalDateTime now = LocalDateTime.now();


        List<VotingSession> expiredSessions = votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS);

        for (VotingSession session : expiredSessions) {
            Agenda agenda = session.getAgenda();
            agenda.setStatus(Status.CLOSED);
            agendaRepository.save(agenda);
        }
    }

    @Override
    public Page<VotingSessionResponse> getOpenVotingSessions(Pageable pageable) {
        return votingSessionRepository.findByEndTimeAfter(LocalDateTime.now(), pageable)
                .map(votingSessionMapper::toDto);
    }
}
