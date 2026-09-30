package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class SaldoInsuficienteLuaHandler implements ResultadoLuaDebitoHandler {

    private static final String STATUS = "INSUFFICIENT_BALANCE";

    @Override
    public boolean supports(String respostaLua) {
        return STATUS.equals(respostaLua);
    }

    @Override
    public ResultadoDebito interpretar() {
        return ResultadoDebito.SALDO_INSUFICIENTE;
    }
}
