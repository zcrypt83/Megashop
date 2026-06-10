package pe.edu.idat.megashop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<?> notFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<?> conflict(ConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), null);
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<?> unauthorized(UnauthorizedException exception) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> forbidden(AccessDeniedException exception) {
        return error(HttpStatus.FORBIDDEN, "No autorizado", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(field -> details.put(field.getField(), field.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Datos invalidos", details);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> general(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", null);
    }

    private ResponseEntity<?> error(HttpStatus status, String message, Object details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("status", status.value());
        if (details != null) body.put("details", details);
        return ResponseEntity.status(status).body(Map.of("error", body));
    }
}
