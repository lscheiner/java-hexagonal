package br.scheiner.hexagonal.cadastro.application.usecases;

import java.util.UUID;

public record EnderecoResult(UUID id, String logradouro, String numero, String complemento, String bairro,
                             String cidade, String estado, String cep, String tipo) { }
