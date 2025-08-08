package github.io.api_voting_challenge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VotingSessionRequestDto(
        @NotNull(message = "Duration in minutes cannot be null.")
        @Min(value = 1, message = "Duration must be at least 1 minute.")
        Integer durationInMinutes
) {}
