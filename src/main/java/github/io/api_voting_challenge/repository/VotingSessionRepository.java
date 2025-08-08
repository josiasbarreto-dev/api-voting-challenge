package github.io.api_voting_challenge.repository;

import github.io.api_voting_challenge.model.enums.Status;
import github.io.api_voting_challenge.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    List<VotingSession> findByEndTimeBeforeAndAgendaStatus(LocalDateTime time, Status status);
    List<VotingSession> findByEndTimeAfter(LocalDateTime now);

    @Query("SELECT vs FROM VotingSession vs WHERE vs.endTime > :now AND vs.agenda.status = github.io.api_voting_challenge.model.enums.Status.IN_PROGRESS")
    List<VotingSession> findActiveVotingSessions(@Param("now") LocalDateTime now);
}
