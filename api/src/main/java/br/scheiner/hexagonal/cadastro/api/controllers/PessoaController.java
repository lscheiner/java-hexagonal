package br.scheiner.hexagonal.cadastro.api.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.scheiner.hexagonal.cadastro.api.dto.EnderecoRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaRequest;
import br.scheiner.hexagonal.cadastro.api.dto.PessoaResponse;
import br.scheiner.hexagonal.cadastro.api.dto.PaginaPessoaResponse;
import br.scheiner.hexagonal.cadastro.api.mapper.EnderecoRequestMapper;
import br.scheiner.hexagonal.cadastro.api.mapper.PessoaRequestMapper;
import br.scheiner.hexagonal.cadastro.api.mapper.PessoaResponseMapper;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.application.pagination.Paginacao;
import br.scheiner.hexagonal.cadastro.application.ports.in.PessoaService;
import br.scheiner.hexagonal.cadastro.domain.Endereco;
import br.scheiner.hexagonal.cadastro.domain.Pessoa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pessoas")
@Tag(name = "Pessoas", description = "Operações para cadastro e manutenção de pessoas e seus endereços")
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
    @Operation(summary = "Cadastra uma pessoa", description = "Cria uma pessoa com pelo menos um endereço.")
    @ApiResponse(responseCode = "201", description = "Pessoa cadastrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody PessoaRequest request) {

        var cadastrada = service.cadastrar(pessoaRequestMapper.map(request));

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cadastrada.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pessoaResponseMapper.map(cadastrada));
    }

    @GetMapping
    @Operation(summary = "Lista pessoas", description = "Retorna uma página de pessoas. A paginação começa em zero.")
    @ApiResponse(responseCode = "200", description = "Página de pessoas retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    public PaginaPessoaResponse listar(
            @Parameter(description = "Índice da página, iniciado em zero", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade máxima de itens por página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        var pessoas = service.listar(new Paginacao(page, size)).map(pessoaResponseMapper::map);
        return new PaginaPessoaResponse(
                pessoas.conteudo(),
                pessoas.pagina(),
                pessoas.tamanho(),
                pessoas.totalElementos(),
                pessoas.totalPaginas()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pessoa", description = "Retorna a pessoa correspondente ao identificador informado.")
    @ApiResponse(responseCode = "200", description = "Pessoa encontrada")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    public PessoaResponse buscar(@PathVariable UUID id) {
        return pessoaResponseMapper.map(service.buscar(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Substitui uma pessoa", description = "Substitui todos os dados da pessoa, incluindo CPF e endereços.")
    @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    public PessoaResponse atualizar(@PathVariable UUID id, @RequestBody PessoaRequest request) {
        var atualizada = service.substituir(id, pessoaRequestMapper.map(request));
        return pessoaResponseMapper.map(atualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma pessoa", description = "Remove a pessoa correspondente ao identificador informado.")
    @ApiResponse(responseCode = "204", description = "Pessoa excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enderecos")
    @Operation(summary = "Substitui os endereços", description = "Substitui todos os endereços de uma pessoa pela lista enviada.")
    @ApiResponse(responseCode = "200", description = "Endereços substituídos com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    public PessoaResponse substituirEnderecos(@PathVariable UUID id, @RequestBody List<EnderecoRequest> requests) {
        var novosEnderecos = requests == null
                ? List.<Endereco>of()
                : requests.stream().map(enderecoRequestMapper::map).toList();
        var atualizada = service.substituirEnderecos(id, novosEnderecos);
        return pessoaResponseMapper.map(atualizada);
    }

    @PostMapping("/{id}/enderecos")
    @Operation(summary = "Adiciona um endereço", description = "Inclui um novo endereço para a pessoa informada.")
    @ApiResponse(responseCode = "201", description = "Endereço adicionado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    public ResponseEntity<PessoaResponse> adicionarEndereco(
            @PathVariable UUID id,
            @RequestBody EnderecoRequest request) {
        var endereco = enderecoRequestMapper.map(request);
        var atualizada = service.adicionarEndereco(id, endereco);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{enderecoId}")
                .buildAndExpand(endereco.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pessoaResponseMapper.map(atualizada));
    }

    @PutMapping("/{id}/enderecos/{enderecoId}")
    @Operation(summary = "Atualiza um endereço", description = "Atualiza o endereço informado de uma pessoa.")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada ou endereço inexistente")
    public PessoaResponse atualizarEndereco(
            @PathVariable UUID id,
            @PathVariable UUID enderecoId,
            @RequestBody EnderecoRequest request) {
        var endereco = enderecoRequestMapper.map(request);
        var atualizada = service.atualizarEndereco(id, enderecoId, endereco);
        return pessoaResponseMapper.map(atualizada);
    }

    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    @Operation(summary = "Remove um endereço", description = "Remove o endereço informado da pessoa.")
    @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso")
    @ApiResponse(responseCode = "400", description = "Não é possível deixar a pessoa sem endereços")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada ou endereço inexistente")
    public ResponseEntity<Void> removerEndereco(@PathVariable UUID id, @PathVariable UUID enderecoId) {
        service.removerEndereco(id, enderecoId);
        return ResponseEntity.noContent().build();
    }
}
