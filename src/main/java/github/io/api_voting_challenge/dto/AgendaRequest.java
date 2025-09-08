package github.io.api_voting_challenge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AgendaRequest(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description
) {}
