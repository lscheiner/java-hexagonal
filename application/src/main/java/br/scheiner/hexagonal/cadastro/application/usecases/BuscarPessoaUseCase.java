package br.scheiner.hexagonal.cadastro.application.usecases;
import java.util.UUID;
public interface BuscarPessoaUseCase { PessoaResult executar(UUID id); }
