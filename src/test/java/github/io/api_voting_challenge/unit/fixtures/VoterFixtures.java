package github.io.api_voting_challenge.unit.fixtures;

import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;
import github.io.api_voting_challenge.model.VotingUser;
import github.io.api_voting_challenge.model.enums.Role;

import static github.io.api_voting_challenge.unit.fixtures.TestConstants.*;

public class VoterFixtures {
    public static VoterRequestDto.VoterRequestDtoBuilder createValidVoterRequestDtoBuilder() {
        return VoterRequestDto.builder()
                .name(VALID_NAME)
                .cpf(VALID_CPF);
    }

    public static VoterRequestDto.VoterRequestDtoBuilder createInvalidVoterRequestDtoBuilder() {
        return VoterRequestDto.builder()
                .name(INVALID_NAME)
                .cpf(INVALID_CPF);
    }

    public static VoterResponseDto.VoterResponseDtoBuilder createVoterResponseDtoBuilder() {
        return VoterResponseDto.builder()
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
    public static VoterRequestDto createValidVoterRequestDto() {
        return createValidVoterRequestDtoBuilder().build();
    }

    public static VoterRequestDto createInvalidVoterRequestDto() {
        return createInvalidVoterRequestDtoBuilder().build();
    }

    public static VoterResponseDto createVoterResponseDto() {
        return createVoterResponseDtoBuilder().build();
    }

    public static VotingUser createValidVotingUserEntity() {
        return createValidVotingUserEntityBuilder().build();
    }
}
