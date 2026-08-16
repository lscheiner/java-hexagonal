package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

public record Email(String value) {
    
	public Email {
        if (value != null && !value.isBlank() && !value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new DomainValidationException("E-mail inválido");
        }
        value = value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }
}
