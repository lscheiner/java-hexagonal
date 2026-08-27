package br.scheiner.hexagonal.cadastro.application.exceptions;

public class PaginacaoInvalidaException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public PaginacaoInvalidaException(String message) {
        super(message);
    }
}
