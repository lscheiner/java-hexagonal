package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

public record Cpf(String value) {
    public Cpf {
        value = value == null ? null : value.replaceAll("\\D", "");
        if (!isValid(value)) {
            throw new DomainValidationException("CPF inválido");
        }
    }

    private static boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) return false;
        return digit(cpf, 9) == cpf.charAt(9) - '0' && digit(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digit(String cpf, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) sum += (cpf.charAt(i) - '0') * (length + 1 - i);
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
