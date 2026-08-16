package br.scheiner.hexagonal.cadastro.persistence.adapter;

import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import br.scheiner.hexagonal.cadastro.persistence.mapper.PessoaDomainMapper;
import br.scheiner.hexagonal.cadastro.persistence.mapper.PessoaEntityMapper;
import br.scheiner.hexagonal.cadastro.persistence.repository.PessoaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPessoaRepository implements PessoaRepository {

    private final PessoaJpaRepository repository;
    private final PessoaEntityMapper entityMapper;
    private final PessoaDomainMapper domainMapper;

    public JpaPessoaRepository(PessoaJpaRepository repository,
                               PessoaEntityMapper entityMapper,
                               PessoaDomainMapper domainMapper) {
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.domainMapper = domainMapper;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        var entity = entityMapper.map(pessoa);
        var savedEntity = repository.save(entity);
        return domainMapper.map(savedEntity);
    }

    @Override
    public Optional<Pessoa> findById(UUID id) {
        return repository.findById(id).map(domainMapper::map);
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return repository.existsByCpf(cpf.getValue());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
