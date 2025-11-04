package github.io.api_voting_challenge.unit.model;

import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Unit")
@DisplayName("User Model Unit Tests")
public class UserUnitTest {

    @Test
    @DisplayName("Deve atualizar o nome de um usuário com sucesso")
    void shouldUpdateUserNameSuccessfully() {
        User user = UserFixtures.createValidUserEntity();
        String newName = "New Name";

        user.updateName(newName);

        assert user.getName().equals(newName);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar o nome de um usuário com valor nulo ou em branco")
    void shouldThrowExceptionWhenUpdatingUserNameWithNullOrBlankValue() {
        User user = UserFixtures.createValidUserEntity();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> user.updateName(null)
        );

        assertEquals("Name cannot be null or blank", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());

        BusinessException exceptionBlank = assertThrows(
                BusinessException.class,
                () -> user.updateName("   ")
        );

        assertEquals("Name cannot be null or blank", exceptionBlank.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionBlank.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar o CPF de um usuário existente")
    void shouldThrowExceptionWhenChangingCpfOfExistingUser() {
        User user = UserFixtures.createValidUserEntity();
        String newCpf = "467.334.290-98";

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> user.validateCpfImmutability(newCpf)
        );

        assertEquals("Cannot change the CPF of an existing User.", exception.getMessage());
    }
}
