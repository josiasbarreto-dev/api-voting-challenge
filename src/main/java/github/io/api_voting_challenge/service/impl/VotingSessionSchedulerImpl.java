package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VotingSessionSchedulerImpl implements VotingSessionScheduler {
    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VotingSessionMapper votingSessionMapper;
    private final Clock clock;

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkExpiredVotingSessions() {
        log.info("Checking for expired voting sessions...");
        LocalDateTime now = LocalDateTime.now(clock);

        List<VotingSession> expiredSessions = getExpiredSessions(now);

        int updatedCount = agendaRepository.bulkUpdateStatusForExpiredSessions(Status.CLOSED, now);
        log.info("Found {} expired voting sessions. Updated {} agendas to CLOSED status.", expiredSessions.size(), updatedCount);
    }

    private List<VotingSession> getExpiredSessions(LocalDateTime now) {
        return votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS);
    }

    @Override
    public Page<VotingSessionResponse> getOpenVotingSessions(Pageable pageable) {
        return votingSessionRepository.findByEndTimeAfter(LocalDateTime.now(clock), pageable)
                .map(votingSessionMapper::toDto);
    }
}
