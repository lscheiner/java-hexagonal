package br.scheiner.hexagonal.cadastro.api.mapper;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoResponse;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoResponseMapper implements Mapper<Endereco, EnderecoResponse> {

    @Override
    public EnderecoResponse map(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoResponse(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep() != null ? endereco.getCep().getValue() : null,
                endereco.getTipo() != null ? endereco.getTipo().name() : null
        );
    }
}