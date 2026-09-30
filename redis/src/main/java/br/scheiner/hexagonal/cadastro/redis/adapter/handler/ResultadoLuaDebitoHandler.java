package br.scheiner.hexagonal.cadastro.redis.adapter.handler;

import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

public interface ResultadoLuaDebitoHandler {

    boolean supports(String respostaLua);

    ResultadoDebito interpretar();
}
