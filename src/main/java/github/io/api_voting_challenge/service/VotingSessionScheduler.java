package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.response.VotingSessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VotingSessionScheduler {
    Page<VotingSessionResponse> getOpenVotingSessions(Pageable pageable);
    void checkExpiredVotingSessions();
}
