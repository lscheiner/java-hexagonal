package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.exceptions.DadosInvalidosException;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class ValorInvalidoLuaHandler implements ResultadoLuaDebitoHandler {

    private static final String STATUS = "INVALID_AMOUNT";

    @Override
    public boolean supports(String respostaLua) {
        return STATUS.equals(respostaLua);
    }

    @Override
    public ResultadoDebito interpretar() {
        throw new DadosInvalidosException("Valor do débito deve ser maior que zero");
    }
}
