package br.scheiner.hexagonal.cadastro.application.usecases;
import java.util.UUID;
public interface AtualizarPessoaUseCase { PessoaResult executar(UUID id, AtualizarPessoaCommand command); }
