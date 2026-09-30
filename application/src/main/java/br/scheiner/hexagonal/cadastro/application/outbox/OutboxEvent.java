package br.scheiner.hexagonal.cadastro.application.outbox;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class OutboxEvent {
    public static final String CONTA_CRIADA = "CONTA_CRIADA";

    private final UUID id;
    private final UUID aggregateId;
    private final String eventType;
    private final ContaCriadaPayload payload;
    private final Instant createdAt;
    private Instant processadoEm;

    public OutboxEvent(UUID id, UUID aggregateId, String eventType, ContaCriadaPayload payload,
            Instant createdAt, Instant processadoEm) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.processadoEm = processadoEm;
    }

    public static OutboxEvent contaCriada(UUID contaId, BigDecimal limite) {
        var eventId = UUID.randomUUID();
        return new OutboxEvent(eventId, contaId, CONTA_CRIADA,
                new ContaCriadaPayload(eventId, contaId, limite), Instant.now(), null);
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public ContaCriadaPayload getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getProcessadoEm() {
        return processadoEm;
    }

    public boolean ehContaCriada() {
        return CONTA_CRIADA.equals(eventType);
    }

    public boolean estaPendente() {
        return processadoEm == null;
    }

    public void marcarComoProcessado() {
        processadoEm = Instant.now();
    }

    public record ContaCriadaPayload(UUID eventId, UUID contaId, BigDecimal limite) { }
}
