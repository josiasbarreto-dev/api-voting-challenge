package github.io.api_voting_challenge.dto.response;

import github.io.api_voting_challenge.model.enums.Status;
import lombok.Builder;

@Builder
public record VotingSessionResponse(
        Long id,
        Integer durationInMinutes,
        String startTime,
        String endTime,
        Status status,
        Long agendaId
) {}
