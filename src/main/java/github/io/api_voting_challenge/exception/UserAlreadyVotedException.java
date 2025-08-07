package github.io.api_voting_challenge.exception;

public class UserAlreadyVotedException extends RuntimeException {
    public UserAlreadyVotedException(String message) {
        super(message);
    }
}
