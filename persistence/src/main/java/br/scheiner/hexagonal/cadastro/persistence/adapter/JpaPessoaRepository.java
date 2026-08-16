package br.scheiner.hexagonal.cadastro.persistence.adapter;

import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import br.scheiner.hexagonal.cadastro.persistence.mapper.PessoaPersistenceMapper;
import br.scheiner.hexagonal.cadastro.persistence.repository.PessoaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPessoaRepository implements PessoaRepository {

    private final PessoaJpaRepository repository;
    private final PessoaPersistenceMapper mapper = new PessoaPersistenceMapper();

    public JpaPessoaRepository(PessoaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        var entity = mapper.toEntity(pessoa);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Pessoa> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return repository.existsByCpf(cpf.value());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
