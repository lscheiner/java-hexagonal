package br.scheiner.hexagonal.cadastro.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface DebitarSaldo {
    void debitar(UUID contaId, BigDecimal valor, UUID refId);
}
