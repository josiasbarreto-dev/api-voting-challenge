package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.Status;

public record VotingSessionResponseDto(
        Long id,
        Integer durationInMinutes,
        String startTime,
        String endTime,
        Status status,
        Long agendaId
) {}
