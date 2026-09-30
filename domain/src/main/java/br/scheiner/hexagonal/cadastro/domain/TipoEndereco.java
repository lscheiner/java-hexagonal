package br.scheiner.hexagonal.cadastro.domain;

import java.util.Locale;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public enum TipoEndereco {
    RESIDENCIAL, COMERCIAL, OUTRO;

    public static TipoEndereco from(String value) {
        if (value == null) {
            throw new DomainException("Tipo do endereço obrigatório");
        }
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "RESIDENCIAL" -> RESIDENCIAL;
            case "COMERCIAL" -> COMERCIAL;
            case "OUTRO" -> OUTRO;
            default -> throw new DomainException("Tipo de endereço inválido");
        };
    }
}
