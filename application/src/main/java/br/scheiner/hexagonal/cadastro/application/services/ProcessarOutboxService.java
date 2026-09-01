package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarOutbox;
import br.scheiner.hexagonal.cadastro.application.ports.out.OutboxRepository;
import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;

public class ProcessarOutboxService implements ProcessarOutbox {

    private final OutboxRepository outboxRepository;
    private final SaldoStore saldoStore;

    public ProcessarOutboxService(
            OutboxRepository outboxRepository,
            SaldoStore saldoStore) {

        this.outboxRepository = outboxRepository;
        this.saldoStore = saldoStore;
    }

    @Override
    public void processarPendentes() {

        var eventos = outboxRepository.reservarPendentes(50);

        for (var event : eventos) {

            try {

                if (event.ehContaCriada()) {
                    saldoStore.inicializar(
                            event.getPayload().contaId(),
                            event.getPayload().limite()
                    );
                }

                event.marcarComoProcessado();

                outboxRepository.salvar(event);

            } catch (Exception e) {
                // Não marca como processado.
                // Quando locked_until expirar,
                // o evento poderá ser processado novamente.
            }
        }
    }
}