package github.io.api_voting_challenge.exception;

public class VotingSessionClosedException extends RuntimeException {
    public VotingSessionClosedException(String message) {
        super(message);
    }
}
