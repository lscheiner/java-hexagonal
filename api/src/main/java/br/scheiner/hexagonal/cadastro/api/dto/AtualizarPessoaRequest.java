package br.scheiner.hexagonal.cadastro.api.dto;

import java.time.LocalDate;
public record AtualizarPessoaRequest(String nome, LocalDate dataNascimento, String email, String telefone) { }
