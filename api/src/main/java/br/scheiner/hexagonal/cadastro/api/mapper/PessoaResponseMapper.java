package br.scheiner.hexagonal.cadastro.api.mapper;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoResponse;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaResponse;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PessoaResponseMapper implements Mapper<Pessoa, PessoaResponse> {

    private final EnderecoResponseMapper enderecoResponseMapper;

    public PessoaResponseMapper(EnderecoResponseMapper enderecoResponseMapper) {
        this.enderecoResponseMapper = enderecoResponseMapper;
    }

    @Override
    public PessoaResponse map(Pessoa pessoa) {
        if (pessoa == null) return null;
        var enderecosResponse = pessoa.getEnderecos() == null
                ? List.<EnderecoResponse>of()
                : pessoa.getEnderecos().stream().map(enderecoResponseMapper::map).toList();

        return new PessoaResponse(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getCpf() != null ? pessoa.getCpf().getValue() : null,
                pessoa.getDataNascimento(),
                pessoa.getEmail() != null ? pessoa.getEmail().getValue() : null,
                pessoa.getTelefone(),
                enderecosResponse
        );
    }
}