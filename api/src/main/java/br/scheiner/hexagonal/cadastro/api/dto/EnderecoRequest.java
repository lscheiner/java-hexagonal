package br.scheiner.hexagonal.cadastro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um endereço para cadastro ou atualização")
public record EnderecoRequest(
        @Schema(description = "Nome do logradouro", example = "Rua das Flores") String logradouro,
        @Schema(description = "Número do imóvel", example = "123") String numero,
        @Schema(description = "Complemento do endereço", example = "Apto 42") String complemento,
        @Schema(description = "Bairro", example = "Centro") String bairro,
        @Schema(description = "Cidade", example = "São Paulo") String cidade,
        @Schema(description = "Sigla da unidade federativa", example = "SP") String estado,
        @Schema(description = "CEP do endereço", example = "01001-000") String cep,
        @Schema(description = "Tipo do endereço", example = "RESIDENCIAL", allowableValues = {"RESIDENCIAL", "COMERCIAL", "OUTRO"}) String tipo
) { }
