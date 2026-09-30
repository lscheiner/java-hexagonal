package br.scheiner.hexagonal.cadastro.domain;

import java.util.UUID;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public abstract class DomainObject {

    protected String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " obrigatorio");
        }
        return value.trim();
    }

    protected <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new DomainException(field + " obrigatorio");
        }
        return value;
    }

    protected String requireMatches(String value, String regex, String message) {
        if (value == null || !value.matches(regex)) {
            throw new DomainException(message);
        }
        return value.trim();
    }

    protected String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    protected UUID generateId(UUID id) {
        return id == null ? UUID.randomUUID() : id;
    }
}
