package br.scheiner.hexagonal.cadastro.api.dto;

import java.time.LocalDate;
import java.util.List;

public record PessoaRequest(
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String email,
        String telefone,
        List<EnderecoRequest> enderecos
) { }
