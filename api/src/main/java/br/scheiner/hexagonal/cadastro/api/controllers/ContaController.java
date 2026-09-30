package br.scheiner.hexagonal.cadastro.api.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.scheiner.hexagonal.cadastro.api.dto.ContaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.ContaResponse;
import br.scheiner.hexagonal.cadastro.api.dto.DebitoRequest;
import br.scheiner.hexagonal.cadastro.api.dto.SaldoResponse;
import br.scheiner.hexagonal.cadastro.api.exception.ApiExceptionHandler;
import br.scheiner.hexagonal.cadastro.application.ports.in.ConsultarSaldo;
import br.scheiner.hexagonal.cadastro.application.ports.in.ContaService;
import br.scheiner.hexagonal.cadastro.application.ports.in.DebitarSaldo;
import br.scheiner.hexagonal.cadastro.domain.Conta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/contas")
@Tag(name = "Contas", description = "Criação de contas e consulta ou movimentação de saldo")
public class ContaController {
    private final ContaService contaService;
    private final DebitarSaldo debitarSaldo;
    private final ConsultarSaldo consultarSaldo;
    public ContaController(
            ContaService contaService,
            DebitarSaldo debitarSaldo,
            ConsultarSaldo consultarSaldo) {
        this.contaService = contaService;
        this.debitarSaldo = debitarSaldo;
        this.consultarSaldo = consultarSaldo;
    }

    @PostMapping
    @Operation(
            summary = "Cria uma conta",
            description = "Cadastra uma conta para uma pessoa com o limite informado. O saldo é inicializado pelo processamento do evento de criação.")
    @ApiResponse(responseCode = "201", description = "Conta criada", content = @Content(schema = @Schema(implementation = ContaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido ou mal formatado", content = @Content)
    @ApiResponse(responseCode = "422", description = "Dados de negócio inválidos", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    public ResponseEntity<ContaResponse> criar(@RequestBody ContaRequest request) {
        var conta = contaService.criar(
                new Conta(null, request.pessoaId(), request.limite()));
        return ResponseEntity.created(URI.create("/contas/" + conta.getId()))
                .body(new ContaResponse(conta.getId(), conta.getPessoaId(), conta.getLimite()));
    }

    @PostMapping("/{id}/debitos")
    @Operation(
            summary = "Debita o saldo de uma conta",
            description = "Debita o valor informado. O refId identifica a operação para idempotência: repetir uma requisição já processada não realiza um novo débito. Retorna o saldo atual após a operação.")
    @ApiResponse(responseCode = "200", description = "Débito processado ou repetição idempotente; saldo atual retornado", content = @Content(schema = @Schema(implementation = SaldoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido ou mal formatado", content = @Content)
    @ApiResponse(responseCode = "422", description = "Valor inválido, saldo insuficiente ou conta sem saldo disponível", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    public ResponseEntity<SaldoResponse> debitar(
            @Parameter(description = "Identificador único da conta", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Valor do débito e identificador de referência usado para reconhecer repetições da mesma operação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DebitoRequest.class)))
            @RequestBody DebitoRequest request) {
        debitarSaldo.debitar(id, request.valor(), request.refId());
        return ResponseEntity.ok(new SaldoResponse(id, consultarSaldo.consultar(id)));
    }

    @GetMapping("/{id}/saldo")
    @Operation(
            summary = "Consulta o saldo de uma conta",
            description = "Retorna o saldo atualmente registrado para a conta. O saldo é mantido no Redis e não é removido por expiração.")
    @ApiResponse(responseCode = "200", description = "Saldo encontrado", content = @Content(schema = @Schema(implementation = SaldoResponse.class)))
    @ApiResponse(responseCode = "422", description = "Conta sem saldo inicializado", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiExceptionHandler.ErrorResponse.class)))
    public ResponseEntity<SaldoResponse> consultarSaldo(
            @Parameter(description = "Identificador único da conta", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id) {
        return ResponseEntity.ok(new SaldoResponse(id, consultarSaldo.consultar(id)));
    }

}
