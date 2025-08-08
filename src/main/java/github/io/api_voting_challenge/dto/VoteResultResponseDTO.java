package github.io.api_voting_challenge.dto;

public record VoteResultResponseDTO (
        String message,
        Long yesVotes,
        Long noVotes
){}
