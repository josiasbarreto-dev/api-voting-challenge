package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.VoteOption;
import jakarta.validation.constraints.NotNull;

public record VoteRequestDTO (
        @NotNull(message = "Vote option cannot be null.")
        VoteOption voteOption
){}
