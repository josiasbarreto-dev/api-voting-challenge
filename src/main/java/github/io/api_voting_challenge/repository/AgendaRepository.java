package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
