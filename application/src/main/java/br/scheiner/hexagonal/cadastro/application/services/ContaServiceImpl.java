package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.outbox.OutboxEvent;
import br.scheiner.hexagonal.cadastro.application.ports.in.ContaService;
import br.scheiner.hexagonal.cadastro.application.ports.out.ContaRepository;
import br.scheiner.hexagonal.cadastro.domain.Conta;

public class ContaServiceImpl implements ContaService {
    private final ContaRepository repository;

    public ContaServiceImpl(ContaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Conta criar(Conta conta) {
        var event = OutboxEvent.contaCriada(conta.getId(), conta.getLimite());
        return repository.salvarComOutbox(conta, event);
    }
}
