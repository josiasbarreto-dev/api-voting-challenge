package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.Role;

public record VoterResponseDto(
        Long id,
        String name,
        String cpf,
        Role role
) {}
