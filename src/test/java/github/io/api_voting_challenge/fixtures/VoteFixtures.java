package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.enums.VoteOption;

import static github.io.api_voting_challenge.fixtures.TestConstants.INVALID_ID;
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;

public class VoteFixtures {
    public static VoteRequest.VoteRequestBuilder createValidVoteRequestBuilder() {
        return VoteRequest.builder()
                .userId(VALID_ID)
                .voteOption(VoteOption.YES);
    }

    public static VoteRequest.VoteRequestBuilder createInvalidVoteRequestBuilder() {
        return VoteRequest.builder()
                .userId(INVALID_ID)
                .voteOption(null);
    }

    public static VoteResultResponse.VoteResultResponseBuilder createVoteResultResponseBuilder() {
        return VoteResultResponse.builder()
                .message("Voting session results")
                .yesVotes(10L)
                .noVotes(5L);
    }

    public static Vote.VoteBuilder createValidVoteEntityBuilder() {
        return Vote.builder()
                .id(VALID_ID)
                .user(UserFixtures.createValidUserEntity())
                .agenda(AgendaFixtures.createAgenda())
                .voteOption(VoteOption.YES);
    }

    public static VoteRequest createValidVoteRequest() {
        return createValidVoteRequestBuilder().build();
    }

    public static VoteRequest createInvalidVoteRequest() {
        return createInvalidVoteRequestBuilder().build();
    }

    public static VoteResultResponse createVoteResultResponse() {
        return createVoteResultResponseBuilder().build();
    }

    public static Vote createValidVoteEntity() {
        return createValidVoteEntityBuilder().build();
    }
}
