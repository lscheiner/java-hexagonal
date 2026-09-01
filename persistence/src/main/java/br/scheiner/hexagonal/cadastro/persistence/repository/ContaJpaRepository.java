package br.scheiner.hexagonal.cadastro.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import br.scheiner.hexagonal.cadastro.persistence.entity.ContaEntity;

public interface ContaJpaRepository extends JpaRepository<ContaEntity, UUID> {

}
