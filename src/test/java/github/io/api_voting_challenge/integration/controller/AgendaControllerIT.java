package github.io.api_voting_challenge.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.io.api_voting_challenge.controller.AgendaController;
import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.exception.GlobalExceptionHandler;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.service.AgendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("Agenda Controller Integration Tests")
@WebMvcTest({AgendaController.class, GlobalExceptionHandler.class})
public class AgendaControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendaService agendaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar status 201")
    void shouldCreateAgendaSuccessfullyAndReturnStatus201() throws Exception {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.create(agendaRequest)).thenReturn(agendaResponse);

        mockMvc.perform(post("/api/v1/agendas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(agendaResponse.id()))
                .andExpect(jsonPath("$.title").value(agendaResponse.title()))
                .andExpect(jsonPath("$.description").value(agendaResponse.description()))
                .andExpect(jsonPath("$.status").value(agendaResponse.status().toString()))
                .andExpect(jsonPath("$.creationDate").value(agendaResponse.creationDate().toString()));

        verify(agendaService, times(1)).create(agendaRequest);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao tentar criar uma pauta com dados inválidos")
    void shouldReturnStatusUnprocessableEntityWhenCreatingAgendaWithInvalidData() throws Exception {
        AgendaRequest invalidAgendaRequest = AgendaFixtures.createInvalidAgendaRequest();

        mockMvc.perform(post("/api/v1/agendas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAgendaRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso e retornar status 200")
    void shouldUpdateAgendaSuccessfullyAndReturnStatus200() throws Exception {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.update(VALID_ID, agendaRequest)).thenReturn(agendaResponse);

        mockMvc.perform(put("/api/v1/agendas/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(agendaResponse.id()))
                .andExpect(jsonPath("$.title").value(agendaResponse.title()))
                .andExpect(jsonPath("$.description").value(agendaResponse.description()))
                .andExpect(jsonPath("$.status").value(agendaResponse.status().toString()))
                .andExpect(jsonPath("$.creationDate").value(agendaResponse.creationDate().toString()));

        verify(agendaService, times(1)).update(VALID_ID, agendaRequest);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar atualizar uma pauta inexistente")
    void shouldReturnStatusNotFoundWhenUpdatingNonExistentAgenda() throws Exception {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        when(agendaService.update(INVALID_ID, agendaRequest)).thenThrow(new AgendaNotFoundException("Agenda not found"));

        mockMvc.perform(put("/api/v1/agendas/{id}", INVALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isNotFound());

        verify(agendaService, times(1)).update(INVALID_ID, agendaRequest);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao tentar atualizar uma pauta com dados inválidos")
    void shouldReturnStatusUnprocessableEntityWhenUpdatingAgendaWithInvalidData() throws Exception {
        AgendaRequest invalidAgendaRequest = AgendaFixtures.createInvalidAgendaRequest();

        mockMvc.perform(put("/api/v1/agendas/{id}", VALID_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidAgendaRequest)))
                .andExpect(status().isUnprocessableEntity());

        verifyNoInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve buscar uma pauta por ID com sucesso e retornar status 200")
    void shouldGetAgendaByIdSuccessfullyAndReturnStatus200() throws Exception {
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();

        when(agendaService.getById(VALID_ID)).thenReturn(agendaResponse);

        mockMvc.perform(get("/api/v1/agendas/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(agendaResponse.id()))
                .andExpect(jsonPath("$.title").value(agendaResponse.title()))
                .andExpect(jsonPath("$.description").value(agendaResponse.description()))
                .andExpect(jsonPath("$.status").value(agendaResponse.status().toString()))
                .andExpect(jsonPath("$.creationDate").value(agendaResponse.creationDate().toString()));

        verify(agendaService, times(1)).getById(VALID_ID);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar buscar uma pauta inexistente por ID")
    void shouldReturnStatusNotFoundWhenGettingNonExistentAgendaById() throws Exception {
        when(agendaService.getById(INVALID_ID)).thenThrow(new AgendaNotFoundException("Agenda not found"));

        mockMvc.perform(get("/api/v1/agendas/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(agendaService, times(1)).getById(INVALID_ID);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar uma lista paginada de pautas com sucesso e retornar status 200")
    void shouldGetAllAgendasSuccessfullyAndReturnStatus200() throws Exception {
        var pageable = Pageable.ofSize(10).withPage(0);
        var agendaResponsePage = AgendaFixtures.createAgendaResponsePage(10, pageable);

        when(agendaService.getAll(pageable)).thenReturn(agendaResponsePage);

        mockMvc.perform(get("/api/v1/agendas")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(agendaResponsePage.getContent().size()))
                .andExpect(jsonPath("$.totalElements").value(agendaResponsePage.getTotalElements()))
                .andExpect(jsonPath("$.totalPages").value(agendaResponsePage.getTotalPages()))
                .andExpect(jsonPath("$.number").value(agendaResponsePage.getNumber()))
                .andExpect(jsonPath("$.size").value(agendaResponsePage.getSize()));

        verify(agendaService, times(1)).getAll(pageable);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve deletar uma pauta com sucesso e retornar status 204")
    void shouldDeleteAgendaSuccessfullyAndReturnStatus204() throws Exception {
        doNothing().when(agendaService).delete(VALID_ID);

        mockMvc.perform(delete("/api/v1/agendas/{id}", VALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(agendaService, times(1)).delete(VALID_ID);
        verifyNoMoreInteractions(agendaService);
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao tentar deletar uma pauta inexistente")
    void shouldReturnStatusNotFoundWhenDeletingNonExistentAgenda() throws Exception {
        doThrow(new AgendaNotFoundException("Agenda not found")).when(agendaService).delete(INVALID_ID);

        mockMvc.perform(delete("/api/v1/agendas/{id}", INVALID_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(agendaService, times(1)).delete(INVALID_ID);
        verifyNoMoreInteractions(agendaService);
    }
}
