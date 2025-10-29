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
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Business exception");
        response.put("status", ex.getStatus().value());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<Map<String, Object>> handleInfrastructureException(InfrastructureException ex) {
        log.error("Infrastructure exception: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Infrastructure exception");
        response.put("status", ex.getHttpStatus().value());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }
}
