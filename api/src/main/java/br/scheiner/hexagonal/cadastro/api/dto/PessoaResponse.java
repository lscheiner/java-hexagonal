package br.scheiner.hexagonal.cadastro.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma pessoa retornados pela API")
public record PessoaResponse(
        @Schema(description = "Identificador único da pessoa", example = "8b298d74-6c46-4db4-a0d1-a143dc0f2e8a") UUID id,
        @Schema(description = "Nome completo da pessoa", example = "Maria da Silva") String nome,
        @Schema(description = "CPF da pessoa", example = "123.456.789-09") String cpf,
        @Schema(description = "Data de nascimento no formato ISO-8601", example = "1990-05-20") LocalDate dataNascimento,
        @Schema(description = "E-mail da pessoa", example = "maria.silva@example.com") String email,
        @Schema(description = "Telefone para contato", example = "+55 11 99999-9999") String telefone,
        @Schema(description = "Endereços vinculados à pessoa") List<EnderecoResponse> enderecos
) { }
