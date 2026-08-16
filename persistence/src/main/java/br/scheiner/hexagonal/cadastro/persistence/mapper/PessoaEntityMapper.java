package br.scheiner.hexagonal.cadastro.persistence.mapper;

import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import br.scheiner.hexagonal.cadastro.persistence.entity.EnderecoEntity;
import br.scheiner.hexagonal.cadastro.persistence.entity.PessoaEntity;
import org.springframework.stereotype.Component;

@Component
public class PessoaEntityMapper implements Mapper<Pessoa, PessoaEntity> {

    @Override
    public PessoaEntity map(Pessoa pessoa) {
        if (pessoa == null) return null;
        var entity = new PessoaEntity(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getCpf().getValue(),
                pessoa.getDataNascimento(),
                pessoa.getEmail() == null ? null : pessoa.getEmail().getValue(),
                pessoa.getTelefone()
        );
        pessoa.getEnderecos().forEach(e -> entity.addEndereco(new EnderecoEntity(
                e.getId(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getEstado(),
                e.getCep().getValue(),
                e.getTipo().name()
        )));
        return entity;
    }
}