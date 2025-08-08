package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;

public interface VoteServiceInterface {
    void registerVote(Long sessionId, Long userId, VoteRequestDTO voteRequest);
    VoteResultResponseDTO calculateVotingResult(Long sessionId);
}
