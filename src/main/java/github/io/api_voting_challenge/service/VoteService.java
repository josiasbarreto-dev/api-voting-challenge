package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;

public interface VoteService {
    void registerVote(Long sessionId, VoteRequest voteRequest);
    VoteResultResponse calculateVotingResult(Long sessionId);
}
