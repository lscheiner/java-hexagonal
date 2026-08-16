package br.scheiner.hexagonal.cadastro.api.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PessoaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PessoaNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleConflict(CpfDuplicadoException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidation(DomainException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(), message));
    }

    public record ErrorResponse(Instant timestamp, int status, String message) { }
}
