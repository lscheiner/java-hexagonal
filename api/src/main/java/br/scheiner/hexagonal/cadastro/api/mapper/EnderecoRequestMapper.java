package br.scheiner.hexagonal.cadastro.api.mapper;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoRequest;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Cep;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.TipoEndereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoRequestMapper implements Mapper<EnderecoRequest, Endereco> {

    @Override
    public Endereco map(EnderecoRequest request) {
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
}
