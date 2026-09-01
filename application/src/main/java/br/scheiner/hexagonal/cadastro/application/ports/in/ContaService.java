package br.scheiner.hexagonal.cadastro.application.ports.in;

import br.scheiner.hexagonal.cadastro.domain.Conta;

public interface ContaService {
    Conta criar(Conta conta);
}
