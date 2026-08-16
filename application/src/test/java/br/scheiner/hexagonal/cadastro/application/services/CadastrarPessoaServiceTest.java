package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.usecases.*;
import br.scheiner.hexagonal.cadastro.domain.entities.Pessoa;
import br.scheiner.hexagonal.cadastro.domain.valueobjects.Cpf;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastrarPessoaServiceTest {
    @Test void rejectsDuplicatedCpf() {
        PessoaRepository repository = new PessoaRepository() {
            public Pessoa save(Pessoa pessoa) { return pessoa; }
            public Optional<Pessoa> findById(UUID id) { return Optional.empty(); }
            public boolean existsByCpf(Cpf cpf) { return true; }
            public void deleteById(UUID id) { }
        };
        var command = new CadastrarPessoaCommand("Ana", "529.982.247-25", LocalDate.of(1990, 1, 1), null, null,
                List.of(new EnderecoCommand("Rua A", "1", null, null, "São Paulo", "SP", "01001000", "RESIDENCIAL")));
        assertThrows(CpfDuplicadoException.class, () -> new CadastrarPessoaService(repository).executar(command));
    }
}
