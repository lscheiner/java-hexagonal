package br.scheiner.hexagonal.cadastro.persistence.adapter;

import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.entities.Pessoa;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.Cpf;
import br.scheiner.hexagonal.cadastro.persistence.mapper.PessoaPersistenceMapper;
import br.scheiner.hexagonal.cadastro.persistence.repository.PessoaJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPessoaRepository implements PessoaRepository {
    private final PessoaJpaRepository repository;
    public JpaPessoaRepository(PessoaJpaRepository repository) { this.repository = repository; }
    public Pessoa save(Pessoa pessoa) { return PessoaPersistenceMapper.toDomain(repository.save(PessoaPersistenceMapper.toEntity(pessoa))); }
    public Optional<Pessoa> findById(UUID id) { return repository.findById(id).map(PessoaPersistenceMapper::toDomain); }
    public boolean existsByCpf(Cpf cpf) { return repository.existsByCpf(cpf.value()); }
    public void deleteById(UUID id) { repository.deleteById(id); }
}
