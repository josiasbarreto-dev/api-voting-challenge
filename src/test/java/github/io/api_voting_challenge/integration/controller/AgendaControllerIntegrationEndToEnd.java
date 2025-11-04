package github.io.api_voting_challenge.integration.controller;

import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.repository.AgendaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_ID;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("Agenda Controller Integration End-to-End Tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AgendaControllerIntegrationEndToEnd {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AgendaRepository agendaRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/agendas";
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar o status 201")
    void shouldCreateAgendaSuccessfullyAndReturnStatus201() {
        AgendaRequest request = AgendaFixtures.createValidAgendaRequest();

        var response = restTemplate.postForEntity(getBaseUrl(), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(agendaRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar criar uma pauta com dados inválidos")
    void shouldReturnStatus400WhenCreatingAgendaWithInvalidData() {
        AgendaRequest request = AgendaFixtures.createInvalidAgendaRequest();

        var response = restTemplate.postForEntity(getBaseUrl(), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(agendaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar uma pauta com sucesso e retornar o status 200")
    void shouldUpdateAgendaSuccessfullyAndReturnStatus200() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        var createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                agendaRequest,
                String.class
        );

        Long userId = agendaRepository.findAll().get(0).getId();
        AgendaRequest updateRequest = AgendaFixtures.createValidAgendaRequestBuilder()
                .title("User Updated")
                .description("User description updated")
                .build();

        restTemplate.put(
                getBaseUrl() + "/" + userId,
                updateRequest
        );

        var getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("User Updated");
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar atualizar uma pauta com dados inválidos")
    void shouldReturnStatus400WhenUpdatingAgendaWithInvalidData() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        var createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                agendaRequest,
                String.class
        );

        Long agendaId = agendaRepository.findAll().get(0).getId();
        AgendaRequest updateRequest = AgendaFixtures.createInvalidAgendaRequest();

        HttpEntity<AgendaRequest> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + agendaId,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Deve recuperar uma pauta por ID com sucesso e retornar o status 200")
    void shouldGetAgendaByIdSuccessfullyAndReturnStatus200() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        var createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                agendaRequest,
                String.class
        );

        Long agendaId = agendaRepository.findAll().get(0).getId();

        var getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + agendaId,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains(agendaRequest.title());
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar recuperar uma pauta inexistente por ID")
    void shouldReturnStatus404WhenGettingNonExistentAgendaById() {
        var getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + INVALID_ID,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve deletar uma pauta com sucesso e retornar o status 204")
    void shouldDeleteAgendaSuccessfullyAndReturnStatus204() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        var createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                agendaRequest,
                String.class
        );

        Long agendaId = agendaRepository.findAll().get(0).getId();

        restTemplate.delete(getBaseUrl() + "/" + agendaId);

        var getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + agendaId,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar deletar uma pauta inexistente")
    void shouldReturnStatus404WhenDeletingNonExistentAgenda() {
        var deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + INVALID_ID,
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve listar todas as pautas com sucesso e retornar o status 200")
    void shouldListAllAgendasSuccessfullyAndReturnStatus200() {
        AgendaRequest agendaRequest1 = AgendaFixtures.createValidAgendaRequestBuilder()
                .title("Agenda 1")
                .build();
        AgendaRequest agendaRequest2 = AgendaFixtures.createValidAgendaRequestBuilder()
                .title("Agenda 2")
                .build();

        restTemplate.postForEntity(getBaseUrl(), agendaRequest1, String.class);
        restTemplate.postForEntity(getBaseUrl(), agendaRequest2, String.class);

        var listResponse = restTemplate.getForEntity(
                getBaseUrl(),
                String.class
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Agenda 1", "Agenda 2");
    }
}
