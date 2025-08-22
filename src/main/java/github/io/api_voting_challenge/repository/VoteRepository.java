package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.enums.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByUserIdAndAgenda_Id(Long userId, Long votingSessionId);
    long countByAgendaIdAndVoteOption(Long agendaId, VoteOption voteOption);
}
