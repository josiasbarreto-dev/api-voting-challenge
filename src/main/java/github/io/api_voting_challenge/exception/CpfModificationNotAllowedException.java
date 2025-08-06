package github.io.api_voting_challenge.exception;

public class CpfModificationNotAllowedException extends RuntimeException{
    public CpfModificationNotAllowedException(String message) {
        super(message);
    }
}
