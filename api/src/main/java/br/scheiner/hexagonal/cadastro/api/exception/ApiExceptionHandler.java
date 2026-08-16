package br.scheiner.hexagonal.cadastro.api.exception;

import br.scheiner.hexagonal.cadastro.application.exceptions.*;
import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;
import java.time.Instant;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(PessoaNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(PessoaNotFoundException ex) { return error(HttpStatus.NOT_FOUND, ex); }
    @ExceptionHandler({DomainValidationException.class, CpfDuplicadoException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> invalid(RuntimeException ex) { return error(ex instanceof CpfDuplicadoException ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST, ex); }
    private ResponseEntity<ErrorResponse> error(HttpStatus status, RuntimeException ex) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(), ex.getMessage()));
    }
    record ErrorResponse(Instant timestamp, int status, String message) { }
}
