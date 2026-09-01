package br.scheiner.hexagonal.cadastro.persistence.mapper;

import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.domain.Conta;
import br.scheiner.hexagonal.cadastro.persistence.entity.ContaEntity;
import org.springframework.stereotype.Component;

@Component
public class ContaEntityMapper implements Mapper<Conta, ContaEntity> {

    @Override
    public ContaEntity map(Conta conta) {
        if (conta == null) return null;
        return new ContaEntity(conta.getId(), conta.getPessoaId(), conta.getLimite());
    }
}
