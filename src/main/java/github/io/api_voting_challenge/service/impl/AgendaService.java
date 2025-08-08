package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AgendaRequestDto;
import github.io.api_voting_challenge.dto.AgendaResponseDto;
import github.io.api_voting_challenge.exception.UserNotFoundException;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.repository.UserAdminRepository;
import github.io.api_voting_challenge.service.AgendaServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class AgendaService implements AgendaServiceInterface {
    AgendaRepository agendaRepository;
    AgendaMapper agendaMapper;
    UserAdminRepository userAdminRepository;

    public AgendaService(AgendaRepository agendaRepository, AgendaMapper agendaMapper, UserAdminRepository userAdminRepository) {
        this.agendaRepository = agendaRepository;
        this.agendaMapper = agendaMapper;
        this.userAdminRepository = userAdminRepository;
    }

    @Override
    public AgendaResponseDto createAgenda(AgendaRequestDto agendaRequestDto, Long adminId) {
        var userAdmin = userAdminRepository.findById(adminId).orElseThrow(
                () -> new UserNotFoundException("Admin user not found with id: " + adminId)
        );
        Agenda agendaToSave = agendaMapper.toEntity(agendaRequestDto);
        agendaToSave.setCreatedBy(userAdmin.getName());
        agendaToSave.setCreationDate(LocalDate.now());
        agendaToSave.setStatus(Status.PENDING);

        Agenda savedAgenda = agendaRepository.save(agendaToSave);
        savedAgenda.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return agendaMapper.toDto(savedAgenda);
    }
}
