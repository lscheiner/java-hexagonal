package br.scheiner.hexagonal.cadastro.domain.valueobjects;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

public record Cep(String value) {
    public Cep {
        value = value == null ? null : value.replaceAll("\\D", "");
        if (value == null || !value.matches("\\d{8}")) throw new DomainValidationException("CEP inválido");
    }
}
