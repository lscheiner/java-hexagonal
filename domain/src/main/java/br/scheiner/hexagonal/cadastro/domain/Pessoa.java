package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Pessoa(
		UUID id, 
		String nome, 
		Cpf cpf, 
		LocalDate dataNascimento, 
		Email email, 
		String telefone,
        List<Endereco> enderecos) {
    
	public Pessoa {
        id = id == null ? UUID.randomUUID() : id;
        if (nome == null || nome.isBlank()) throw new DomainValidationException("Nome obrigatório");
        nome = nome.trim();
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new DomainValidationException("Data de nascimento inválida");
        }
        if (enderecos == null || enderecos.isEmpty()) {
            throw new DomainValidationException("A pessoa deve possuir ao menos um endereço");
        }
        enderecos = List.copyOf(enderecos);
        telefone = telefone == null ? null : telefone.trim();
    }

    public Pessoa atualizar(String nome, LocalDate dataNascimento, Email email, String telefone) {
        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, enderecos);
    }

    public Pessoa adicionarEndereco(Endereco endereco) {
        var novosEnderecos = new java.util.ArrayList<>(enderecos);
        novosEnderecos.add(endereco);
        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, novosEnderecos);
    }
}
