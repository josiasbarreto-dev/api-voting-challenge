package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VotingSessionSchedulerInterface {
    Page<VotingSessionResponseDto> getOpenVotingSessions(Pageable pageable);
    void checkExpiredVotingSessions();
}
