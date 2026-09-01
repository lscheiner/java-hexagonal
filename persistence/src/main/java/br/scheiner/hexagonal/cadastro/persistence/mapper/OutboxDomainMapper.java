package br.scheiner.hexagonal.cadastro.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.persistence.entity.OutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxDomainMapper implements Mapper<OutboxEntity, OutboxEvent> {

    private final ObjectMapper objectMapper;

    public OutboxDomainMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxEvent map(OutboxEntity entidade) {
        if (entidade == null) return null;
        try {
            var payload = objectMapper.readValue(entidade.getPayload(), OutboxEvent.ContaCriadaPayload.class);
            return new OutboxEvent(entidade.getId(), entidade.getAggregateId(), entidade.getEventType(), payload,
                    entidade.getCreatedAt(), entidade.getProcessedAt());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Payload da outbox invalido: " + entidade.getId(), ex);
        }
    }
}
