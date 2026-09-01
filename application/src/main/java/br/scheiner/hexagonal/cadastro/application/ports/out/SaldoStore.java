package br.scheiner.hexagonal.cadastro.application.ports.out;

import java.math.BigDecimal;
import java.util.UUID;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

public interface SaldoStore {
    void inicializar(UUID contaId, BigDecimal limite);
    ResultadoDebito debitar(UUID contaId, BigDecimal valor, UUID refId, UUID eventId);

}
