package br.scheiner.hexagonal.cadastro.application.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

public class PessoaService {

    private final PessoaRepository repository;

    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    public Pessoa cadastrar(Pessoa pessoa) {
        if (repository.existsByCpf(pessoa.getCpf())) {
            throw new CpfDuplicadoException();
        }
        return repository.save(pessoa);
    }

    public Pessoa buscar(UUID id) {
        return repository.findById(id).orElseThrow(PessoaNotFoundException::new);
    }

    public Pessoa atualizar(UUID id, String nome, LocalDate dataNascimento, Email email, String telefone) {
        var pessoaAtualizada = buscar(id).atualizar(nome, dataNascimento, email, telefone);
        return repository.save(pessoaAtualizada);
    }

    public void excluir(UUID id) {
        var pessoa = buscar(id);
        repository.deleteById(pessoa.getId());
    }

    public Pessoa substituirEnderecos(UUID pessoaId, List<Endereco> novosEnderecos) {
        var pessoaAtualizada = buscar(pessoaId).substituirEnderecos(novosEnderecos);
        return repository.save(pessoaAtualizada);
    }

    public Pessoa adicionarEndereco(UUID pessoaId, Endereco endereco) {
        var pessoaAtualizada = buscar(pessoaId).adicionarEndereco(endereco);
        return repository.save(pessoaAtualizada);
    }

    public Pessoa atualizarEndereco(UUID pessoaId, UUID enderecoId, Endereco novosDados) {
        var pessoaAtualizada = buscar(pessoaId).atualizarEndereco(enderecoId, novosDados);
        return repository.save(pessoaAtualizada);
    }

    public Pessoa removerEndereco(UUID pessoaId, UUID enderecoId) {
        var pessoaAtualizada = buscar(pessoaId).removerEndereco(enderecoId);
        return repository.save(pessoaAtualizada);
    }
}