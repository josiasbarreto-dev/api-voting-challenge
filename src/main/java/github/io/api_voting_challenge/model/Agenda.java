package github.io.api_voting_challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agenda {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDate creationDate;
}
