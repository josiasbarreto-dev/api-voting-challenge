package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VotingSessionRequestDto;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VotingSessionServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VotingSessionServiceImpl implements VotingSessionServiceInterface {
    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;
    private final VotingSessionMapper votingSessionMapper;

public VotingSessionServiceImpl(VotingSessionRepository votingSessionRepository, AgendaRepository agendaRepository, VotingSessionMapper votingSessionMapper) {
        this.votingSessionRepository = votingSessionRepository;
        this.agendaRepository = agendaRepository;
        this.votingSessionMapper = votingSessionMapper;
    }

    @Override
    public VotingSessionResponseDto openVotingSession(Long id, VotingSessionRequestDto votingSessionRequestDto) {
        Agenda agenda = agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );

        if (agenda.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Voting session can only be created for agendas with status PENDING.");
        }

        int durationMinutes = votingSessionRequestDto.durationInMinutes() != null ? votingSessionRequestDto.durationInMinutes() : 1;

        VotingSession votingSession = new VotingSession();
        votingSession.setAgenda(agenda);
        votingSession.setDurationInMinutes(durationMinutes);
        LocalDateTime now = LocalDateTime.now();
        votingSession.setStartTime(now);
        votingSession.setEndTime(now.plusMinutes(durationMinutes));
        agenda.setStatus(Status.IN_PROGRESS);

        votingSession = votingSessionRepository.save(votingSession);

        agenda.setVotingSession(votingSession);

        agenda = agendaRepository.save(agenda);

        votingSession.setAgenda(agenda);

        return votingSessionMapper.toDto(votingSession);
    }
}
