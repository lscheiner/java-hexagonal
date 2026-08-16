package br.scheiner.hexagonal.cadastro.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "endereco")
public class EnderecoEntity {
    @Id private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "pessoa_id", nullable = false)
    private PessoaEntity pessoa;
    @Column(nullable = false) private String logradouro;
    @Column(nullable = false, length = 30) private String numero;
    private String complemento; private String bairro;
    @Column(nullable = false) private String cidade;
    @Column(nullable = false, length = 2) private String estado;
    @Column(nullable = false, length = 8) private String cep;
    @Column(nullable = false, length = 20) private String tipo;
    protected EnderecoEntity() { }
    public EnderecoEntity(UUID id, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, String tipo) {
        this.id=id; this.logradouro=logradouro; this.numero=numero; this.complemento=complemento; this.bairro=bairro; this.cidade=cidade; this.estado=estado; this.cep=cep; this.tipo=tipo;
    }
    public void setPessoa(PessoaEntity pessoa) { this.pessoa = pessoa; }
    public UUID getId() { return id; } public String getLogradouro() { return logradouro; } public String getNumero() { return numero; }
    public String getComplemento() { return complemento; } public String getBairro() { return bairro; } public String getCidade() { return cidade; }
    public String getEstado() { return estado; } public String getCep() { return cep; } public String getTipo() { return tipo; }
}
