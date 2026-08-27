package br.scheiner.hexagonal.cadastro.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Página de pessoas retornada pela API")
public record PaginaPessoaResponse(
        @Schema(description = "Pessoas presentes nesta página") List<PessoaResponse> conteudo,
        @Schema(description = "Índice da página atual, iniciado em zero", example = "0") int pagina,
        @Schema(description = "Quantidade máxima de itens por página", example = "20") int tamanho,
        @Schema(description = "Quantidade total de pessoas", example = "42") long totalElementos,
        @Schema(description = "Quantidade total de páginas", example = "3") int totalPaginas
) { }
