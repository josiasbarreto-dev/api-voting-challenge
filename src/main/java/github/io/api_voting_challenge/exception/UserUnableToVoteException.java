package github.io.api_voting_challenge.exception;

public class UserUnableToVoteException extends RuntimeException {
    public UserUnableToVoteException(String message) {
        super(message);
    }
}
