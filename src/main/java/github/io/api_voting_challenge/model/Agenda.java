package github.io.api_voting_challenge.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="agendas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDate creationDate;
    @OneToOne(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private VotingSession votingSession;
}
