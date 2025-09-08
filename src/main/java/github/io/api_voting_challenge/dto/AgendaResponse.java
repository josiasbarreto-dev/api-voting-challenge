package github.io.api_voting_challenge.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AgendaResponse(
        Long id,
        String title,
        String description,
        String status,
        LocalDate creationDate,
        String createdBy
) {}
