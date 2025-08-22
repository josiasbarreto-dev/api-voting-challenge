package github.io.api_voting_challenge.exception;

public class VotingSessionNotFoundException extends RuntimeException{
    public VotingSessionNotFoundException(String message) {
        super(message);
    }
}
