package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByCpf(String cpf);
}
