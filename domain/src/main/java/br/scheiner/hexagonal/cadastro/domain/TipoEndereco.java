package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public enum TipoEndereco {
    RESIDENCIAL, COMERCIAL, OUTRO;

    public static TipoEndereco from(String value) {
        if (value == null) throw new DomainException("Tipo do endereço obrigatório");
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new DomainException("Tipo de endereço inválido");
        }
    }
}
