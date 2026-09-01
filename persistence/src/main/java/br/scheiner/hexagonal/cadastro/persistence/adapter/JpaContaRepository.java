package br.scheiner.hexagonal.cadastro.persistence.adapter;

import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.application.ports.out.ContaRepository;
import br.scheiner.hexagonal.cadastro.domain.Conta;
import br.scheiner.hexagonal.cadastro.persistence.mapper.ContaEntityMapper;
import br.scheiner.hexagonal.cadastro.persistence.mapper.OutboxEntityMapper;
import br.scheiner.hexagonal.cadastro.persistence.repository.ContaJpaRepository;
import br.scheiner.hexagonal.cadastro.persistence.repository.OutboxJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaContaRepository implements ContaRepository {
	
	private final ContaJpaRepository contas;
	private final OutboxJpaRepository outbox;
	private final ContaEntityMapper contaEntityMapper;
	private final OutboxEntityMapper outboxEntityMapper;

	public JpaContaRepository(ContaJpaRepository contas, OutboxJpaRepository outbox,
			ContaEntityMapper contaEntityMapper, OutboxEntityMapper outboxEntityMapper) {
		this.contas = contas;
		this.outbox = outbox;
		this.contaEntityMapper = contaEntityMapper;
		this.outboxEntityMapper = outboxEntityMapper;
	}

	@Override
	@Transactional
	public Conta salvarComOutbox(Conta conta, OutboxEvent event) {
		contas.save(contaEntityMapper.map(conta));
		outbox.save(outboxEntityMapper.map(event));
		return conta;
	}
}
