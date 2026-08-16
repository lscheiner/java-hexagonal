package br.scheiner.hexagonal.cadastro.application.usecases;

public record EnderecoCommand(String logradouro, String numero, String complemento, String bairro,
                              String cidade, String estado, String cep, String tipo) { }
