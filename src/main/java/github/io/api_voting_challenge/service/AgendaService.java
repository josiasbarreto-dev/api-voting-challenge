package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;

public interface AgendaService {
    AgendaResponse createAgenda(AgendaRequest agendaRequest);
}
