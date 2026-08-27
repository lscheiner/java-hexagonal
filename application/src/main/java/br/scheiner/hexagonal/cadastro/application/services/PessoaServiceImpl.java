package br.scheiner.hexagonal.cadastro.application.services;

import java.util.List;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.application.pagination.Pagina;
import br.scheiner.hexagonal.cadastro.application.pagination.Paginacao;
import br.scheiner.hexagonal.cadastro.application.ports.in.PessoaService;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository repository;

    public PessoaServiceImpl(PessoaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pessoa cadastrar(Pessoa pessoa) {
        if (repository.existsByCpf(pessoa.getCpf())) {
            throw new CpfDuplicadoException();
        }
        return repository.save(pessoa);
    }

    @Override
    public Pessoa buscar(UUID id) {
        return repository.findById(id).orElseThrow(PessoaNotFoundException::new);
    }

    @Override
    public Pagina<Pessoa> listar(Paginacao paginacao) {
        return repository.findAll(paginacao);
    }

    @Override
    public Pessoa substituir(UUID id, Pessoa novosDados) {
        var pessoaAtual = buscar(id);
        if (!pessoaAtual.getCpf().getValue().equals(novosDados.getCpf().getValue())
                && repository.existsByCpf(novosDados.getCpf())) {
            throw new CpfDuplicadoException();
        }

        var pessoaAtualizada = new Pessoa(
                id,
                novosDados.getNome(),
                novosDados.getCpf(),
                novosDados.getDataNascimento(),
                novosDados.getEmail(),
                novosDados.getTelefone(),
                novosDados.getEnderecos()
        );
        return repository.save(pessoaAtualizada);
    }

    @Override
    public void excluir(UUID id) {
        var pessoa = buscar(id);
        repository.deleteById(pessoa.getId());
    }

    @Override
    public Pessoa substituirEnderecos(UUID pessoaId, List<Endereco> novosEnderecos) {
        var pessoaAtualizada = buscar(pessoaId).substituirEnderecos(novosEnderecos);
        return repository.save(pessoaAtualizada);
    }

    @Override
    public Pessoa adicionarEndereco(UUID pessoaId, Endereco endereco) {
        var pessoaAtualizada = buscar(pessoaId).adicionarEndereco(endereco);
        return repository.save(pessoaAtualizada);
    }

    @Override
    public Pessoa atualizarEndereco(UUID pessoaId, UUID enderecoId, Endereco novosDados) {
        var pessoaAtualizada = buscar(pessoaId).atualizarEndereco(enderecoId, novosDados);
        return repository.save(pessoaAtualizada);
    }

    @Override
    public Pessoa removerEndereco(UUID pessoaId, UUID enderecoId) {
        var pessoaAtualizada = buscar(pessoaId).removerEndereco(enderecoId);
        return repository.save(pessoaAtualizada);
    }
}
