package br.scheiner.hexagonal.cadastro.application.usecases;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PessoaResult(UUID id, String nome, String cpf, LocalDate dataNascimento, String email,
                           String telefone, List<EnderecoResult> enderecos) { }
