package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.service.AgendaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    @Override
    public AgendaResponse create(AgendaRequest agendaRequest) {
        Agenda agendaToSave = Agenda.builder()
                .title(agendaRequest.title())
                .description(agendaRequest.description())
                .creationDate(LocalDate.now())
                .status(Status.PENDING)
                .build();

        return agendaMapper.toDto(agendaRepository.save(agendaToSave));
    }

    @Override
    public AgendaResponse update(Long id, AgendaRequest agendaRequest) {
        Agenda existingAgenda = agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );

        existingAgenda.setTitle(agendaRequest.title());
        existingAgenda.setDescription(agendaRequest.description());

        return agendaMapper.toDto(agendaRepository.save(existingAgenda));
    }

    @Override
    public AgendaResponse getById(Long id) {
        return agendaMapper.toDto(getAgenda(id));
    }

    public Page<AgendaResponse> getAll(Pageable pageable){
        return agendaRepository.findAll(pageable).map(agendaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        agendaRepository.delete(getAgenda(id));
    }

    private Agenda getAgenda(Long id) {
        return agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );
    }
}
