package br.scheiner.hexagonal.cadastro.application.exceptions;

public class CpfDuplicadoException extends ApplicationException {
	
	private static final long serialVersionUID = 1L;

	public CpfDuplicadoException() {
		super("CPF já cadastrado");
	}
}
