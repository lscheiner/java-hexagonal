package br.scheiner.hexagonal.cadastro.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoResponse;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaResponse;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

@Component
public class PessoaApiMapper {

    private final EnderecoApiMapper enderecoMapper;

    public PessoaApiMapper(EnderecoApiMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    public Pessoa toDomain(PessoaRequest request) {
        if (request == null) return null;
        var enderecos = request.enderecos() == null
                ? null
                : request.enderecos().stream().map(enderecoMapper::toDomain).toList();

        return new Pessoa(
                null,
                request.nome(),
                request.cpf() != null ? new Cpf(request.cpf()) : null,
                request.dataNascimento(),
                request.email() != null ? new Email(request.email()) : null,
                request.telefone(),
                enderecos
        );
    }

    public PessoaResponse toResponse(Pessoa pessoa) {
        if (pessoa == null) return null;
        var enderecosResponse = pessoa.enderecos() == null
                ? List.<EnderecoResponse>of()
                : pessoa.enderecos().stream().map(enderecoMapper::toResponse).toList();

        return new PessoaResponse(
                pessoa.id(),
                pessoa.nome(),
                pessoa.cpf() != null ? pessoa.cpf().value() : null,
                pessoa.dataNascimento(),
                pessoa.email() != null ? pessoa.email().value() : null,
                pessoa.telefone(),
                enderecosResponse
        );
    }
}
