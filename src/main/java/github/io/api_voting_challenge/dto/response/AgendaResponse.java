package github.io.api_voting_challenge.dto.response;

import github.io.api_voting_challenge.model.enums.Status;
import lombok.Builder;

@Builder
public record AgendaResponse (
        Long id,
        String title,
        String description,
        Status status,
        String creationDate
) {}
