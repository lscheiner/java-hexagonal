package br.scheiner.hexagonal.cadastro.domain;

import java.util.UUID;

public final class Endereco extends DomainObject {

    private final UUID id;
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final Cep cep;
    private final TipoEndereco tipo;

    public Endereco(UUID id, String logradouro, String numero, String complemento,
                    String bairro, String cidade, String estado, Cep cep, TipoEndereco tipo) {
        this.id = generateId(id);
        this.logradouro = requireNonBlank(logradouro, "Logradouro");
        this.numero = requireNonBlank(numero, "Numero");
        this.cidade = requireNonBlank(cidade, "Cidade");
        this.estado = requireNonBlank(estado, "Estado");
        this.tipo = requireNonNull(tipo, "Tipo do endereco");
        this.bairro = trimOrNull(bairro);
        this.complemento = trimOrNull(complemento);
        this.cep = cep;
    }

    public UUID getId() { return id; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public Cep getCep() { return cep; }
    public TipoEndereco getTipo() { return tipo; }

    public Endereco comId(UUID novoId) {
        return new Endereco(novoId, logradouro, numero, complemento, bairro, cidade, estado, cep, tipo);
    }
}