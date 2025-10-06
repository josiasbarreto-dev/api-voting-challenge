package github.io.api_voting_challenge.unit.service;

import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.service.impl.AgendaServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Agenda Service Unit Tests")
public class AgendaServiceImplUnitTest {
    @InjectMocks
    private AgendaServiceImpl agendaService;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private AgendaMapper agendaMapper;

    @Test
    @DisplayName("Deve criar uma Pauta com sucesso")
    void shouldCreateAgendaSuccessfully() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        Agenda agendaEntity = AgendaFixtures.createAgendaBuilder().build();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponseBuilder().build();
        ArgumentCaptor<Agenda> captor = ArgumentCaptor.forClass(Agenda.class);

        when(agendaRepository.save(any(Agenda.class))).thenReturn(agendaEntity);
        when(agendaMapper.toDto(any(Agenda.class))).thenReturn(agendaResponse);

        AgendaResponse result = agendaService.create(agendaRequest);

        assertEquals(result, agendaResponse);

        verify(agendaMapper).toDto(agendaEntity);
        verify(agendaRepository).save(captor.capture());
        verifyNoMoreInteractions(agendaMapper, agendaRepository);
    }

    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso")
    void shouldUpdateAgendaSuccessfully() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        Agenda agendaEntity = AgendaFixtures.createAgendaBuilder().build();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponseBuilder().build();
        ArgumentCaptor<Agenda> captor = ArgumentCaptor.forClass(Agenda.class);

        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agendaEntity));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agendaEntity);
        when(agendaMapper.toDto(any(Agenda.class))).thenReturn(agendaResponse);

        AgendaResponse result = agendaService.update(VALID_ID, agendaRequest);

        assertEquals(result, agendaResponse);

        verify(agendaRepository).findById(VALID_ID);
        verify(agendaRepository).save(captor.capture());
        verify(agendaMapper).toDto(agendaEntity);
        verifyNoMoreInteractions(agendaMapper, agendaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar pauta com id inválido")
    void shouldThrowExceptionWhenUpdatingAgendaWithInvalidId() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        when(agendaRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                AgendaNotFoundException.class, () -> {
                    agendaService.update(INVALID_ID, agendaRequest);
                });

        String message = "Agenda not found with ID: "+ INVALID_ID;
        assertEquals(message, exception.getMessage());

        verify(agendaRepository).findById(INVALID_ID);
        verifyNoMoreInteractions(agendaRepository, agendaMapper);
    }

    @Test
    @DisplayName("Deve buscar Pauta por ID com sucesso")
    void shouldGetAgendaByIdSuccessfully() {
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();
        Agenda agendaEntity = AgendaFixtures.createAgenda();

        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agendaEntity));
        when(agendaMapper.toDto(agendaEntity)).thenReturn(agendaResponse);

        AgendaResponse result = agendaService.getById(VALID_ID);

        assertNotNull(result);
        assertEquals(agendaResponse, result);

        verify(agendaRepository).findById(VALID_ID);
        verify(agendaMapper).toDto(agendaEntity);
        verifyNoMoreInteractions(agendaRepository, agendaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar Pauta por ID inválido")
    void shouldThrowExceptionWhenGettingAgendaByInvalidId() {
        when(agendaRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                AgendaNotFoundException.class, () -> {
                    agendaService.getById(INVALID_ID);
                });
        String message = "Agenda not found with ID: "+ INVALID_ID;
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(agendaRepository, agendaMapper);
    }

    @Test
    @DisplayName("Deve deletar Pauta com sucesso")
    void shouldDeleteAgendaSuccessfully() {
        Agenda agendaEntity = AgendaFixtures.createAgenda();

        when(agendaRepository.findById(VALID_ID)).thenReturn(Optional.of(agendaEntity));
        doNothing().when(agendaRepository).delete(agendaEntity);

        agendaService.delete(VALID_ID);

        verify(agendaRepository).findById(VALID_ID);
        verify(agendaRepository).delete(agendaEntity);
        verifyNoMoreInteractions(agendaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar Pauta com ID inválido")
    void shouldThrowExceptionWhenDeletingAgendaWithInvalidId() {
        when(agendaRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                AgendaNotFoundException.class, () -> {
                    agendaService.delete(INVALID_ID);
                });
        String message = "Agenda not found with ID: " + INVALID_ID;
        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(agendaRepository, agendaMapper);
    }
}
