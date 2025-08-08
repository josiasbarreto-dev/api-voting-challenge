package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VotingSessionResponseDto;

import java.util.List;

public interface VotingSessionSchedulerInterface {
    List<VotingSessionResponseDto> getOpenVotingSessions();
    void checkExpiredVotingSessions();
}
