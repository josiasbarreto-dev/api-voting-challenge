package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;

public interface VotingSessionService {
    VotingSessionResponse openVotingSession(VotingSessionRequest votingSessionRequest);
    VoteResultResponse calculateVotingResult(Long sessionId);
}
