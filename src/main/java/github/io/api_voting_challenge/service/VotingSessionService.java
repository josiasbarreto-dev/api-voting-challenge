package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.request.VotingSessionRequest;
import github.io.api_voting_challenge.dto.response.VotingSessionResponse;

public interface VotingSessionService {
    VotingSessionResponse openVotingSession(VotingSessionRequest votingSessionRequest);
}
