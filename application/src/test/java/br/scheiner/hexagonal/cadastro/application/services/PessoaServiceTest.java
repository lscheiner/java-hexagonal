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
                "Sao Paulo",
                "SP",
                new Cep("01001000"),
                TipoEndereco.RESIDENCIAL
        );
        return new Pessoa(
                UUID.randomUUID(),
                "Joao da Silva",
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
        assertEquals(pessoa.getId(), cadastrada.getId());
        assertEquals("Joao da Silva", cadastrada.getNome());
        assertTrue(repository.existsByCpf(pessoa.getCpf()));
    }

    @Test
    @DisplayName("Deve lancar CpfDuplicadoException ao cadastrar com CPF existente")
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

        var encontrada = service.buscar(pessoa.getId());
        assertEquals(pessoa.getId(), encontrada.getId());
    }

    @Test
    @DisplayName("Deve lancar PessoaNotFoundException ao buscar id inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        assertThrows(PessoaNotFoundException.class, () -> service.buscar(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve atualizar dados de uma pessoa existente")
    void deveAtualizarPessoa() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        var atualizada = service.atualizar(
                pessoa.getId(),
                "Joao Silva Santos",
                LocalDate.of(1990, 1, 1),
                new Email("novo_email@email.com"),
                "11988887777"
        );

        assertEquals("Joao Silva Santos", atualizada.getNome());
        assertEquals("novo_email@email.com", atualizada.getEmail().getValue());
        assertEquals("11988887777", atualizada.getTelefone());
    }

    @Test
    @DisplayName("Deve excluir pessoa por id existente")
    void deveExcluirPessoa() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        service.excluir(pessoa.getId());

        assertThrows(PessoaNotFoundException.class, () -> service.buscar(pessoa.getId()));
    }

    @Test
    @DisplayName("Deve adicionar endereco a pessoa")
    void deveAdicionarEndereco() {
        var pessoa = criarPessoaValida();
        service.cadastrar(pessoa);

        var novoEndereco = new Endereco(
                UUID.randomUUID(),
                "Av Paulista",
                "1000",
                "Sala 50",
                "Bela Vista",
                "Sao Paulo",
                "SP",
                new Cep("01310100"),
                TipoEndereco.COMERCIAL
        );

        var atualizada = service.adicionarEndereco(pessoa.getId(), novoEndereco);
        assertEquals(2, atualizada.getEnderecos().size());
    }

    private static class InMemoryPessoaRepository implements PessoaRepository {
        private final Map<UUID, Pessoa> database = new HashMap<>();

        @Override
        public Pessoa save(Pessoa pessoa) {
            database.put(pessoa.getId(), pessoa);
            return pessoa;
        }

        @Override
        public Optional<Pessoa> findById(UUID id) {
            return Optional.ofNullable(database.get(id));
        }

        @Override
        public boolean existsByCpf(Cpf cpf) {
            return database.values().stream().anyMatch(p -> p.getCpf().getValue().equals(cpf.getValue()));
        }

        @Override
        public void deleteById(UUID id) {
            database.remove(id);
        }
    }
}