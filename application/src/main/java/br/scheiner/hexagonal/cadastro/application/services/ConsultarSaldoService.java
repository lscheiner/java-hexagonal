package br.scheiner.hexagonal.cadastro.application.services;

import java.math.BigDecimal;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.application.exceptions.DadosInvalidosException;
import br.scheiner.hexagonal.cadastro.application.exceptions.SaldoNotFoundException;
import br.scheiner.hexagonal.cadastro.application.ports.in.ConsultarSaldo;
import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;

public class ConsultarSaldoService implements ConsultarSaldo {
    private final SaldoStore saldoStore;

    public ConsultarSaldoService(SaldoStore saldoStore) {
        this.saldoStore = saldoStore;
    }

    @Override
    public BigDecimal consultar(UUID contaId) {
        if (contaId == null) {
            throw new DadosInvalidosException("Conta é obrigatória");
        }
        var saldo = saldoStore.consultar(contaId);
        if (saldo == null) {
            throw new SaldoNotFoundException();
        }
        return saldo;
    }
}
