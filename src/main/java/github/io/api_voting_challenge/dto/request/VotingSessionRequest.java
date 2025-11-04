package github.io.api_voting_challenge.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VotingSessionRequest(
        @NotNull(message = "Agenda ID cannot be null.")
        Long agendaId,
        @NotNull(message = "Duration in minutes cannot be null.")
        @Min(value = 1, message = "Duration must be at least 1 minute.")
        Integer durationInMinutes
) {}
