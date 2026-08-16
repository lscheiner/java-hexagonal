package br.scheiner.hexagonal.cadastro.application.usecases;

import java.time.LocalDate;

public record AtualizarPessoaCommand(String nome, LocalDate dataNascimento, String email, String telefone) { }
