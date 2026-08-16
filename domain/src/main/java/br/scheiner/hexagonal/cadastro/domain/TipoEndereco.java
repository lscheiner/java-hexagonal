package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

public enum TipoEndereco {
    RESIDENCIAL, COMERCIAL, OUTRO;

    public static TipoEndereco from(String value) {
        if (value == null) throw new DomainValidationException("Tipo do endereço obrigatório");
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new DomainValidationException("Tipo de endereço inválido");
        }
    }
}
