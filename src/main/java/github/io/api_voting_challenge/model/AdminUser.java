package github.io.api_voting_challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser extends VotingUser{
    private String email;
    private String password;
}
