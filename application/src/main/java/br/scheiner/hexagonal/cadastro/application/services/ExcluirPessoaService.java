package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.usecases.ExcluirPessoaUseCase;
import java.util.UUID;

public class ExcluirPessoaService implements ExcluirPessoaUseCase {
	
	private final PessoaRepository repository;

	public ExcluirPessoaService(PessoaRepository repository) {
		this.repository = repository;
	}

	public void executar(UUID id) {
		if (repository.findById(id).isEmpty())
			throw new PessoaNotFoundException();
		repository.deleteById(id);
	}
}
