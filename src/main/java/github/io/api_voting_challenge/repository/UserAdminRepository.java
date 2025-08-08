package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAdminRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<AdminUser> findByCpf(String cpf);
}
