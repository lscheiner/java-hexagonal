package br.scheiner.hexagonal.cadastro.application.ports.in;

import br.scheiner.hexagonal.cadastro.application.saldo.EventoSaldo;

public interface ProcessarEventoSaldo {
    void processar(EventoSaldo evento);
}
