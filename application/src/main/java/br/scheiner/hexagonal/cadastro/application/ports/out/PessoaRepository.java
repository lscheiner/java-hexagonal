package br.scheiner.hexagonal.cadastro.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

public interface PessoaRepository {
    Pessoa save(Pessoa pessoa);
    Optional<Pessoa> findById(UUID id);
    boolean existsByCpf(Cpf cpf);
    void deleteById(UUID id);
}
