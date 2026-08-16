package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.usecases.*;

public class CadastrarPessoaService implements CadastrarPessoaUseCase {
    private final PessoaRepository repository;
    public CadastrarPessoaService(PessoaRepository repository) { this.repository = repository; }
    public PessoaResult executar(CadastrarPessoaCommand command) {
        var pessoa = PessoaMapper.pessoa(command);
        if (repository.existsByCpf(pessoa.cpf())) throw new CpfDuplicadoException();
        return PessoaMapper.result(repository.save(pessoa));
    }
}
