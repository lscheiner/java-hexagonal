package br.scheiner.hexagonal.cadastro.application.services;

import br.scheiner.hexagonal.cadastro.application.exceptions.CpfDuplicadoException;
import br.scheiner.hexagonal.cadastro.application.exceptions.PessoaNotFoundException;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.domain.Cep;
import br.scheiner.hexagonal.cadastro.domain.Cpf;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import br.scheiner.hexagonal.cadastro.domain.TipoEndereco;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PessoaServiceTest {

    private InMemoryPessoaRepository repository;
    private PessoaService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPessoaRepository();
        service = new PessoaService(repository);
    }

    private Pessoa criarPessoaValida() {
        var endereco = new Endereco(
                UUID.randomUUID(),
                "Rua das Flores",
                "123",
                "Apto 101",
                "Centro",
                "São Paulo",
                "SP",
                new Cep("01001000"),
                TipoEndereco.RESIDENCIAL
        );
        return new Pessoa(
                UUID.randomUUID(),
                "João da Silva",
                new Cpf("52998224725"),
                LocalDate.of(1990, 1, 1),
                new Email("joao@email.com"),
                "11999998888",
                List.of(endereco)
        );
    }

    @Test
    @DisplayName("Deve cadastrar uma pessoa com sucesso")
    void deveCadastrarPessoa() {
        var pessoa = criarPessoaValida();
        var cadastrada = service.cadastrar(pessoa);

        assertNotNull(cadastrada);
        assertEquals(pessoa.id(), cadastrada.id());
        assertEquals("João da Silva", cadastrada.nome());
        assertTrue(repository.existsByCpf(pessoa.cpf()));
    }

    @Test
    @DisplayName("Deve lançar CpfDuplicadoException ao cadastrar com CPF existente")
    void deveLancarExcecaoCpfDuplicado() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        assertThrows(CpfDuplicadoException.class, () -> service.cadastrar(pessoa));
    }

    @Test
    @DisplayName("Deve buscar pessoa por id existente")
    void deveBuscarPessoaPorId() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        var encontrada = service.buscar(pessoa.id());
        assertEquals(pessoa.id(), encontrada.id());
    }

    @Test
    @DisplayName("Deve lançar PessoaNotFoundException ao buscar id inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        assertThrows(PessoaNotFoundException.class, () -> service.buscar(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve atualizar dados de uma pessoa existente")
    void deveAtualizarPessoa() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        var atualizada = service.atualizar(
                pessoa.id(),
                "João Silva Santos",
                LocalDate.of(1990, 1, 1),
                new Email("novo_email@email.com"),
                "11988887777"
        );

        assertEquals("João Silva Santos", atualizada.nome());
        assertEquals("novo_email@email.com", atualizada.email().value());
        assertEquals("11988887777", atualizada.telefone());
    }

    @Test
    @DisplayName("Deve excluir pessoa por id existente")
    void deveExcluirPessoa() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        service.excluir(pessoa.id());

        assertThrows(PessoaNotFoundException.class, () -> service.buscar(pessoa.id()));
    }

    @Test
    @DisplayName("Deve adicionar endereço à pessoa")
    void deveAdicionarEndereco() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        var novoEndereco = new Endereco(
                UUID.randomUUID(),
                "Av Paulista",
                "1000",
                "Sala 50",
                "Bela Vista",
                "São Paulo",
                "SP",
                new Cep("01310100"),
                TipoEndereco.COMERCIAL
        );

        var atualizada = service.adicionarEndereco(pessoa.id(), novoEndereco);
        assertEquals(2, atualizada.enderecos().size());
    }

    private static class InMemoryPessoaRepository implements PessoaRepository {
        private final Map<UUID, Pessoa> database = new HashMap<>();

        @Override
        public Pessoa save(Pessoa pessoa) {
            database.put(pessoa.id(), pessoa);
            return pessoa;
        }

        @Override
        public Optional<Pessoa> findById(UUID id) {
            return Optional.ofNullable(database.get(id));
        }

        @Override
        public boolean existsByCpf(Cpf cpf) {
            return database.values().stream().anyMatch(p -> p.cpf().value().equals(cpf.value()));
        }

        @Override
        public void deleteById(UUID id) {
            database.remove(id);
        }
    }
}
