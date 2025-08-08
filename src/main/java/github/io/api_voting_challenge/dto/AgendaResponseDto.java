package github.io.api_voting_challenge.dto;

import java.time.LocalDate;

public record AgendaResponseDto(
        Long id,
        String title,
        String description,
        String status,
        LocalDate creationDate,
        String createdBy
) {}
