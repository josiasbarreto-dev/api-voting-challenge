package github.io.api_voting_challenge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record AdminUserRequestDto(
        @NotBlank(message = "Name cannot be empty")
        @Pattern(regexp = "^[A-Z]+(.)*", message = "Name must start with an uppercase letter and can only contain letters and spaces.")
        String name,

        @NotBlank(message = "CPF cannot be empty")
        @CPF
        String cpf,

        @NotBlank(message = "Email cannot be empty")
        @Email
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.")
        String password
) {}
