package br.scheiner.hexagonal.cadastro.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarOutbox;
import br.scheiner.hexagonal.cadastro.application.ports.out.OutboxRepository;
import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;

public class ProcessarOutboxService implements ProcessarOutbox {

    private static final Logger log = LoggerFactory.getLogger(ProcessarOutboxService.class);

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
                    log.info("Inicializando saldo da conta {} com limite {} (evento {})",
                            event.getPayload().contaId(), event.getPayload().limite(), event.getId());
                    saldoStore.inicializar(
                            event.getPayload().contaId(),
                            event.getPayload().limite()
                    );
                    log.info("Saldo da conta {} inicializado com sucesso", event.getPayload().contaId());
                }

                event.marcarComoProcessado();

                outboxRepository.salvar(event);

            } catch (Exception e) {
                log.error("Falha ao processar evento {} do tipo {} para agregado {}. "
                                + "O evento poderá ser tentado novamente após expirar o lock.",
                        event.getId(), event.getEventType(), event.getAggregateId(), e);
            }
        }
    }
}
