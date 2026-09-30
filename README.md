# Cadastro Hexagonal

Aplicação de cadastro de pessoas e contas construída com Java 21 e Spring Boot. O projeto organiza as regras de negócio no domínio e na aplicação; HTTP, MySQL e Redis ficam em módulos externos conectados por ports e adapters.

## Tecnologias

- Java 21
- Spring Boot e Spring MVC
- Spring Data JPA com MySQL 8.4
- Spring Data Redis com Lettuce e Redis 7.4
- Maven em estrutura multimódulo
- ArchUnit para verificar regras de arquitetura

## Módulos

| Módulo | Responsabilidade |
| --- | --- |
| `domain` | Entidades, objetos de valor e regras fundamentais do negócio. Não depende de Spring, JPA ou Redis. |
| `application` | Services, ports de entrada e saída, resultados e exceções da aplicação. Depende do domínio. |
| `api` | Adapter de entrada HTTP: controllers, DTOs, mapeadores e tradução de exceções para respostas HTTP. |
| `persistence` | Adapter de saída MySQL/JPA, entidades de persistência e mapeamento entre persistência e aplicação/domínio. |
| `redis` | Adapter de saída para saldo. Executa o débito atômico em Lua e interpreta respostas por handlers. |
| `outbox` | Adapter agendado que busca e processa eventos pendentes da outbox. |
| `bootstrap` | Aplicação Spring Boot, composição dos módulos e configuração das dependências. |

## Conceitos de programação usados

### Arquitetura hexagonal

O domínio e a camada de aplicação ficam no centro. A arquitetura hexagonal não exige que a aplicação seja organizada como classes chamadas “casos de uso” ou “services”; neste projeto, ela é organizada em services. Os adapters de entrada chamam ports de entrada, e os services dependem de ports de saída para acessar armazenamento e outros recursos externos. Os adapters concretos implementam esses ports.

```text
HTTP (api) ──> port de entrada ──> service ──> port de saída <── MySQL (persistence)
                                                   └──────────────<── Redis (redis)

Outbox (outbox) ──> port de entrada
Bootstrap (bootstrap) conecta as implementações
```

O adapter HTTP pode converter DTOs em objetos de domínio quando esse é o contrato do port. O domínio não conhece DTOs nem detalhes HTTP.

### SOLID

- **Responsabilidade única (SRP):** controllers tratam HTTP; services coordenam ações da aplicação; adapters traduzem chamadas para seus mecanismos externos. Essas responsabilidades são separadas por motivo de mudança, sem exigir uma classe para cada método.
- **Aberto/fechado (OCP):** respostas do Lua são encaminhadas a implementações de `ResultadoLuaDebitoHandler`. Um novo status pode ganhar um handler próprio sem editar o fluxo de despacho em `RedisSaldoStore`. Mudanças que introduzam um novo resultado de negócio ainda podem exigir ajustes nos contratos e em outras camadas.
- **Substituição de Liskov (LSP):** adapters implementam ports e devem respeitar os contratos definidos por eles para que possam ser substituídos sem alterar o comportamento esperado pela aplicação. O ArchUnit verifica a implementação estrutural dos ports e as fronteiras entre módulos; isso, isoladamente, não comprova substituição comportamental.
- **Segregação de interfaces (ISP):** ports de entrada e saída expressam as operações que a aplicação precisa, em vez de expor APIs de framework. As operações relacionadas a um mesmo recurso podem permanecer agrupadas em um port coeso.
- **Inversão de dependência (DIP):** services dependem de interfaces; `bootstrap` fornece as implementações concretas. As regras do ArchUnit ajudam a impedir dependências da aplicação em adapters externos.

### Ports, adapters e injeção de dependências

Os ports ficam em `application.ports.in` e `application.ports.out`. API, JPA, Redis e polling são adapters. A configuração em `bootstrap` conecta os services aos ports. Spring é usado na borda para criar e injetar os componentes.

### Padrões de projeto e contratos

- **Strategy:** cada `ResultadoLuaDebitoHandler` interpreta um status do Lua. O adapter recebe as estratégias registradas e delega o resultado compatível.
- **Repository/port de saída:** `PessoaRepository`, `ContaRepository`, `OutboxRepository` e `SaldoStore` descrevem o que a aplicação precisa; JPA e Redis fornecem as implementações.
- **DTO e mapper:** DTOs definem o formato HTTP sem obrigar clientes a conhecer entidades de persistência. Mappers fazem a conversão entre DTOs, domínio e entidades JPA.
- **Encapsulamento:** invariantes são aplicadas no domínio e nos services; estado mutável de infraestrutura fica dentro dos adapters.
- **Contrato de erro:** exceções de negócio são traduzidas na borda HTTP; detalhes de Redis, SQL e JSON não fazem parte do contrato público.

### Domínio, objetos de valor e imutabilidade

O domínio contém entidades com identidade e comportamento, como `Pessoa`, `Conta` e `Endereco`, além de objetos de valor validados, como `Cpf`, `Email` e `Cep`. DTOs e dados de paginação imutáveis usam `record`. Coleções expostas pelo domínio são copiadas para impedir alteração externa.

### Services e tratamento de erros

Os serviços da aplicação implementam os ports de entrada e coordenam o domínio e os ports de saída. Exceções de negócio próprias representam situações como dados inválidos, saldo insuficiente ou saldo inexistente.

A API traduz erros de negócio para **HTTP 422**. JSON malformado retorna **400**; falhas técnicas não tratadas retornam **500**. A camada de negócio não depende de códigos HTTP.

