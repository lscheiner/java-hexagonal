package br.scheiner.hexagonal.cadastro.application.exceptions;

import java.io.Serial;

public class CpfDuplicadoException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CpfDuplicadoException() {
        super("CPF já cadastrado");
    }
}
