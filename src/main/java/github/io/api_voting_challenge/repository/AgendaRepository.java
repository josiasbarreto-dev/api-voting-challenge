package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Agenda agenda SET agenda.status = :status " +
            "WHERE agenda.id IN (SELECT session.agenda.id FROM VotingSession session " +
            "WHERE session.endTime < :now)")
    int bulkUpdateStatusForExpiredSessions(@Param("status") Status status,
                                           @Param("now") LocalDateTime now);
}
//Percorre todas as sessões de votação expiradas e modifica o status da pauta associada para "CLOSED", salvando as alterações no repositório de pautas.
