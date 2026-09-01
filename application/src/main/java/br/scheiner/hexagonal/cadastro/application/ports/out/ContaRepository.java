package br.scheiner.hexagonal.cadastro.application.ports.out;

import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.domain.Conta;

public interface ContaRepository {
    Conta salvarComOutbox(Conta conta, OutboxEvent event);
}
