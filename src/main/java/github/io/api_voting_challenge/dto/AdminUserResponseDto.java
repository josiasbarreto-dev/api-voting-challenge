package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.Role;

public record AdminUserResponseDto(
        Long id,
        String name,
        String cpf,
        String email,
        Role role
) {}
