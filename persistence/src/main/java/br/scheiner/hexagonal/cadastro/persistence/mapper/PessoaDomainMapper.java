package br.scheiner.hexagonal.cadastro.persistence.mapper;

import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Cep;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import br.scheiner.hexagonal.cadastro.domain.TipoEndereco;
import br.scheiner.hexagonal.cadastro.persistence.entity.PessoaEntity;
import org.springframework.stereotype.Component;

@Component
public class PessoaDomainMapper implements Mapper<PessoaEntity, Pessoa> {

    @Override
    public Pessoa map(PessoaEntity entity) {
        if (entity == null) return null;
        var enderecos = entity.getEnderecos().stream().map(e -> new Endereco(
                e.getId(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getEstado(),
                new Cep(e.getCep()),
                TipoEndereco.from(e.getTipo())
        )).toList();

        return new Pessoa(
                entity.getId(),
                entity.getNome(),
                new Cpf(entity.getCpf()),
                entity.getDataNascimento(),
                new Email(entity.getEmail()),
                entity.getTelefone(),
                enderecos
        );
    }
}
