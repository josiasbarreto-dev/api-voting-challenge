package github.io.api_voting_challenge.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Entity
@Table(name = "tb_agendas")
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate creationDate;

    @OneToOne(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private VotingSession votingSession;

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException("Title cannot be null or blank", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.setTitle(title);
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException("Description cannot be null or blank", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.setDescription(description);
    }

    public void updateStatus(Status status) {
        if (status == null) {
            throw new BusinessException("Status cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.setStatus(status);
    }

    public void openSession(VotingSession votingSession) {
        if (votingSession == null || this.status != Status.PENDING) {
            throw new BusinessException("Cannot open voting session. Agenda must be in PENDING status and voting session cannot be null.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        this.setStatus(Status.IN_PROGRESS);
        this.setVotingSession(votingSession);
    }

    public boolean isValidForSession(){
        return this.status == Status.PENDING;
    }

}
