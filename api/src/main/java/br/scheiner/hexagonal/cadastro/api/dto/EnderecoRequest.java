package br.scheiner.hexagonal.cadastro.api.dto;

public record EnderecoRequest(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String tipo
) { }
