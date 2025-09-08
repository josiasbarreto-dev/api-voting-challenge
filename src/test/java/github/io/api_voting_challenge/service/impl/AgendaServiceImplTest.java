package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.fixtures.AdminFixtures;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.UserAdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaServiceImpl Test")
public class AgendaServiceImplTest {
    @InjectMocks
    private AgendaServiceImpl agendaServiceImpl;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private AgendaMapper agendaMapper;

    @Mock
    private UserAdminRepository userAdminRepository;

    @Test
    @DisplayName("Deve criar uma agenda com sucesso")
    void shouldCreateAgendaSuccessfully(){
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();
        AgendaResponse agendaResponse = AgendaFixtures.createAgendaResponse();
        Agenda agendaEntity = AgendaFixtures.createAgenda();
        AdminUser adminUser = AdminFixtures.createValidAdminUserEntity();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.of(adminUser));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agendaEntity);
        when(agendaMapper.toDto(any(Agenda.class))).thenReturn(agendaResponse);

        AgendaResponse result = agendaServiceImpl.createAgenda(agendaRequest, VALID_ID);

        assertNotNull(result);
        assertEquals(agendaResponse, result);
        verifyNoMoreInteractions(agendaMapper, agendaRepository, userAdminRepository);
    }

    @Test
    @DisplayName("Deve retornar UserNotFoundException quando o admin não for encontrado")
    void shouldThrowUserNotFoundExceptionWhenAdminNotFound() {
        AgendaRequest agendaRequest = AgendaFixtures.createValidAgendaRequest();

        when(userAdminRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> {
                    agendaServiceImpl.createAgenda(agendaRequest, VALID_ID);
                });
        String message = String.format("Admin user not found with id: %d", VALID_ID);

        assertEquals(message, exception.getMessage());
        verifyNoMoreInteractions(agendaMapper, agendaRepository, userAdminRepository);
    }
}
