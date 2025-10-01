package github.io.api_voting_challenge.unit.controller;

import github.io.api_voting_challenge.controller.AgendaController;
import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.service.AgendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Agenda Controller Unit Tests")
public class AgendaControllerUnitTest {
    @Mock
    private AgendaService agendaService;

    @InjectMocks
    private AgendaController agendaController;

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar o status 201")
    void shouldCreateAgendaSuccessfullyAndReturnStatus201() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.create(agendaRequest)).thenReturn(agendaResponse);
        ResponseEntity<AgendaResponse> response = agendaController.create(agendaRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(agendaResponse, response.getBody());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar uma pauta com dados inválidos")
    void shouldThrowExceptionWhenCreatingAgendaWithInvalidData() {
        AgendaRequest invalidAgendaRequest = AgendaFixtures.createInvalidAgendaRequest();

        String errorMensage = "Validation failed for one or more fields";
        when(agendaService.create(invalidAgendaRequest)).thenThrow(new IllegalArgumentException(errorMensage));
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendaController.create(invalidAgendaRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso e retornar o status 200")
    void shouldUpdateAgendaSuccessfullyAndReturnStatus200(){
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.update(VALID_ID, agendaRequest)).thenReturn(agendaResponse);
        ResponseEntity<AgendaResponse> response = agendaController.update(VALID_ID, agendaRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(agendaResponse, response.getBody());
        verify(agendaService).update(VALID_ID, agendaRequest);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar uma pauta inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentAgenda(){
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        String errorMensage = "Agenda not found with id: " + INVALID_ID;
        when(agendaService.update(INVALID_ID, agendaRequest)).thenThrow(new AgendaNotFoundException(errorMensage));
        Exception exception = assertThrows(
                AgendaNotFoundException.class,
                () -> agendaController.update(INVALID_ID, agendaRequest)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve buscar uma pauta por ID com sucesso e retornar o status 200")
    void shouldGetAgendaByIdSuccessfullyAndReturnStatus200() {
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.getById(VALID_ID)).thenReturn(agendaResponse);
        ResponseEntity<AgendaResponse> response = agendaController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(agendaResponse, response.getBody());
        verify(agendaService).getById(VALID_ID);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar uma pauta por ID inexistente")
    void shouldThrowExceptionWhenGettingPautaByNonExistentId() {
        String errorMensage = "Pauta not found with id: " + INVALID_ID;
        when(agendaService.getById(INVALID_ID)).thenThrow(new AgendaNotFoundException(errorMensage));
        Exception exception = assertThrows(
                AgendaNotFoundException.class,
                () -> agendaController.getById(INVALID_ID)
        );

        assertEquals(errorMensage, exception.getMessage());
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de usuários com sucesso e retornar o status 200")
    void shouldListAgendasSuccessfullyAndReturnStatus200() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AgendaResponse> agendaPage = AgendaFixtures.createAgendaResponsePage(15, pageable);

        when(agendaService.getAll(pageable)).thenReturn(agendaPage);
        ResponseEntity<Page<AgendaResponse>> response = agendaController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(agendaPage, response.getBody());
        verify(agendaService).getAll(pageable);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve deletar uma pauta com sucesso e retornar o status 204")
    void shouldDeleteAgendaSuccessfullyAndReturnStatus204() {
        doNothing().when(agendaService).delete(VALID_ID);
        ResponseEntity<Void> response = agendaController.delete(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agendaService).delete(VALID_ID);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar uma pauta inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentAgenda() {
        String errorMensage = "Agenda not found with id: " + INVALID_ID;
        doThrow(new AgendaNotFoundException(errorMensage)).when(agendaService).delete(INVALID_ID);
        Exception exception = assertThrows(
                AgendaNotFoundException.class,
                () -> agendaController.delete(INVALID_ID)
        );

        assertEquals(errorMensage, exception.getMessage());
        verify(agendaService).delete(INVALID_ID);
        verifyNoMoreInteractions(agendaService);
    }
}
