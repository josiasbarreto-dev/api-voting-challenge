package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.model.VotingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface VotingSessionMapper {
    @Mapping(source = "agenda.id", target = "agendaId")
    @Mapping(source = "agenda.status", target = "status")
    @Mapping(source = "startTime", target = "startTime", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "endTime", target = "endTime", qualifiedByName = "localDateTimeToString")
    VotingSessionResponseDto toDto(VotingSession votingSession);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }
}