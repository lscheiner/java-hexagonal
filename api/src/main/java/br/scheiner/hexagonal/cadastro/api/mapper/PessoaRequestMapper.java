package br.scheiner.hexagonal.cadastro.api.mapper;

import br.scheiner.hexagonal.cadastro.api.dto.PessoaRequest;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class PessoaRequestMapper implements Mapper<PessoaRequest, Pessoa> {

    private final EnderecoRequestMapper enderecoRequestMapper;

    public PessoaRequestMapper(EnderecoRequestMapper enderecoRequestMapper) {
        this.enderecoRequestMapper = enderecoRequestMapper;
    }

    @Override
    public Pessoa map(PessoaRequest request) {
        if (request == null) return null;
        var enderecos = request.enderecos() == null
                ? null
                : request.enderecos().stream().map(enderecoRequestMapper::map).toList();

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
}
