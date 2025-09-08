package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.AdminUserRequest;
import github.io.api_voting_challenge.dto.AdminUserResponse;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.enums.Role;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;

public class AdminFixtures {
    public static AdminUserRequest.AdminUserRequestBuilder createValidAdminUserRequestBuilder() {
        return AdminUserRequest.builder()
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD);
    }

    public static AdminUserRequest.AdminUserRequestBuilder createInvalidAdminUserRequestBuilder() {
        return AdminUserRequest.builder()
                .name("admin Test")
                .cpf(INVALID_CPF)
                .email(INVALID_EMAIL)
                .password("12345678");
    }

    public static AdminUserResponse.AdminUserResponseBuilder createAdminUserResponseBuilder() {
        return AdminUserResponse.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf("528.375.080-98")
                .email(VALID_EMAIL)
                .role(Role.ADMIN);
    }

    public static AdminUserRequest.AdminUserRequestBuilder createAdminUserUpdateRequestBuilder() {
        return AdminUserRequest.builder()
                .name(UPDATED_NAME)
                .cpf(VALID_CPF)
                .email("email@test.com")
                .password(UPDATED_PASSWORD);
    }

    public static AdminUserResponse.AdminUserResponseBuilder createAdminUserResponseAfterUpdateBuilder() {
        return AdminUserResponse.builder()
                .id(VALID_ID)
                .name("User Updated")
                .cpf(VALID_CPF)
                .email("emailupdated@test.com")
                .role(Role.ADMIN);
    }

    public static AdminUser.AdminUserBuilder buildValidAdminUserEntity() {
        return AdminUser.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .role(Role.ADMIN)
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD);
    }
    public static AdminUserRequest createValidAdminUserRequest() {
        return createValidAdminUserRequestBuilder().build();
    }

    public static AdminUserRequest createInvalidAdminUserRequest() {
        return createInvalidAdminUserRequestBuilder().build();
    }

    public static AdminUserResponse createAdminUserResponse() {
        return createAdminUserResponseBuilder().build();
    }

    public static AdminUserRequest createAdminUserUpdateRequest() {
        return createAdminUserUpdateRequestBuilder().build();
    }

    public static AdminUserResponse createAdminUserResponseAfterUpdate() {
        return createAdminUserResponseAfterUpdateBuilder().build();
    }

    public static AdminUser createValidAdminUserEntity() {
        return buildValidAdminUserEntity().build();
    }
}
