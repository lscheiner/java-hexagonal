package br.scheiner.hexagonal.cadastro.api.dto;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Endereço vinculado a uma pessoa")
public record EnderecoResponse(
        @Schema(description = "Identificador único do endereço", example = "a4d1f4a1-0f9f-4a53-a20e-6d3f1a5d1e31") UUID id,
        @Schema(description = "Nome do logradouro", example = "Rua das Flores") String logradouro,
        @Schema(description = "Número do imóvel", example = "123") String numero,
        @Schema(description = "Complemento do endereço", example = "Apto 42") String complemento,
        @Schema(description = "Bairro", example = "Centro") String bairro,
        @Schema(description = "Cidade", example = "São Paulo") String cidade,
        @Schema(description = "Sigla da unidade federativa", example = "SP") String estado,
        @Schema(description = "CEP do endereço", example = "01001-000") String cep,
        @Schema(description = "Tipo do endereço", example = "RESIDENCIAL") String tipo
) { }
