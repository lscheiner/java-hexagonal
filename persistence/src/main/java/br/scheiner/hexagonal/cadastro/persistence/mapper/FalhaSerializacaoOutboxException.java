package br.scheiner.hexagonal.cadastro.persistence.mapper;

import java.io.Serial;

public final class FalhaSerializacaoOutboxException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FalhaSerializacaoOutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
