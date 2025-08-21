package github.io.api_voting_challenge.unit.fixtures;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.enums.Role;

import static github.io.api_voting_challenge.unit.fixtures.TestConstants.*;

public class AdminFixtures {
    public static AdminUserRequestDto.AdminUserRequestDtoBuilder createValidAdminUserRequestDtoBuilder() {
        return AdminUserRequestDto.builder()
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD);
    }

    public static AdminUserRequestDto.AdminUserRequestDtoBuilder createInvalidAdminUserRequestDtoBuilder() {
        return AdminUserRequestDto.builder()
                .name("admin Test")
                .cpf(INVALID_CPF)
                .email(INVALID_EMAIL)
                .password("12345678");
    }

    public static AdminUserResponseDto.AdminUserResponseDtoBuilder createAdminUserResponseDtoBuilder() {
        return AdminUserResponseDto.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf("528.375.080-98")
                .email(VALID_EMAIL)
                .role(Role.ADMIN);
    }

    public static AdminUserRequestDto.AdminUserRequestDtoBuilder createAdminUserUpdateRequestDtoBuilder() {
        return AdminUserRequestDto.builder()
                .name(UPDATED_NAME)
                .cpf(VALID_CPF)
                .email("email@test.com")
                .password(UPDATED_PASSWORD);
    }

    public static AdminUserResponseDto.AdminUserResponseDtoBuilder createAdminUserResponseDtoAfterUpdateBuilder() {
        return AdminUserResponseDto.builder()
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
    public static AdminUserRequestDto createValidAdminUserRequestDto() {
        return createValidAdminUserRequestDtoBuilder().build();
    }

    public static AdminUserRequestDto createInvalidAdminUserRequestDto() {
        return createInvalidAdminUserRequestDtoBuilder().build();
    }

    public static AdminUserResponseDto createAdminUserResponseDto() {
        return createAdminUserResponseDtoBuilder().build();
    }

    public static AdminUserRequestDto createAdminUserUpdateRequestDto() {
        return createAdminUserUpdateRequestDtoBuilder().build();
    }

    public static AdminUserResponseDto createAdminUserResponseDtoAfterUpdate() {
        return createAdminUserResponseDtoAfterUpdateBuilder().build();
    }

    public static AdminUser createValidAdminUserEntity() {
        return buildValidAdminUserEntity().build();
    }
}
