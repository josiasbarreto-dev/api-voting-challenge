package github.io.api_voting_challenge.integration.controller;

import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("User Controller Integration Tests with H2 Database")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIntegrationEndToEnd {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/users";
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso e retornar status 201")
    void shouldCreateUserSuccessfullyAndReturnStatus201() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar criar usuário com dados inválidos")
    void shouldReturnStatus400WhenCreatingUserWithInvalidData() {
        UserRequest userRequest = UserFixtures.createInvalidUserRequest();

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar status 409 ao tentar criar usuário com CPF já existente")
    void shouldReturnStatus409WhenCreatingUserWithExistingCpf() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso e retornar status 200")
    void shouldUpdateUserSuccessfullyAndReturnStatus200() {
        UserRequest userRequest = UserFixtures.createValidUserRequestBuilder()
                .cpf("528.375.080-98")
                .build();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        Long userId = userRepository.findAll().get(0).getId();
        UserRequest updateRequest = UserFixtures.createUserUpdateRequest();

        restTemplate.put(
                getBaseUrl() + "/" + userId,
                updateRequest
        );

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("User Updated");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar o cpf de um usuário cadastrado")
    void shouldThrowExceptionWhenTryingToUpdateCpfOfRegisteredUser() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userRepository.findAll()).hasSize(1);

        Long userId = userRepository.findAll().get(0).getId();
        UserRequest updateRequest = UserFixtures.createUserUpdateRequestBuilder()
                .cpf(VALID_CPF_UPDATE)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + userId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findAll().get(0).getCpf()).isEqualTo(userRequest.cpf());
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar atualizar usuário inexistente")
    void shouldReturnStatus404WhenTryingToUpdateNonExistentUser() {
        UserRequest updateRequest = UserFixtures.createUserUpdateRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + INVALID_ID,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Deve recuperar usuário por ID com sucesso e retornar status 200")
    void shouldGetUserByIdSuccessfullyAndReturnStatus200() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        Long userId = userRepository.findAll().get(0).getId();

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + userId,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(VALID_CPF);
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar recuperar usuário inexistente por ID")
    void shouldReturnStatus404WhenTryingToGetNonExistentUserById() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + INVALID_ID,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve recuperar usuário por CPF com sucesso e retornar status 200")
    void shouldGetUserByCpfSuccessfullyAndReturnStatus200() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/cpf/" + VALID_CPF,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(VALID_NAME);
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar recuperar usuário inexistente por CPF")
    void shouldReturnStatus404WhenTryingToGetNonExistentUserByCpf() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/cpf/" + INVALID_CPF,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso e retornar status 204")
    void shoudlDeleteUserSuccessfullyAndReturnStatus204() {
        UserRequest userRequest = UserFixtures.createValidUserRequest();

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                userRequest,
                String.class
        );

        Long userId = userRepository.findAll().get(0).getId();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + userId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar deletar usuário inexistente")
    void shouldReturnStatus404WhenTryingToDeleteNonExistentUser() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + INVALID_ID,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve recuperar todos os usuários com sucesso e retornar status 200")
    void shouldGetAllUsersSuccessfullyAndReturnStatus200() {
        UserRequest userRequest1 = UserFixtures.createValidUserRequest();

        UserRequest userRequest2 = UserFixtures.createValidUserRequestBuilder()
                .name("User Two")
                .cpf("987.654.321-00")
                .build();

        restTemplate.postForEntity(
                getBaseUrl(),
                userRequest1,
                String.class
        );

        restTemplate.postForEntity(
                getBaseUrl(),
                userRequest2,
                String.class
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/all",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("User Test");
        assertThat(response.getBody()).contains("User Two");
    }
}
