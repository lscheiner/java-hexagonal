package br.scheiner.hexagonal.cadastro.application.exceptions;

public class PessoaNotFoundException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public PessoaNotFoundException() {
		super("Pessoa não encontrada");
	}
}
