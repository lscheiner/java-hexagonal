package br.scheiner.hexagonal.cadastro.application.ports.out;

import java.util.List;

import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;

public interface OutboxRepository {
	List<OutboxEvent> reservarPendentes(int limite);

	OutboxEvent salvar(OutboxEvent evento);
}
