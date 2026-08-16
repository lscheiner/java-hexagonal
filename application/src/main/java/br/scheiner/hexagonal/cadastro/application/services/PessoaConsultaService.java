package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.usecases.*;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.Email;
import java.util.UUID;

public class PessoaConsultaService implements BuscarPessoaUseCase, AtualizarPessoaUseCase, AdicionarEnderecoUseCase {
    private final PessoaRepository repository;
    public PessoaConsultaService(PessoaRepository repository) { this.repository = repository; }
    public PessoaResult executar(UUID id) { return PessoaMapper.result(find(id)); }
    public PessoaResult executar(UUID id, AtualizarPessoaCommand command) {
        return PessoaMapper.result(repository.save(find(id).atualizar(command.nome(), command.dataNascimento(), new Email(command.email()), command.telefone())));
    }
    public PessoaResult executar(UUID pessoaId, EnderecoCommand command) {
        return PessoaMapper.result(repository.save(find(pessoaId).adicionarEndereco(PessoaMapper.endereco(command))));
    }
    private br.scheiner.hexagonal.cadastro.domain.entities.Pessoa find(UUID id) {
        return repository.findById(id).orElseThrow(PessoaNotFoundException::new);
    }
}
