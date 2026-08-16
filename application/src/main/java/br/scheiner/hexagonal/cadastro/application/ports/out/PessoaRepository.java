package br.scheiner.hexagonal.cadastro.application.ports.out;

import br.scheiner.hexagonal.cadastro.domain.entities.Pessoa;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.Cpf;
import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository {
    Pessoa save(Pessoa pessoa);
    Optional<Pessoa> findById(UUID id);
    boolean existsByCpf(Cpf cpf);
    void deleteById(UUID id);
}
