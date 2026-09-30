package br.scheiner.hexagonal.cadastro.application.exceptions;

import java.io.Serial;

public class SaldoNotFoundException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SaldoNotFoundException() {
        super("Saldo da conta não encontrado no Redis");
    }
}
