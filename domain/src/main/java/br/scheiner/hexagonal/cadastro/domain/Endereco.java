package br.scheiner.hexagonal.cadastro.domain;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;

import java.util.UUID;

public record Endereco(
		UUID id, 
		String logradouro, 
		String numero, 
		String complemento, 
		String bairro, 
		String cidade,
		String estado, 
		Cep cep, 
		TipoEndereco tipo) {
	
	public Endereco {
		id = id == null ? UUID.randomUUID() : id;
		logradouro = required(logradouro, "Logradouro");
		numero = required(numero, "Número");
		cidade = required(cidade, "Cidade");
		estado = required(estado, "Estado");
		if (tipo == null)
			throw new DomainValidationException("Tipo do endereço obrigatório");
		bairro = bairro == null ? null : bairro.trim();
		complemento = complemento == null ? null : complemento.trim();
	}

	private static String required(String value, String field) {
		if (value == null || value.isBlank())
			throw new DomainValidationException(field + " obrigatório");
		return value.trim();
	}
}
