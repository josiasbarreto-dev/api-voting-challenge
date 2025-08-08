package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.Status;
import jakarta.validation.constraints.NotBlank;

public record AgendaRequestDto(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotBlank(message = "Status cannot be blank")
        Status status
) {}
