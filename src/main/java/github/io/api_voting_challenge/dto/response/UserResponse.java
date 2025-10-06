package github.io.api_voting_challenge.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        String cpf
) {}
