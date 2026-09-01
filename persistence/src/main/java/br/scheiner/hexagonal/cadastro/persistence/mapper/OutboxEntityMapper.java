package br.scheiner.hexagonal.cadastro.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.persistence.entity.OutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxEntityMapper implements Mapper<OutboxEvent, OutboxEntity> {

    private final ObjectMapper objectMapper;

    public OutboxEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxEntity map(OutboxEvent evento) {
        if (evento == null) return null;
        try {
            return new OutboxEntity(evento.getId(), evento.getAggregateId(), evento.getEventType(),
                    objectMapper.writeValueAsString(evento.getPayload()), evento.getCreatedAt(),
                    evento.getProcessadoEm());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Nao foi possivel serializar o evento da outbox", ex);
        }
    }
}
