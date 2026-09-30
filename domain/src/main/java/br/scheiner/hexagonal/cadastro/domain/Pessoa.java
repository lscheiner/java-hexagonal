package br.scheiner.hexagonal.cadastro.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public final class Pessoa extends DomainObject {

    private final UUID id;
    private final String nome;
    private final Cpf cpf;
    private final LocalDate dataNascimento;
    private final Email email;
    private final String telefone;
    private final List<Endereco> enderecos;

    public Pessoa(
            UUID id,
            String nome,
            Cpf cpf,
            LocalDate dataNascimento,
            Email email,
            String telefone,
            List<Endereco> enderecos) {
        this.id = generateId(id);
        this.nome = requireNonBlank(nome, "Nome");
        this.cpf = cpf;

        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new DomainException("Data de nascimento invalida");
        }
        this.dataNascimento = dataNascimento;

        if (enderecos == null || enderecos.isEmpty()) {
            throw new DomainException("A pessoa deve possuir ao menos um endereco");
        }
        this.enderecos = List.copyOf(enderecos);
        this.email = email;
        this.telefone = trimOrNull(telefone);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Email getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public Pessoa atualizar(String nome, LocalDate dataNascimento, Email email, String telefone) {
        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, enderecos);
    }

    public Pessoa substituirEnderecos(List<Endereco> novosEnderecos) {
        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, novosEnderecos);
    }

    public Pessoa adicionarEndereco(Endereco endereco) {
        var novaLista = new ArrayList<>(enderecos);
        novaLista.add(endereco);
        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, novaLista);
    }

    public Pessoa atualizarEndereco(UUID enderecoId, Endereco novosDados) {
        var novaLista = new ArrayList<Endereco>(enderecos.size());
        var alterado = false;

        for (var atual : enderecos) {
            if (atual.getId().equals(enderecoId)) {
                novaLista.add(novosDados.comId(enderecoId));
                alterado = true;
            } else {
                novaLista.add(atual);
            }
        }

        if (!alterado) {
            throw new DomainException("Endereco nao encontrado para esta pessoa");
        }

        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, novaLista);
    }

    public Pessoa removerEndereco(UUID enderecoId) {
        var novaLista = new ArrayList<>(enderecos);
        var removido = novaLista.removeIf(endereco -> endereco.getId().equals(enderecoId));

        if (!removido) {
            throw new DomainException("Endereco nao encontrado para esta pessoa");
        }

        return new Pessoa(id, nome, cpf, dataNascimento, email, telefone, novaLista);
    }
}
