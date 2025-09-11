package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VoteRequest;

public interface VoteService {
    void registerVote(Long sessionId, VoteRequest voteRequest);
}
