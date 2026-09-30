package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public final class Cpf extends DomainObject {

    private final String value;

    public Cpf(String value) {
        var digits = value == null ? null : value.replaceAll("\\D", "");

        if (!isValid(digits)) {
            throw new DomainException("CPF invalido");
        }
        this.value = digits;
    }

    public String getValue() {
        return value;
    }

    private static boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digit(cpf, 9) == cpf.charAt(9) - '0' && digit(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digit(String cpf, int length) {
        var sum = 0;
        for (var index = 0; index < length; index++) {
            sum += (cpf.charAt(index) - '0') * (length + 1 - index);
        }
        var remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
