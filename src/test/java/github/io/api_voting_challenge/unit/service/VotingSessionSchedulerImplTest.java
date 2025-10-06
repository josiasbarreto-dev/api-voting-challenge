package github.io.api_voting_challenge.unit.service;

import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.impl.VotingSessionSchedulerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static github.io.api_voting_challenge.fixtures.TestConstants.UPDATED_AGENDAS_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VotingSessionSchedulerImpl Unit Tests")
class VotingSessionSchedulerImplTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private VotingSessionMapper votingSessionMapper;

    @InjectMocks
    private VotingSessionSchedulerImpl votingSessionScheduler;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 10, 1, 10, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );

        ReflectionTestUtils.setField(votingSessionScheduler, "clock", fixedClock);
    }

    @Test
    @DisplayName("Deve fechar sessoes de votacao expiradas e atualizar agendas em bulk")
    void shouldCloseExpiredVotingSessions() {
        LocalDateTime now = LocalDateTime.now(fixedClock);

        VotingSession expiredSession1 = VotingSessionFixtures.createExpiredVotingSession(fixedClock);
        VotingSession expiredSession2 = VotingSessionFixtures.createExpiredVotingSession(fixedClock);
        List<VotingSession> expiredSessions = List.of(expiredSession1, expiredSession2);

        when(votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS))
                .thenReturn(expiredSessions);

        when(agendaRepository.bulkUpdateStatusForExpiredSessions(Status.CLOSED, now))
                .thenReturn(UPDATED_AGENDAS_COUNT);

        votingSessionScheduler.checkExpiredVotingSessions();

        verify(votingSessionRepository, times(1)).findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS);
        verify(agendaRepository, times(1)).bulkUpdateStatusForExpiredSessions(Status.CLOSED, now);
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Nao deve fazer nada se nao houver sessoes de votacao expiradas")
    void shouldDoNothingIfNoExpiredVotingSessions() {
        LocalDateTime now = LocalDateTime.now(fixedClock);

        when(votingSessionRepository.findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS))
                .thenReturn(Collections.emptyList());

        when(agendaRepository.bulkUpdateStatusForExpiredSessions(Status.CLOSED, now))
                .thenReturn(0);

        votingSessionScheduler.checkExpiredVotingSessions();

        verify(votingSessionRepository, times(1)).findByEndTimeBeforeAndAgendaStatus(now, Status.IN_PROGRESS);
        verify(agendaRepository, times(1)).bulkUpdateStatusForExpiredSessions(Status.CLOSED, now);
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve retornar sessoes de votacao abertas com sucesso")
    void shouldReturnOpenVotingSessionsSuccessfully() {
        LocalDateTime now = LocalDateTime.now(fixedClock);
        VotingSession openSession = VotingSessionFixtures.createOpenVotingSession();
        Pageable pageable = mock(Pageable.class);
        Page<VotingSession> sessionsPage = new PageImpl<>(Collections.singletonList(openSession));
        VotingSessionResponse responseDto = VotingSessionResponse.builder().id(2L).build();

        when(votingSessionRepository.findByEndTimeAfter(now, pageable))
                .thenReturn(sessionsPage);

        when(votingSessionMapper.toDto(any(VotingSession.class)))
                .thenReturn(responseDto);

        Page<VotingSessionResponse> result = votingSessionScheduler.getOpenVotingSessions(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));

        verify(votingSessionRepository, times(1)).findByEndTimeAfter(now, pageable);
        verify(votingSessionMapper, times(1)).toDto(openSession);
    }
}