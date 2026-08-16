package br.scheiner.hexagonal.cadastro.application.usecases;

import java.time.LocalDate;
import java.util.List;

public record CadastrarPessoaCommand(String nome, String cpf, LocalDate dataNascimento, String email,
                                     String telefone, List<EnderecoCommand> enderecos) { }
