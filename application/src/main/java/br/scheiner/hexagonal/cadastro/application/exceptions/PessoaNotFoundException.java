package br.scheiner.hexagonal.cadastro.application.exceptions;

public class PessoaNotFoundException extends RuntimeException {
    public PessoaNotFoundException() { super("Pessoa não encontrada"); }
}
