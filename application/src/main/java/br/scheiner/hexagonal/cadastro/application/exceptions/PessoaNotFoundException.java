package br.scheiner.hexagonal.cadastro.application.exceptions;

public class PessoaNotFoundException extends ApplicationException {

	private static final long serialVersionUID = 1730999640248746635L;

	public PessoaNotFoundException() {
		super("Pessoa não encontrada");
	}
}
