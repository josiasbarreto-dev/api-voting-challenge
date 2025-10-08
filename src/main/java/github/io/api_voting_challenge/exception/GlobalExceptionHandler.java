package github.io.api_voting_challenge.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        log.error("Validation failed: {}", fieldErrors);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        response.put("error", "Bad Request");
        response.put("message", "Validation failed for one or more fields.");
        response.put("details", fieldErrors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(AgendaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAgendaNotFoundException(AgendaNotFoundException ex) {
        log.error("Agenda not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Agenda not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyVotedException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyVotedException(UserAlreadyVotedException ex) {
        log.error("User has already voted: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "User has already voted");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "User not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(VotingSessionInProgressException.class)
    public ResponseEntity<Map<String, Object>> handleVotingSessionInProgressException(VotingSessionInProgressException ex) {
        log.error("Voting session in progress: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Voting session is in progress");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CpfAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleCpfAlreadyRegisteredException(CpfAlreadyRegisteredException ex) {
        log.error("CPF already registered: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "CPF already registered");
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(CpfModificationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleCpfModificationNotAllowedException(CpfModificationNotAllowedException ex) {
        log.error("CPF modification not allowed: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "CPF modification not allowed");
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(VotingSessionClosedException.class)
    public ResponseEntity<Map<String, Object>> handleVotingSessionClosedException(VotingSessionClosedException ex) {
        log.error("Voting session closed: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Voting session is closed");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VotingSessionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVotingSessionNotFoundException(VotingSessionNotFoundException ex) {
        log.error("Voting session not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Voting session not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserUnableToVoteException.class)
    public ResponseEntity<Map<String, Object>> handleUserUnableToVote(UserUnableToVoteException ex) {
        log.error("User unable to vote: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User unable to vote");
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException ex) {
        log.error("Feign client error: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "External service error");
        response.put("status", ex.status());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(ex.status()).body(response);
    }
}
