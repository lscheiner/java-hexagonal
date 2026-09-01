package br.scheiner.hexagonal.cadastro.api.controllers;

import java.net.URI;
import java.util.UUID;
import br.scheiner.hexagonal.cadastro.api.dto.ContaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.ContaResponse;
import br.scheiner.hexagonal.cadastro.api.dto.DebitoRequest;
import br.scheiner.hexagonal.cadastro.application.ports.in.ContaService;
import br.scheiner.hexagonal.cadastro.application.ports.in.DebitarSaldo;
import br.scheiner.hexagonal.cadastro.domain.Conta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private final ContaService contaService;
    private final DebitarSaldo debitarSaldo;
    public ContaController(ContaService contaService, DebitarSaldo debitarSaldo) {
        this.contaService = contaService; this.debitarSaldo = debitarSaldo;
    }
    @PostMapping
    public ResponseEntity<ContaResponse> criar(@RequestBody ContaRequest request) {
        var conta = contaService.criar(new Conta(null, request.pessoaId(), request.limite()));
        return ResponseEntity.created(URI.create("/contas/" + conta.getId()))
                .body(new ContaResponse(conta.getId(), conta.getPessoaId(), conta.getLimite()));
    }
    @PostMapping("/{id}/debitos")
    public ResponseEntity<Void> debitar(@PathVariable UUID id, @RequestBody DebitoRequest request) {
        debitarSaldo.debitar(id, request.valor(), request.refId());
        return ResponseEntity.noContent().build();
    }
}
