package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AgendaRequestDto;
import github.io.api_voting_challenge.dto.AgendaResponseDto;
import github.io.api_voting_challenge.model.Agenda;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgendaMapper {
    AgendaResponseDto toDto(Agenda agenda);
    Agenda toEntity(AgendaRequestDto agendaRequestDto);
}
