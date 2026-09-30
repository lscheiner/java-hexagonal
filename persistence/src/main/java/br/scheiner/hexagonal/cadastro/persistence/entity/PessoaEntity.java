package br.scheiner.hexagonal.cadastro.persistence.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "pessoa", uniqueConstraints = @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "cpf"))
public class PessoaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefone;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EnderecoEntity> enderecos = new ArrayList<>();

    protected PessoaEntity() {
    }

    public PessoaEntity(UUID id, String nome, String cpf, LocalDate dataNascimento, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.telefone = telefone;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public List<EnderecoEntity> getEnderecos() {
        return enderecos;
    }

    public void addEndereco(EnderecoEntity endereco) {
        endereco.setPessoa(this);
        enderecos.add(endereco);
    }
}
