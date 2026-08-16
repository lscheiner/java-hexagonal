package br.scheiner.hexagonal.cadastro.api.controllers;

import br.scheiner.hexagonal.cadastro.api.dto.AtualizarPessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.EnderecoRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaResponse;
import br.scheiner.hexagonal.cadastro.api.mapper.EnderecoApiMapper;
import br.scheiner.hexagonal.cadastro.api.mapper.PessoaApiMapper;
import br.scheiner.hexagonal.cadastro.application.services.PessoaService;
import br.scheiner.hexagonal.cadastro.domain.Email;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService service;
    private final PessoaApiMapper pessoaMapper;
    private final EnderecoApiMapper enderecoMapper;

    public PessoaController(PessoaService service, PessoaApiMapper pessoaMapper, EnderecoApiMapper enderecoMapper) {
        this.service = service;
        this.pessoaMapper = pessoaMapper;
        this.enderecoMapper = enderecoMapper;
    }

    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody PessoaRequest request) {
        var pessoa = pessoaMapper.toDomain(request);
        var cadastrada = service.cadastrar(pessoa);
        return ResponseEntity.created(URI.create("/pessoas/" + cadastrada.id()))
                .body(pessoaMapper.toResponse(cadastrada));
    }

    @GetMapping("/{id}")
    public PessoaResponse buscar(@PathVariable UUID id) {
        var pessoa = service.buscar(id);
        return pessoaMapper.toResponse(pessoa);
    }

    @PutMapping("/{id}")
    public PessoaResponse atualizar(@PathVariable UUID id, @RequestBody AtualizarPessoaRequest request) {
        var email = request.email() != null ? new Email(request.email()) : null;
        var atualizada = service.atualizar(id, request.nome(), request.dataNascimento(), email, request.telefone());
        return pessoaMapper.toResponse(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enderecos")
    public PessoaResponse adicionarEndereco(@PathVariable UUID id, @RequestBody EnderecoRequest request) {
        var endereco = enderecoMapper.toDomain(request);
        var atualizada = service.adicionarEndereco(id, endereco);
        return pessoaMapper.toResponse(atualizada);
    }
}
