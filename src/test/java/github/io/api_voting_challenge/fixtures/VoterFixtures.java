package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.VoterRequest;
import github.io.api_voting_challenge.dto.VoterResponse;
import github.io.api_voting_challenge.model.VotingUser;
import github.io.api_voting_challenge.model.enums.Role;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;

public class VoterFixtures {
    public static VoterRequest.VoterRequestBuilder createValidVoterRequestBuilder() {
        return VoterRequest.builder()
                .name(VALID_NAME)
                .cpf(VALID_CPF);
    }

    public static VoterRequest.VoterRequestBuilder createInvalidVoterRequestBuilder() {
        return VoterRequest.builder()
                .name(INVALID_NAME)
                .cpf(INVALID_CPF);
    }

    public static VoterResponse.VoterResponseBuilder createVoterResponseBuilder() {
        return VoterResponse.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .role(Role.USER);
    }

    public static VotingUser.VotingUserBuilder createValidVotingUserEntityBuilder() {
        return VotingUser.builder()
                .id(VALID_ID)
                .name(VALID_NAME)
                .cpf(VALID_CPF)
                .role(Role.USER);
    }
    public static VoterRequest createValidVoterRequest() {
        return createValidVoterRequestBuilder().build();
    }

    public static VoterRequest createInvalidVoterRequest() {
        return createInvalidVoterRequestBuilder().build();
    }

    public static VoterResponse createVoterResponse() {
        return createVoterResponseBuilder().build();
    }

    public static VotingUser createValidVotingUserEntity() {
        return createValidVotingUserEntityBuilder().build();
    }
}
