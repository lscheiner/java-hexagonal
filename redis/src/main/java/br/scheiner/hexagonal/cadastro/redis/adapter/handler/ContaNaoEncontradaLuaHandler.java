package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.exceptions.SaldoNotFoundException;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class ContaNaoEncontradaLuaHandler implements ResultadoLuaDebitoHandler {

    private static final String STATUS = "ACCOUNT_NOT_FOUND";

    @Override
    public boolean supports(String respostaLua) {
        return STATUS.equals(respostaLua);
    }

    @Override
    public ResultadoDebito interpretar() {
        throw new SaldoNotFoundException();
    }
}
