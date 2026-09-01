package br.scheiner.hexagonal.cadastro.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DebitoRequest(BigDecimal valor, UUID refId) { }
