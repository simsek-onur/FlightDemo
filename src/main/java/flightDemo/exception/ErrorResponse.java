package flightDemo.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.util.List;

@RegisterForReflection
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<FieldError> fieldErrors,
        LocalDateTime timestamp
) {

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null, LocalDateTime.now());
    }

    public ErrorResponse(int status, String error, String message, List<FieldError> fieldErrors) {
        this(status, error, message, fieldErrors, LocalDateTime.now());
    }

    @RegisterForReflection
    public record FieldError(String field, String message) {
    }
}
