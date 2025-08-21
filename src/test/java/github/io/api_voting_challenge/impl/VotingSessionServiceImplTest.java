package github.io.api_voting_challenge.impl;

import github.io.api_voting_challenge.dto.VotingSessionRequestDto;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.VotingSessionMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.impl.VotingSessionServiceImpl;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VotingSessionServiceImpl Unit Tests")
public class VotingSessionServiceImplTest {
    @InjectMocks
    private VotingSessionServiceImpl votingSessionService;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VotingSessionMapper votingSessionMapper;


    @Test
    @DisplayName("A sessão de votação deve ser aberta com sucesso quando a pauta estiver PENDENTE")
    void shouldOpenVotingSessionSuccessfullyWhenAgendaIsPending() {
        Agenda agenda = AgendaFixtures.createAgenda();

        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createValidVotingSessionRequestDto();
        VotingSessionResponseDto votingSessionResponseDto = VotingSessionFixtures.createVotingSessionResponseDto();

        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(agenda)).thenReturn(agenda);
        doReturn(votingSessionResponseDto).when(votingSessionMapper).toDto(any(VotingSession.class));

        VotingSessionResponseDto result = votingSessionService.openVotingSession(1L, votingSessionRequestDto);

        assertNotNull(result);
        assertEquals(votingSessionResponseDto, result);
        assertEquals(Status.IN_PROGRESS, agenda.getStatus());

        verifyNoMoreInteractions(agendaRepository, votingSessionRepository, votingSessionMapper);
    }

    @Test
    @DisplayName("Deverá abrir a sessão de votação com duração padrão de um minuto")
    void shouldOpenVotingSessionWithDefaultDurationOfOneMinute() {
        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createInvalidVotingSessionRequestDto();
        Agenda agenda = AgendaFixtures.createAgenda();


        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(agenda)).thenReturn(agenda);

        votingSessionService.openVotingSession(1L, votingSessionRequestDto);

        ArgumentCaptor<Agenda> agendaCaptor = ArgumentCaptor.forClass(Agenda.class);
        verify(agendaRepository, times(1)).save(agendaCaptor.capture());

        Agenda capturedAgenda = agendaCaptor.getValue();
        assertNotNull(capturedAgenda.getVotingSession());
        assertEquals(1, capturedAgenda.getVotingSession().getDurationInMinutes());
    }

    @Test
    @DisplayName("Deve lançar AgendaNotFoundException quando a agenda não existir")
    void shouldThrowAgendaNotFoundExceptionWhenAgendaDoesNotExist() {
        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createInvalidVotingSessionRequestDto();

        when(agendaRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        AgendaNotFoundException exception = assertThrows(AgendaNotFoundException.class, () ->
                votingSessionService.openVotingSession(INVALID_ID, votingSessionRequestDto)
        );

        assertEquals("Agenda not found with ID: " + INVALID_ID, exception.getMessage());

        verifyNoInteractions(votingSessionRepository);
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando a agenda não estiver PENDENTE")
    void shouldThrowIllegalStateExceptionWhenAgendaIsNotPending() {
        Agenda agenda = AgendaFixtures.createAgenda();
        agenda.setStatus(Status.IN_PROGRESS);

        VotingSessionRequestDto votingSessionRequestDto = VotingSessionFixtures.createInvalidVotingSessionRequestDto();

        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agenda));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                votingSessionService.openVotingSession(VALID_ID, votingSessionRequestDto)
        );

        verify(agendaRepository, times(1)).findById(VALID_ID);
        verify(agendaRepository, never()).save(any(Agenda.class));
    }
}
