package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.AgendaRequestDto;
import github.io.api_voting_challenge.dto.AgendaResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface AgendaServiceInterface {
    AgendaResponseDto createAgenda(AgendaRequestDto agendaRequestDto, Long adminId);
}
