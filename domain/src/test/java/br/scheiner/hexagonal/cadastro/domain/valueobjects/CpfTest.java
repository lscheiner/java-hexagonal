package br.scheiner.hexagonal.cadastro.domain.valueobjects;

import br.scheiner.hexagonal.cadastro.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpfTest {
    @Test void acceptsValidCpfAndNormalizesIt() { assertEquals("52998224725", new Cpf("529.982.247-25").value()); }
    @Test void rejectsInvalidCpf() { assertThrows(DomainValidationException.class, () -> new Cpf("111.111.111-11")); }
}
