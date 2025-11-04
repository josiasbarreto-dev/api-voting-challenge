package github.io.api_voting_challenge.fixtures;


import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import github.io.api_voting_challenge.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;

public class UserFixtures {
    public static UserRequest.UserRequestBuilder createValidUserRequestBuilder() {
        return UserRequest.builder()
                .name(VALID_NAME)
                .cpf(VALID_CPF);
    }

    public static UserRequest.UserRequestBuilder createInvalidUserRequestBuilder() {
        return UserRequest.builder()
                .name("user Test")
                .cpf(INVALID_CPF);
    }

    public static UserResponse.UserResponseBuilder createUserResponseBuilder() {
        return UserResponse.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf("528.375.080-98");
    }

    public static UserRequest.UserRequestBuilder createUserUpdateRequestBuilder() {
        return UserRequest.builder()
                .name(UPDATED_NAME)
                .cpf(VALID_CPF_UPDATE);
    }

    public static UserResponse.UserResponseBuilder createUpdatedUserResponseBuilder() {
        return UserResponse.builder()
                .id(VALID_ID)
                .name("User Updated")
                .cpf(VALID_CPF);
    }

    public static User.UserBuilder validUserEntityBuild() {
        return User.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf(VALID_CPF);
    }

    public static UserRequest createValidUserRequest() {
        return createValidUserRequestBuilder().build();
    }

    public static UserRequest createInvalidUserRequest() {
        return createInvalidUserRequestBuilder().build();
    }

    public static UserResponse createUserResponse() {
        return createUserResponseBuilder().build();
    }

    public static UserRequest createUserUpdateRequest() {
        return createUserUpdateRequestBuilder().build();
    }

    public static UserResponse createUpdatedUserResponse() {
        return createUpdatedUserResponseBuilder().build();
    }

    public static User createValidUserEntity() {
        return validUserEntityBuild().build();
    }

    public static List<UserResponse> createUserResponseList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> UserResponse.builder()
                        .id((long) i + 1)
                        .name(VALID_NAME + " " + (i + 1))
                        .cpf("000.000.000-" + String.format("%02d", i))
                        .build())
                .collect(Collectors.toList());
    }

    public static List<User> createEntityUserResponseList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> User.builder()
                        .id((long) i + 1)
                        .name(VALID_NAME + " " + (i + 1))
                        .cpf("000.000.000-" + String.format("%02d", i))
                        .build())
                .collect(Collectors.toList());
    }

    public static Page<UserResponse> createUserResponsePage(long totalElements, Pageable pageable) {
        List<UserResponse> content = IntStream.range(pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), (int) totalElements))
                .mapToObj(i -> UserResponse.builder()
                        .id((long) i + 1)
                        .name(VALID_NAME + " " + (i + 1))
                        .cpf("000.000.000-" + String.format("%02d", i))
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalElements);
    }
}
