package br.scheiner.hexagonal.cadastro.persistence.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.application.ports.out.OutboxRepository;
import br.scheiner.hexagonal.cadastro.persistence.mapper.OutboxDomainMapper;
import br.scheiner.hexagonal.cadastro.persistence.mapper.OutboxEntityMapper;
import br.scheiner.hexagonal.cadastro.persistence.repository.OutboxJpaRepository;

@Repository
public class JpaOutboxRepository implements OutboxRepository {

    private final OutboxJpaRepository repository;
    private final OutboxDomainMapper outboxDomainMapper;
    private final OutboxEntityMapper outboxEntityMapper;

    public JpaOutboxRepository(
            OutboxJpaRepository repository,
            OutboxDomainMapper outboxDomainMapper,
            OutboxEntityMapper outboxEntityMapper) {

        this.repository = repository;
        this.outboxDomainMapper = outboxDomainMapper;
        this.outboxEntityMapper = outboxEntityMapper;
    }

    @Override
    @Transactional
    public List<OutboxEvent> reservarPendentes(int limite) {

        var eventos = repository.reservarPendentesComLock(limite);

        if (eventos.isEmpty()) {
            return List.of();
        }

        var reservadoAte = Instant.now()
                .plus(Duration.ofMinutes(2));

        eventos.forEach(evento ->
                repository.reservar(
                        evento.getId(),
                        reservadoAte
                )
        );

        return eventos.stream()
                .map(outboxDomainMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public OutboxEvent salvar(OutboxEvent evento) {

        var entity = outboxEntityMapper.map(evento);

        var saved = repository.save(entity);

        return outboxDomainMapper.map(saved);
    }
}