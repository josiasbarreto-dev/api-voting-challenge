package github.io.api_voting_challenge.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import github.io.api_voting_challenge.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_voting_sessions")
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer durationInMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(name = "agenda_id")
    @JsonBackReference
    private Agenda agenda;

    public void updateEndTime(LocalDateTime endTime){
        if (endTime == null) {
            throw new BusinessException("End time cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.setEndTime(endTime);
    }

    public void updateAgenda(Agenda agenda){
        if (agenda == null) {
            throw new BusinessException("Agenda cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.setAgenda(agenda);
    }

    public static VotingSession createSession (Agenda agenda, int durationInMinutes) {
        if (agenda == null) {
            throw new BusinessException("Agenda cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        LocalDateTime now = LocalDateTime.now();

        VotingSession votingSession = new VotingSession();
        votingSession.agenda = agenda;
        votingSession.startTime = now;
        votingSession.endTime = now.plusMinutes(durationInMinutes);
        votingSession.durationInMinutes = durationInMinutes;
        return votingSession;
    }

    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
}
