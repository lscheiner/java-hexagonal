package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarEventoSaldo;
import br.scheiner.hexagonal.cadastro.application.saldo.EventoSaldo;

public class ProcessarEventoSaldoService implements ProcessarEventoSaldo {

    @Override
    public void processar(EventoSaldo evento) {
        if (evento == null || evento.eventId() == null || evento.refId() == null || evento.contaId() == null
                || evento.valor() == null || evento.saldoAtual() == null
                || !"SALDO_DEBITADO".equals(evento.tipoEvento())) {
            throw new IllegalArgumentException("Evento de saldo invalido");
        }
    }
}
