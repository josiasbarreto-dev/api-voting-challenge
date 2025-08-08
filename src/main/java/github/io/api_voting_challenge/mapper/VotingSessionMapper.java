package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.VotingSessionRequestDto;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.model.VotingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface VotingSessionMapper {
    @Mapping(source = "durationInMinutes", target = "durationInMinutes")
    @Mapping(source = "agenda.status", target = "status")
    @Mapping(source = "agenda.id", target = "agendaId")
    @Mapping(source = "startTime", target = "startTime", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "endTime", target = "endTime", qualifiedByName = "localDateTimeToString")
    VotingSessionResponseDto toDto(VotingSession votingSession);

    VotingSession toEntity(VotingSessionRequestDto votingSessionRequestDto);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return localDateTime.format(formatter);
    }
}
