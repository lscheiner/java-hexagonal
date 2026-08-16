package br.scheiner.hexagonal.cadastro.application.exceptions;

public class CpfDuplicadoException extends RuntimeException {
    public CpfDuplicadoException() { super("CPF já cadastrado"); }
}
