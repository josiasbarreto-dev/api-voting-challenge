package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VotingSessionRequestDto;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;

public interface VotingSessionServiceInterface {
    VotingSessionResponseDto openVotingSession(Long id, VotingSessionRequestDto votingSessionRequestDto);
}
