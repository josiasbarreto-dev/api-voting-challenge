package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.VoteOption;

public record VoteResponseDTO(
    Long voteId,
    Long userId,
    Long agendaId,
    VoteOption voteOption
) {}
