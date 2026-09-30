package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class DuplicidadeDebitoLuaHandler implements ResultadoLuaDebitoHandler {

    private static final String PREFIXO = "DUPLICATE|";

    @Override
    public boolean supports(String respostaLua) {
        return respostaLua.startsWith(PREFIXO);
    }

    @Override
    public ResultadoDebito interpretar() {
        return ResultadoDebito.JA_PROCESSADO;
    }
}
