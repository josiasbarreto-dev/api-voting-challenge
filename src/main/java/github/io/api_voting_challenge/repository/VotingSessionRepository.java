package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
}
