package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import github.io.api_voting_challenge.exception.AgendaNotFoundException;
import github.io.api_voting_challenge.mapper.AgendaMapper;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.repository.AgendaRepository;
import github.io.api_voting_challenge.service.AgendaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    @Override
    public AgendaResponse create(AgendaRequest agendaRequest) {
        log.info("Creating new agenda with title: {}", agendaRequest.title());
        Agenda agendaToSave = Agenda.builder()
                .title(agendaRequest.title())
                .description(agendaRequest.description())
                .creationDate(LocalDate.now())
                .status(Status.PENDING)
                .build();

        Agenda agenda = agendaRepository.save(agendaToSave);
        log.info("Agenda created with ID: {}", agenda.getId());

        return agendaMapper.toDto(agenda);
    }

    @Override
    public AgendaResponse update(Long id, AgendaRequest agendaRequest) {
        log.info("Updating agenda with ID: {}", id);
        Agenda existingAgenda = agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );

        existingAgenda.setTitle(agendaRequest.title());
        existingAgenda.setDescription(agendaRequest.description());

        Agenda agenda = agendaRepository.save(existingAgenda);
        log.info("Agenda with ID: {} updated successfully.", agenda.getId());
        
        return agendaMapper.toDto(agenda);
    }

    @Override
    public AgendaResponse getById(Long id) {
        log.info("Retrieving agenda with ID: {}", id);
        Agenda agenda = getAgenda(id);
        log.info("Agenda with ID: {} retrieved successfully.", agenda.getId());
        
        return agendaMapper.toDto(agenda);
    }

    public Page<AgendaResponse> getAll(Pageable pageable) {
        log.info("Retrieving all agendas - page: {}, size: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Agenda> allAgendas = agendaRepository.findAll(pageable);
        log.info("Total agendas retrieved: {}", allAgendas.getTotalElements());

        return allAgendas.map(agendaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting agenda with ID: {}", id);
        Agenda agenda = getAgenda(id);

        agendaRepository.delete(agenda);
        log.info("Agenda with ID: {} deleted successfully.", id);
    }

    private Agenda getAgenda(Long id) {
        return agendaRepository.findById(id).orElseThrow(
                () -> new AgendaNotFoundException("Agenda not found with ID: " + id)
        );
    }
}
