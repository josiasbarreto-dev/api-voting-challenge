package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VotingSessionSchedulerInterface;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VotingSessionSchedulerImpl implements VotingSessionSchedulerInterface {
    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VotingSessionMapper votingSessionMapper;

    public VotingSessionSchedulerImpl(AgendaRepository agendaRepository, VotingSessionRepository votingSessionRepository, VotingSessionMapper votingSessionMapper) {
        this.agendaRepository = agendaRepository;
        this.votingSessionRepository = votingSessionRepository;
        this.votingSessionMapper = votingSessionMapper;
    }

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
    public List<VotingSessionResponseDto> getOpenVotingSessions() {
        List<VotingSession> openSessions = votingSessionRepository.findByEndTimeAfter(LocalDateTime.now());

        return openSessions.stream()
                .map(votingSessionMapper::toDto)
                .collect(Collectors.toList());
    }
}
