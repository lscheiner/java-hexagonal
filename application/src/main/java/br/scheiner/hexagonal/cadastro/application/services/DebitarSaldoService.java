package br.scheiner.hexagonal.cadastro.application.services;

import java.math.BigDecimal;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.application.exceptions.SaldoInsuficienteException;
import br.scheiner.hexagonal.cadastro.application.ports.in.DebitarSaldo;
import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;
import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainException;

public class DebitarSaldoService implements DebitarSaldo {
    private final SaldoStore saldoStore;

    public DebitarSaldoService(SaldoStore saldoStore) {
        this.saldoStore = saldoStore;
    }

    @Override
    public void debitar(UUID contaId, BigDecimal valor, UUID refId) {
        if (contaId == null || refId == null || valor == null || valor.signum() <= 0) {
            throw new DomainException("Conta, refId e valor positivo sao obrigatorios");
        }
        var resultado = saldoStore.debitar(contaId, valor, refId, UUID.randomUUID());
        if (resultado == ResultadoDebito.SALDO_INSUFICIENTE) {
            throw new SaldoInsuficienteException();
        }
    }
}
