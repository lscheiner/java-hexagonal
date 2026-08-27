package br.scheiner.hexagonal.cadastro.application.ports.in;

import java.util.List;
import java.util.UUID;

import br.scheiner.hexagonal.cadastro.application.pagination.Pagina;
import br.scheiner.hexagonal.cadastro.application.pagination.Paginacao;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

public interface PessoaService {

    Pessoa cadastrar(Pessoa pessoa);

    Pessoa buscar(UUID id);

    Pagina<Pessoa> listar(Paginacao paginacao);

    Pessoa substituir(UUID id, Pessoa novosDados);

    void excluir(UUID id);

    Pessoa substituirEnderecos(UUID pessoaId, List<Endereco> novosEnderecos);

    Pessoa adicionarEndereco(UUID pessoaId, Endereco endereco);

    Pessoa atualizarEndereco(UUID pessoaId, UUID enderecoId, Endereco novosDados);

    Pessoa removerEndereco(UUID pessoaId, UUID enderecoId);
}
