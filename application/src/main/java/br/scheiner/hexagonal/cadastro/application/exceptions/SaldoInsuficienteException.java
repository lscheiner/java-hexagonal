package br.scheiner.hexagonal.cadastro.application.exceptions;

public class SaldoInsuficienteException extends ApplicationException {
	
    private static final long serialVersionUID = 1L;

	public SaldoInsuficienteException() {
		super("Saldo insuficiente");
	}
}
