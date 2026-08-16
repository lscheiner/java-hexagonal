package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.usecases.*;
import br.scheiner.hexagonal.cadastro.domain.entities.*;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.*;
import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;
import java.util.List;

final class PessoaMapper {
    private PessoaMapper() { }
    static Endereco endereco(EnderecoCommand c) {
        if (c == null || c.tipo() == null) throw new DomainValidationException("Tipo do endereço obrigatório");
        return new Endereco(null, c.logradouro(), c.numero(), c.complemento(), c.bairro(), c.cidade(),
                c.estado(), new Cep(c.cep()), TipoEndereco.valueOf(c.tipo().trim().toUpperCase()));
    }
    static PessoaResult result(Pessoa p) {
        List<EnderecoResult> enderecos = p.enderecos().stream().map(e -> new EnderecoResult(e.id(), e.logradouro(),
                e.numero(), e.complemento(), e.bairro(), e.cidade(), e.estado(), e.cep().value(), e.tipo().name())).toList();
        return new PessoaResult(p.id(), p.nome(), p.cpf().value(), p.dataNascimento(),
                p.email() == null ? null : p.email().value(), p.telefone(), enderecos);
    }
    static Pessoa pessoa(CadastrarPessoaCommand c) {
        if (c == null || c.enderecos() == null) throw new DomainValidationException("A pessoa deve possuir ao menos um endereço");
        return new Pessoa(null, c.nome(), new Cpf(c.cpf()), c.dataNascimento(), new Email(c.email()), c.telefone(),
                c.enderecos().stream().map(PessoaMapper::endereco).toList());
    }
}
