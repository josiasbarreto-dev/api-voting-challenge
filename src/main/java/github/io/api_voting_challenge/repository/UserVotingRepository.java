package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.VotingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVotingRepository extends JpaRepository<VotingUser, Long> {
    boolean existsByCpf(String cpf);
}
