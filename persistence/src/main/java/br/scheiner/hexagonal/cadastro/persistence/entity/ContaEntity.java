package br.scheiner.hexagonal.cadastro.persistence.entity;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conta")
public class ContaEntity {
    @Id private UUID id;
    @Column(name = "pessoa_id", nullable = false) private UUID pessoaId;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal limite;
    protected ContaEntity() { }
    public ContaEntity(UUID id, UUID pessoaId, BigDecimal limite) { this.id = id; this.pessoaId = pessoaId; this.limite = limite; }
    public UUID getId() { return id; }
    public UUID getPessoaId() { return pessoaId; }
    public BigDecimal getLimite() { return limite; }
}
