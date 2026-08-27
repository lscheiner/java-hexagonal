package br.scheiner.hexagonal.cadastro.application.pagination;

import br.scheiner.hexagonal.cadastro.application.exceptions.PaginacaoInvalidaException;

public record Paginacao(int pagina, int tamanho) {

    public Paginacao {
        if (pagina < 0) {
            throw new PaginacaoInvalidaException("Página deve ser maior ou igual a zero");
        }
        if (tamanho < 1) {
            throw new PaginacaoInvalidaException("Tamanho da página deve ser maior que zero");
        }
    }
}
