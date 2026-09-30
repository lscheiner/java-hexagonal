package br.scheiner.hexagonal.cadastro.application.exceptions;

import java.io.Serial;

public class PaginacaoInvalidaException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaginacaoInvalidaException(String message) {
        super(message);
    }
}
