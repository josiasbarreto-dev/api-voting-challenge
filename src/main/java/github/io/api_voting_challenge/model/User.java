package github.io.api_voting_challenge.model;

import github.io.api_voting_challenge.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "tb_users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String cpf;

    public void updateName(String newName){
        if(newName == null || newName.isBlank()){
            throw new BusinessException("Name cannot be null or blank", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.name = newName.trim();
    }

    public void validateCpfImmutability(String newCpf) {
        if (!this.cpf.equals(newCpf)) {
            throw new BusinessException("Cannot change the CPF of an existing User.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
