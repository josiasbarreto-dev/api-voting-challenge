package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.Role;
import lombok.Builder;

@Builder
public record VoterResponse(
        Long id,
        String name,
        String cpf,
        Role role
) {}
