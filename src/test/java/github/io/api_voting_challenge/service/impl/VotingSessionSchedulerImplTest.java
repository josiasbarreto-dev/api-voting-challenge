package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionSchedulerImplTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private VotingSessionMapper votingSessionMapper;

    @InjectMocks
    private VotingSessionSchedulerImpl votingSessionScheduler;

    @Test
    @DisplayName("Deve fechar sessoes de votacao expiradas")
    void shouldCloseExpiredVotingSessions() {
        VotingSession expiredSession = VotingSessionFixtures.createExpiredVotingSession();
        Agenda expiredAgenda = expiredSession.getAgenda();
        List<VotingSession> expiredSessions = Collections.singletonList(expiredSession);
        when(votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(any(LocalDateTime.class), eq(Status.IN_PROGRESS)))
                .thenReturn(expiredSessions);

        votingSessionScheduler.checkExpiredVotingSessions();

        assertEquals(Status.CLOSED, expiredAgenda.getStatus());
        verify(votingSessionRepository).findByEndTimeBeforeAndAgendaStatus(any(LocalDateTime.class), eq(Status.IN_PROGRESS));
        verify(agendaRepository).save(expiredAgenda);
    }

    @Test
    @DisplayName("Nao deve fazer nada se nao houver sessoes de votacao expiradas")
    void shouldDoNothingIfNoExpiredVotingSessions() {
        when(votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(any(LocalDateTime.class), eq(Status.IN_PROGRESS)))
                .thenReturn(Collections.emptyList());

        votingSessionScheduler.checkExpiredVotingSessions();

        verify(votingSessionRepository).findByEndTimeBeforeAndAgendaStatus(any(LocalDateTime.class), eq(Status.IN_PROGRESS));
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve retornar sessoes de votacao abertas com sucesso")
    void shouldReturnOpenVotingSessionsSuccessfully() {
        VotingSession openSession = VotingSessionFixtures.createOpenVotingSession();
        Pageable pageable = mock(Pageable.class);
        Page<VotingSession> sessionsPage = new PageImpl<>(Collections.singletonList(openSession));
        VotingSessionResponseDto responseDto = VotingSessionResponseDto.builder().id(2L).build();

        when(votingSessionRepository.findByEndTimeAfter(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(sessionsPage);
        when(votingSessionMapper.toDto(any(VotingSession.class)))
                .thenReturn(responseDto);

        Page<VotingSessionResponseDto> result = votingSessionScheduler.getOpenVotingSessions(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));
        verify(votingSessionRepository).findByEndTimeAfter(any(LocalDateTime.class), eq(pageable));
        verify(votingSessionMapper).toDto(openSession);
    }
}