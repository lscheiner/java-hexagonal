package br.scheiner.hexagonal.cadastro.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public record PessoaResponse(UUID id, String nome, String cpf, LocalDate dataNascimento, String email,
                             String telefone, List<EnderecoResponse> enderecos) { }
