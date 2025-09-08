package github.io.api_voting_challenge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

@Builder
public record AdminUserRequest(
        @NotBlank(message = "Name cannot be empty")
        @Pattern(regexp = "^[A-Z]+(.)*", message = "Name must start with an uppercase letter and can only contain letters and spaces.")
        String name,

        @NotBlank(message = "CPF cannot be empty")
        @CPF
        String cpf,

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.")
        String password
) {}