### Persistência e transactional outbox

A criação de uma conta e a gravação do evento `CONTA_CRIADA` na outbox acontecem juntas na transação do MySQL. O adapter de polling tenta processar eventos pendentes a cada segundo por padrão. Ao processar a criação, inicializa o saldo no Redis e marca o evento como processado. Falhas no processamento são registradas e permitem nova tentativa após a expiração do lock.

O saldo pode ainda não estar disponível imediatamente após a resposta de criação da conta: o processamento da outbox é assíncrono.

### Redis, Lua e idempotência

O débito é executado em um script Lua atômico. A verificação da chave de idempotência, a validação do saldo, o `INCRBYFLOAT` e o registro da transação acontecem na mesma execução. Assim, requisições concorrentes com o mesmo identificador não aplicam dois débitos enquanto o registro de idempotência existir.

As chaves seguem estes formatos:

```text
account:{accountId}:balance
account:{accountId}:tx:{transactionId}
```

O `{accountId}` é o hash tag. Portanto, saldo e transação da mesma conta ficam no mesmo hash slot do Redis Cluster. O `transactionId` não é hash tag. As chaves são enviadas explicitamente em `KEYS[]`; os dados da operação seguem em `ARGV[]`.

A chave de saldo não tem TTL. A chave de idempotência expira em **3600 segundos**. Ela registra os dados da transação (identificador, valor, tipo `DEBIT` e saldo após a operação). Após uma hora, a mesma transação pode ser processada novamente porque o registro expirou.

O resultado do Lua é interpretado por handlers separados, implementações de `ResultadoLuaDebitoHandler`. O adapter Redis não usa Redis Streams, versão de saldo nem chave separada de ledger.

### Java 21

O projeto compila para Java 21. Usa `var` para variáveis locais quando o tipo é evidente, `record` para dados imutáveis, switch expressions, streams com `toList()`, APIs imutáveis de coleção e `@Serial` nas exceções serializáveis. Entidades JPA e entidades de domínio com comportamento permanecem classes, pois não são simples DTOs.

## Pré-requisitos

- JDK 21
- Docker com Docker Compose
- Maven (ou o Maven Wrapper incluído no repositório)

## Executar localmente

Na raiz do projeto, inicie MySQL e Redis:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Compile os módulos e empacote a aplicação:

```bash
./mvnw -pl bootstrap -am package
```

No Windows PowerShell, use `mvnw.cmd` no lugar de `./mvnw`.

Inicie a aplicação:

```bash
java -jar bootstrap/target/bootstrap-0.0.1-SNAPSHOT.jar
```

As configurações padrão apontam para MySQL em `localhost:3306` e Redis em `localhost:6379`.

### Configuração por ambiente

| Variável/propriedade | Padrão | Uso |
| --- | --- | --- |
| `MYSQL_URL` | `jdbc:mysql://localhost:3306/cadastro?...` | URL JDBC do MySQL |
| `MYSQL_USER` | `cadastro` | Usuário do MySQL |
| `MYSQL_PASSWORD` | `cadastro` | Senha do MySQL |
| `REDIS_HOST` | `localhost` | Host do Redis |
| `REDIS_PORT` | `6379` | Porta do Redis |
| `outbox.polling-delay-ms` | `1000` | Intervalo entre verificações da outbox, em milissegundos |

O Compose local usa as credenciais de desenvolvimento definidas em `docker/docker-compose.yml`.

## API de contas e saldo

### Criar conta

```bash
curl -i -X POST http://localhost:8080/contas \
  -H 'Content-Type: application/json' \
  -d '{
    "pessoaId": "<id-da-pessoa>",
    "limite": 1000
  }'
```

A resposta `201 Created` contém o `id` gerado para a conta. Use esse ID — não o `pessoaId` — nas chamadas de saldo e débito. O saldo inicial é carregado pela outbox em segundo plano.

### Consultar saldo

```bash
curl http://localhost:8080/contas/<id-da-conta>/saldo
```

Resposta:

```json
{
  "contaId": "<id-da-conta>",
  "saldo": 1000
}
```

### Debitar

```bash
curl -i -X POST http://localhost:8080/contas/<id-da-conta>/debitos \
  -H 'Content-Type: application/json' \
  -d '{
    "valor": 10,
    "refId": "<id-unico-da-transacao>"
  }'
```

O endpoint retorna `200 OK` com `contaId` e o saldo consultado após o débito. `refId` identifica a transação para idempotência. Reutilize o mesmo `refId` ao repetir uma requisição da mesma transação.

### Outros endpoints

- `POST /pessoas` — cadastra uma pessoa com seus endereços.
- `GET /pessoas?page=0&size=20` — lista pessoas paginadas.
- `GET /pessoas/{id}` — consulta uma pessoa.
- `PUT /pessoas/{id}` — substitui os dados da pessoa.
- `DELETE /pessoas/{id}` — exclui uma pessoa.
- `POST`, `PUT` e `DELETE /pessoas/{id}/enderecos` — gerenciam endereços.
- `GET /swagger-ui/index.html` — interface OpenAPI, quando a aplicação está em execução.

## ArchUnit

As regras em `bootstrap/src/test/.../HexagonalArchitectureTest.java` verificam que domínio e aplicação não dependam de adapters externos, que adapters não dependam uns dos outros, que os ports sejam interfaces e que os adapters de saída implementem seus ports.
