package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import java.io.Serial;

public final class RespostaDebitoRedisInvalidaException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RespostaDebitoRedisInvalidaException(String message) {
        super(message);
    }
}
