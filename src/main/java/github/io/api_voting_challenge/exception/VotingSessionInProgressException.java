package github.io.api_voting_challenge.exception;

public class VotingSessionInProgressException extends RuntimeException {
    public VotingSessionInProgressException(String message) {
        super(message);
    }
}
