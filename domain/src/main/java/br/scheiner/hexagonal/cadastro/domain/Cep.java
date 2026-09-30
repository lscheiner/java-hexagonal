package br.scheiner.hexagonal.cadastro.domain;

public final class Cep extends DomainObject {

    private final String value;

    public Cep(String value) {
        var digits = value == null ? null : value.replaceAll("\\D", "");
        this.value = requireMatches(digits, "\\d{8}", "CEP invalido");
    }

    public String getValue() {
        return value;
    }
}