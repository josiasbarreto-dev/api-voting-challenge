package github.io.api_voting_challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

@Builder
public record UserRequest(
        @NotBlank(message = "Name cannot be empty")
        @Pattern(regexp = "^[A-Z]+(.)*", message = "Name must start with an uppercase letter and can only contain letters and spaces.")
        String name,

        @NotBlank(message = "CPF cannot be empty")
        @CPF
        String cpf
) {}
