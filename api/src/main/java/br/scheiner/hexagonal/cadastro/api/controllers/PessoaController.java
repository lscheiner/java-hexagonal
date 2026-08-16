package br.scheiner.hexagonal.cadastro.api.controllers;

import br.scheiner.hexagonal.cadastro.api.dto.AtualizarPessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.EnderecoRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaResponse;
import br.scheiner.hexagonal.cadastro.api.mapper.EnderecoRequestMapper;
import br.scheiner.hexagonal.cadastro.api.mapper.PessoaRequestMapper;
import br.scheiner.hexagonal.cadastro.api.mapper.PessoaResponseMapper;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.application.services.PessoaService;
import br.scheiner.hexagonal.cadastro.domain.Email;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService service;
    private final Mapper<PessoaRequest, Pessoa> pessoaRequestMapper;
    private final Mapper<Pessoa, PessoaResponse> pessoaResponseMapper;
    private final Mapper<EnderecoRequest, Endereco> enderecoRequestMapper;

    public PessoaController(PessoaService service,
                            PessoaRequestMapper pessoaRequestMapper,
                            PessoaResponseMapper pessoaResponseMapper,
                            EnderecoRequestMapper enderecoRequestMapper) {
        this.service = service;
        this.pessoaRequestMapper = pessoaRequestMapper;
        this.pessoaResponseMapper = pessoaResponseMapper;
        this.enderecoRequestMapper = enderecoRequestMapper;
    }

    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody PessoaRequest request) {

    	var cadastrada = service.cadastrar(pessoaRequestMapper.map(request));

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cadastrada.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pessoaResponseMapper.map(cadastrada));
    }

    @GetMapping("/{id}")
    public PessoaResponse buscar(@PathVariable UUID id) {
        return pessoaResponseMapper.map(service.buscar(id));
    }

    @PutMapping("/{id}")
    public PessoaResponse atualizar(@PathVariable UUID id, @RequestBody AtualizarPessoaRequest request) {
        var email = request.email() != null ? new Email(request.email()) : null;
        var atualizada = service.atualizar(id, request.nome(), request.dataNascimento(), email, request.telefone());
        return pessoaResponseMapper.map(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enderecos")
    public PessoaResponse substituirEnderecos(@PathVariable UUID id, @RequestBody List<EnderecoRequest> requests) {
        var novosEnderecos = requests == null ? List.<Endereco>of() : requests.stream().map(enderecoRequestMapper::map).toList();
        var atualizada = service.substituirEnderecos(id, novosEnderecos);
        return pessoaResponseMapper.map(atualizada);
    }

    @PostMapping("/{id}/enderecos")
    public ResponseEntity<PessoaResponse> adicionarEndereco(@PathVariable UUID id, @RequestBody EnderecoRequest request) {
        var endereco = enderecoRequestMapper.map(request);
        var atualizada = service.adicionarEndereco(id, endereco);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{enderecoId}")
                .buildAndExpand(endereco.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pessoaResponseMapper.map(atualizada));
    }

    @PutMapping("/{id}/enderecos/{enderecoId}")
    public PessoaResponse atualizarEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            @RequestBody EnderecoRequest request) {
        
    	var endereco = enderecoRequestMapper.map(request);
        var atualizada = service.atualizarEndereco(id, enderecoId, endereco);
        return pessoaResponseMapper.map(atualizada);
    }

    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable UUID id, @PathVariable UUID enderecoId) {
        service.removerEndereco(id, enderecoId);
        return ResponseEntity.noContent().build();
    }
}
