package br.scheiner.hexagonal.cadastro.application.saldo;

import java.math.BigDecimal;
import java.util.UUID;

public record EventoSaldo(UUID eventId, UUID refId, UUID contaId, BigDecimal valor,
                          BigDecimal saldoAtual, String tipoEvento) { }
