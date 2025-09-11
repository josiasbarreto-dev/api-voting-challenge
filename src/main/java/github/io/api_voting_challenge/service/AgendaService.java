package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgendaService {
    AgendaResponse create(AgendaRequest agendaRequest);
    AgendaResponse update(Long id, AgendaRequest agendaRequest);
    AgendaResponse getById(Long id);
    Page<AgendaResponse> getAll(Pageable pageable);
    void delete(Long id);
}
