package br.scheiner.hexagonal.cadastro.persistence.mapper;

import br.scheiner.hexagonal.cadastro.domain.entities.*;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.*;
import br.scheiner.hexagonal.cadastro.persistence.entity.*;

public final class PessoaPersistenceMapper {
    private PessoaPersistenceMapper() { }
    public static PessoaEntity toEntity(Pessoa pessoa) {
        var entity = new PessoaEntity(pessoa.id(), pessoa.nome(), pessoa.cpf().value(), pessoa.dataNascimento(),
                pessoa.email() == null ? null : pessoa.email().value(), pessoa.telefone());
        pessoa.enderecos().forEach(e -> entity.addEndereco(new EnderecoEntity(e.id(), e.logradouro(), e.numero(),
                e.complemento(), e.bairro(), e.cidade(), e.estado(), e.cep().value(), e.tipo().name())));
        return entity;
    }
    public static Pessoa toDomain(PessoaEntity entity) {
        var enderecos = entity.getEnderecos().stream().map(e -> new Endereco(e.getId(), e.getLogradouro(), e.getNumero(),
                e.getComplemento(), e.getBairro(), e.getCidade(), e.getEstado(), new Cep(e.getCep()), TipoEndereco.valueOf(e.getTipo()))).toList();
        return new Pessoa(entity.getId(), entity.getNome(), new Cpf(entity.getCpf()), entity.getDataNascimento(),
                new Email(entity.getEmail()), entity.getTelefone(), enderecos);
    }
}
