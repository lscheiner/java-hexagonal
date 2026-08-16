package br.scheiner.hexagonal.cadastro.domain.exceptions;

public class DomainValidationException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public DomainValidationException(String message) {
        super(message);
    }
}
