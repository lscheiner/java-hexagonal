package br.scheiner.hexagonal.cadastro.api.controllers;

import br.scheiner.hexagonal.cadastro.api.dto.*;
import br.scheiner.hexagonal.cadastro.application.usecases.*;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
    private final CadastrarPessoaUseCase cadastrar;
    private final BuscarPessoaUseCase buscar;
    private final AtualizarPessoaUseCase atualizar;
    private final ExcluirPessoaUseCase excluir;
    private final AdicionarEnderecoUseCase adicionarEndereco;
    public PessoaController(CadastrarPessoaUseCase cadastrar, BuscarPessoaUseCase buscar, AtualizarPessoaUseCase atualizar,
                            ExcluirPessoaUseCase excluir, AdicionarEnderecoUseCase adicionarEndereco) {
        this.cadastrar=cadastrar; this.buscar=buscar; this.atualizar=atualizar; this.excluir=excluir; this.adicionarEndereco=adicionarEndereco;
    }
    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody PessoaRequest request) {
        var result = cadastrar.executar(new CadastrarPessoaCommand(request.nome(), request.cpf(), request.dataNascimento(), request.email(), request.telefone(),
                request.enderecos() == null ? null : request.enderecos().stream().map(this::enderecoCommand).toList()));
        return ResponseEntity.created(URI.create("/pessoas/" + result.id())).body(response(result));
    }
    @GetMapping("/{id}") public PessoaResponse buscar(@PathVariable UUID id) { return response(buscar.executar(id)); }
    @PutMapping("/{id}") public PessoaResponse atualizar(@PathVariable UUID id, @RequestBody AtualizarPessoaRequest request) {
        return response(atualizar.executar(id, new AtualizarPessoaCommand(request.nome(), request.dataNascimento(), request.email(), request.telefone())));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluir.executar(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/enderecos") public PessoaResponse adicionarEndereco(@PathVariable UUID id, @RequestBody EnderecoRequest request) {
        return response(adicionarEndereco.executar(id, enderecoCommand(request)));
    }
    private EnderecoCommand enderecoCommand(EnderecoRequest e) { return new EnderecoCommand(e.logradouro(), e.numero(), e.complemento(), e.bairro(), e.cidade(), e.estado(), e.cep(), e.tipo()); }
    private PessoaResponse response(PessoaResult p) { return new PessoaResponse(p.id(), p.nome(), p.cpf(), p.dataNascimento(), p.email(), p.telefone(),
            p.enderecos().stream().map(e -> new EnderecoResponse(e.id(), e.logradouro(), e.numero(), e.complemento(), e.bairro(), e.cidade(), e.estado(), e.cep(), e.tipo())).toList()); }
}
