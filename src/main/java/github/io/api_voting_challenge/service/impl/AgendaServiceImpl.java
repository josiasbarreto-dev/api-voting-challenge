package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.service.AgendaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    @Override
    public AgendaResponse createAgenda(AgendaRequest agendaRequest) {
        Agenda agendaToSave = Agenda.builder()
                .title(agendaRequest.title())
                .description(agendaRequest.description())
                .creationDate(LocalDate.now())
                .status(Status.PENDING)
                .build();

        Agenda savedAgenda = agendaRepository.save(agendaToSave);
        return agendaMapper.toDto(savedAgenda);
    }
}
