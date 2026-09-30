package br.scheiner.hexagonal.cadastro.persistence.repository;

import br.scheiner.hexagonal.cadastro.persistence.entity.PessoaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaJpaRepository extends JpaRepository<PessoaEntity, UUID> {
    boolean existsByCpf(String cpf);
}
