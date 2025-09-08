package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.dto.VoteResultResponse;

public interface VoteServiceInterface {
    void registerVote(Long sessionId, Long userId, VoteRequest voteRequest);
    VoteResultResponse calculateVotingResult(Long sessionId);
}
