package github.io.api_voting_challenge.model;

import github.io.api_voting_challenge.model.enums.VoteOption;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "agenda_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteOption voteOption;
}
