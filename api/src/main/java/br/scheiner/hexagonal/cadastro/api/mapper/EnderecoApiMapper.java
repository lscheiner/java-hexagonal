package br.scheiner.hexagonal.cadastro.api.mapper;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoRequest;
import br.scheiner.hexagonal.cadastro.api.dto.EnderecoResponse;
import br.scheiner.hexagonal.cadastro.domain.Cep;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.TipoEndereco;

import org.springframework.stereotype.Component;

@Component
public class EnderecoApiMapper {

    public Endereco toDomain(EnderecoRequest request) {
        if (request == null) return null;
        return new Endereco(
                null,
                request.logradouro(),
                request.numero(),
                request.complemento(),
                request.bairro(),
                request.cidade(),
                request.estado(),
                new Cep(request.cep()),
                TipoEndereco.from(request.tipo())
        );
    }

    public EnderecoResponse toResponse(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoResponse(
                endereco.id(),
                endereco.logradouro(),
                endereco.numero(),
                endereco.complemento(),
                endereco.bairro(),
                endereco.cidade(),
                endereco.estado(),
                endereco.cep() != null ? endereco.cep().value() : null,
                endereco.tipo() != null ? endereco.tipo().name() : null
        );
    }
}
