package br.scheiner.hexagonal.cadastro.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.scheiner.hexagonal.cadastro.persistence.entity.OutboxEntity;

public interface OutboxJpaRepository
        extends JpaRepository<OutboxEntity, UUID> {

    @Query(value = """
        SELECT *
        FROM outbox
        WHERE processed_at IS NULL
          AND (
                locked_until IS NULL
                OR locked_until < CURRENT_TIMESTAMP(6)
              )
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEntity> reservarPendentesComLock(
            @Param("limit") int limit);

    @Modifying
    @Query("""
        UPDATE OutboxEntity evento
           SET evento.lockedUntil = :reservadoAte
         WHERE evento.id = :id
        """)
    int reservar(
            @Param("id") UUID id,
            @Param("reservadoAte") Instant reservadoAte);
}