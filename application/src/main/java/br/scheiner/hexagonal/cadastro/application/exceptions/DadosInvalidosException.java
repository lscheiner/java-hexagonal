package br.scheiner.hexagonal.cadastro.application.exceptions;

import java.io.Serial;

public final class DadosInvalidosException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String message) {
        super(message);
    }
}
