package github.io.api_voting_challenge.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        String cpf
) {}
