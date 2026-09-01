package br.scheiner.hexagonal.cadastro.outbox.adapter;

import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarOutbox;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPollingScheduler {

    private final ProcessarOutbox processarOutbox;

    public OutboxPollingScheduler(ProcessarOutbox processarOutbox) {
        this.processarOutbox = processarOutbox;
    }

    @Scheduled(fixedDelayString = "${outbox.polling-delay-ms:1000}")
    public void processar() {
        processarOutbox.processarPendentes();
    }
}
