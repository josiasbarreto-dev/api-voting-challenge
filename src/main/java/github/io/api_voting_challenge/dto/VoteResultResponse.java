package github.io.api_voting_challenge.dto;

import lombok.Builder;

@Builder
public record VoteResultResponse(
        String message,
        Long yesVotes,
        Long noVotes
){}
