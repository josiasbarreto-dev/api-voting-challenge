package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
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
    Agenda toEntity(AgendaRequest agendaRequest);
    AgendaResponse toDto(Agenda agenda);
}
