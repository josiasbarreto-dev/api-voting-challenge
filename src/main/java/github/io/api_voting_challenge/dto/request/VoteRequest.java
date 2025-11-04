package github.io.api_voting_challenge.dto.request;

import github.io.api_voting_challenge.model.enums.VoteOption;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VoteRequest(
        @NotNull(message = "User ID cannot be null.")
        Long userId,

        @NotNull(message = "Vote option cannot be null.")
        VoteOption voteOption
){}