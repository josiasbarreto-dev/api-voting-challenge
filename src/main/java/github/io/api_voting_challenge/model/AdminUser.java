package github.io.api_voting_challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@SuperBuilder
public class AdminUser extends VotingUser{
    private String email;
    private String password;
}
