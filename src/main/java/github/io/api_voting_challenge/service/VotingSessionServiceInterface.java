package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;

public interface VotingSessionServiceInterface {
    VotingSessionResponse openVotingSession(Long id, VotingSessionRequest votingSessionRequest);
}
