package br.scheiner.hexagonal.cadastro.domain;

public final class Email extends DomainObject {

    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    private final String value;

    public Email(String value) {
        if (value != null && !value.isBlank())
            requireMatches(value.trim(), EMAIL_REGEX, "E-mail invalido");
        this.value = trimOrNull(value);
    }

    public String getValue() {
        return value;
    }
}