package github.io.api_voting_challenge.exception;

import org.springframework.http.HttpStatus;

public class InfrastructureException extends RuntimeException {
    private final HttpStatus httpStatus;
    public InfrastructureException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
