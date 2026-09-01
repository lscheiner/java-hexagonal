package br.scheiner.hexagonal.cadastro.domain;

import java.math.BigDecimal;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public final class Conta extends DomainObject {

    private final UUID id;
    private final UUID pessoaId;
    private final BigDecimal limite;

    public Conta(UUID id, UUID pessoaId, BigDecimal limite) {
        this.id = generateId(id);
        this.pessoaId = requireNonNull(pessoaId, "Pessoa");
        this.limite = requireNonNull(limite, "Limite");
        if (limite.signum() < 0) {
            throw new DomainException("Limite nao pode ser negativo");
        }
    }

    public UUID getId() { return id; }
    public UUID getPessoaId() { return pessoaId; }
    public BigDecimal getLimite() { return limite; }
}
