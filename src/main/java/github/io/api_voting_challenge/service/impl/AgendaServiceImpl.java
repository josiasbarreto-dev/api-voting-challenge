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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaServiceInterface {
    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final UserAdminRepository userAdminRepository;

    @Override
    public AgendaResponseDto createAgenda(AgendaRequestDto agendaRequestDto, Long adminId) {
        var userAdmin = userAdminRepository.findById(adminId).orElseThrow(
                () -> new UserNotFoundException("Admin user not found with id: " + adminId)
        );

        Agenda agendaToSave = Agenda.builder()
                .title(agendaRequestDto.title())
                .description(agendaRequestDto.description())
                .createdBy(userAdmin.getName())
                .creationDate(LocalDate.now())
                .status(Status.PENDING)
                .build();

        Agenda savedAgenda = agendaRepository.save(agendaToSave);
        return agendaMapper.toDto(savedAgenda);
    }
}
