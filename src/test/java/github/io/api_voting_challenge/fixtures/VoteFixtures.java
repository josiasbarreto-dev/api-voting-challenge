package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.enums.VoteOption;

import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;

public class VoteFixtures {
    public static VoteRequestDTO.VoteRequestDTOBuilder createValidVoteRequestDtoBuilder() {
        return VoteRequestDTO.builder()
                .voteOption(VoteOption.YES);
    }

    public static VoteRequestDTO.VoteRequestDTOBuilder createInvalidVoteRequestDtoBuilder() {
        return VoteRequestDTO.builder()
                .voteOption(null);
    }

    public static VoteResultResponseDTO.VoteResultResponseDTOBuilder createVoteResultResponseDtoBuilder() {
        return VoteResultResponseDTO.builder()
                .message("Voting session results")
                .yesVotes(10L)
                .noVotes(5L);
    }

    public static Vote.VoteBuilder createValidVoteEntityBuilder() {
        return Vote.builder()
                .id(VALID_ID)
                .user(VoterFixtures.createValidVotingUserEntity())
                .agenda(AgendaFixtures.createAgenda())
                .voteOption(VoteOption.YES);
    }

    public static VoteRequestDTO createValidVoteRequestDto() {
        return createValidVoteRequestDtoBuilder().build();
    }

    public static VoteRequestDTO createInvalidVoteRequestDto() {
        return createInvalidVoteRequestDtoBuilder().build();
    }

    public static VoteResultResponseDTO createVoteResultResponseDto() {
        return createVoteResultResponseDtoBuilder().build();
    }

    public static Vote createValidVoteEntity() {
        return createValidVoteEntityBuilder().build();
    }
}
