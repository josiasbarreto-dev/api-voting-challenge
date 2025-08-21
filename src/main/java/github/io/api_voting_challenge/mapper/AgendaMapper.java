package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AgendaRequestDto;
import github.io.api_voting_challenge.dto.AgendaResponseDto;
import github.io.api_voting_challenge.model.Agenda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AgendaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "votingSession", ignore = true)
    Agenda toEntity(AgendaRequestDto agendaRequestDto);
    AgendaResponseDto toDto(Agenda agenda);
}
