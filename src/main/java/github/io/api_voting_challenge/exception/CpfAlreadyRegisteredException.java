package github.io.api_voting_challenge.exception;

public class CpfAlreadyRegisteredException extends RuntimeException{
    public CpfAlreadyRegisteredException(String message) {
        super(message);
    }
}
