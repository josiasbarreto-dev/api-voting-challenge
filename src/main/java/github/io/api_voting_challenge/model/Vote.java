package github.io.api_voting_challenge.model;

import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.model.enums.VoteOption;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "tb_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "agenda_id"})
})
@Getter
@Builder
@Setter(AccessLevel.PRIVATE)
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

    public static Vote createVote(User user, Agenda agenda, VoteOption voteOption) {
        if (agenda == null) {
            throw new BusinessException("Agenda cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (user == null) {
            throw new BusinessException("User cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (voteOption == null) {
            throw new BusinessException("Vote option cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setAgenda(agenda);
        vote.setVoteOption(voteOption);

        return vote;
    }
}
