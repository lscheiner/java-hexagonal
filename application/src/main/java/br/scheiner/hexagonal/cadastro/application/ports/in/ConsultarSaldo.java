package br.scheiner.hexagonal.cadastro.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface ConsultarSaldo {
    BigDecimal consultar(UUID contaId);
}
