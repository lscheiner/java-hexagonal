package br.scheiner.hexagonal.cadastro.application.exceptions;

import java.io.Serial;

public class PessoaNotFoundException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PessoaNotFoundException() {
        super("Pessoa não encontrada");
    }
}
