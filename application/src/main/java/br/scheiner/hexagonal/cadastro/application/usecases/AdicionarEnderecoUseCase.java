package br.scheiner.hexagonal.cadastro.application.usecases;
import java.util.UUID;
public interface AdicionarEnderecoUseCase { PessoaResult executar(UUID pessoaId, EnderecoCommand command); }
