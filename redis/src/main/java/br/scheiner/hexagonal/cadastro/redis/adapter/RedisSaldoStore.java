package br.scheiner.hexagonal.cadastro.redis.adapter;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class RedisSaldoStore implements SaldoStore {

    private static final String STREAM = "saldo-events";

    private static final Duration SALDO_TTL = Duration.ofHours(24);

    private static final Duration IDEMPOTENCIA_MARGIN =
            Duration.ofHours(12);

    private static final int DEBITADO = 1;
    private static final int JA_PROCESSADO = 0;
    private static final int SALDO_INSUFICIENTE = -1;
    private static final int CONTA_NAO_ENCONTRADA = -2;
    private static final int VALOR_INVALIDO = -3;

    private static final DefaultRedisScript<Long> DEBITAR =
            new DefaultRedisScript<>(
                    """
                    -- ============================================================
-- REDIS FUNCTION - DEBIT
-- ============================================================
--
-- Redis 8.x / Redis Cluster
--
-- Esta Function implementa uma operação de débito idempotente.
--
-- A operação executa:
--
--     1. Validação dos argumentos
--     2. Validação das KEYS
--     3. Validação da conta
--     4. Verificação de idempotência
--     5. Validação do saldo
--     6. Débito
--     7. Registro da transaction
--     8. TTL de 24 horas
--     9. Rollback caso o registro falhe
--
-- ============================================================
-- REDIS CLUSTER
-- ============================================================
--
-- As duas KEYS utilizadas pela Function precisam estar no
-- mesmo hash slot do Redis Cluster.
--
-- Para isso, utilizamos o mesmo HASH TAG {accountId} nas
-- duas chaves:
--
--     balance:{accountId}
--     transaction:{accountId}:{transactionId}
--
-- No Redis Cluster, o conteúdo entre { } é utilizado como
-- HASH TAG para calcular o hash slot.
--
-- Exemplo:
--
--     balance:{123}
--     transaction:{123}:ABC
--
-- As duas chaves possuem o HASH TAG "123" e, portanto,
-- pertencem ao mesmo hash slot e ao mesmo nó do cluster.
--
-- As chaves { } são NECESSÁRIAS neste desenho.
--
-- Sem o HASH TAG:
--
--     balance:123
--     transaction:123:ABC
--
-- as chaves poderiam cair em slots diferentes e o Redis
-- Cluster rejeitaria a operação com CROSSSLOT.
--
-- IMPORTANTE:
--
-- O {accountId} NÃO representa uma chave separada.
--
-- A chave armazenada no Redis é literalmente:
--
--     balance:{123}
--
-- O trecho {123} faz parte do nome da chave e possui um
-- significado especial para o cálculo do hash slot.
--
-- Contas diferentes podem continuar distribuídas entre
-- diferentes nós:
--
--     balance:{123} -> nó A
--     balance:{456} -> nó B
--     balance:{789} -> nó C
--
-- O requisito é que as KEYS utilizadas na MESMA execução
-- pertençam ao mesmo hash slot.
--
-- ============================================================
-- REDIS FUNCTION
-- ============================================================
--
-- A Function é persistida no Redis e chamada através de FCALL.
--
-- A aplicação Java NÃO precisa enviar o código Lua a cada
-- requisição.
--
-- A Function deve ser carregada no Redis durante o deploy /
-- provisionamento do cluster.
--
-- ============================================================
--
-- KEYS[1] = balance:{accountId}
-- KEYS[2] = transaction:{accountId}:{transactionId}
--
-- ARGV[1] = transactionId
-- ARGV[2] = accountId
-- ARGV[3] = amount
--
-- ============================================================

#!lua name=account_operations

-- ============================================================
-- CONSTANTES
-- ============================================================

local TRANSACTION_TTL = 86400 -- 24 horas


-- ============================================================
-- DEBIT
-- ============================================================

local function debit(keys, args)

    -- ========================================================
    -- VARIÁVEIS
    -- ========================================================

    local balanceKey = keys[1]
    local txKey      = keys[2]

    local txId    = args[1]
    local account = args[2]
    local amount  = tonumber(args[3])


    -- ========================================================
    -- 1. VALIDAÇÃO BÁSICA DOS ARGUMENTOS
    -- ========================================================

    if not txId or txId == "" then
        return {
            0,
            "INVALID_TRANSACTION_ID"
        }
    end

    if string.len(txId) > 100 then
        return {
            0,
            "INVALID_TRANSACTION_ID"
        }
    end


    if not account or account == "" then
        return {
            0,
            "INVALID_ACCOUNT_ID"
        }
    end

    if string.len(account) > 100 then
        return {
            0,
            "INVALID_ACCOUNT_ID"
        }
    end


    if not amount
       or amount ~= amount
       or amount <= 0 then

        return {
            0,
            "INVALID_AMOUNT"
        }
    end


    -- ========================================================
    -- 2. NORMALIZAÇÃO DO VALOR
    --
    -- O domínio trabalha com 2 casas decimais.
    --
    -- Exemplos:
    --
    --     10      -> 10.00
    --     10.5    -> 10.50
    --     10.50   -> 10.50
    --     10.501  -> inválido
    --
    -- ========================================================

    local normalizedAmount = string.format(
        "%.2f",
        amount
    )

    if tonumber(normalizedAmount) ~= amount then
        return {
            0,
            "INVALID_AMOUNT"
        }
    end


    -- ========================================================
    -- 3. VALIDAÇÃO DAS KEYS
    --
    -- O formato esperado é:
    --
    --     balance:{accountId}
    --     transaction:{accountId}:{transactionId}
    --
    -- O mesmo accountId precisa aparecer no HASH TAG das
    -- duas KEYS.
    --
    -- ========================================================

    local balanceAccount = string.match(
        balanceKey,
        "^balance:%{([^}]*)%}$"
    )

    if not balanceAccount then
        return {
            0,
            "INVALID_BALANCE_KEY"
        }
    end

    if balanceAccount ~= account then
        return {
            0,
            "ACCOUNT_KEY_MISMATCH"
        }
    end


    local transactionAccount = string.match(
        txKey,
        "^transaction:%{([^}]*)%}:"
    )

    if not transactionAccount then
        return {
            0,
            "INVALID_TRANSACTION_KEY"
        }
    end

    if transactionAccount ~= account then
        return {
            0,
            "TRANSACTION_KEY_MISMATCH"
        }
    end


    -- ========================================================
    -- 4. VALIDAÇÃO DO BALANCE KEY
    -- ========================================================

    local balanceType = redis.call(
        "TYPE",
        balanceKey
    )

    -- A conta precisa existir.
    if balanceType == "none" then
        return {
            0,
            "ACCOUNT_NOT_FOUND"
        }
    end

    -- O saldo deve ser armazenado como STRING.
    if balanceType ~= "string" then
        return {
            0,
            "INVALID_BALANCE_KEY_TYPE"
        }
    end


    -- ========================================================
    -- 5. VALIDAÇÃO DO TRANSACTION KEY
    -- ========================================================

    local txType = redis.call(
        "TYPE",
        txKey
    )

    -- A transaction pode ainda não existir.
    --
    -- Se existir, obrigatoriamente deve ser HASH.
    if txType ~= "none"
       and txType ~= "hash" then

        return {
            0,
            "INVALID_TRANSACTION_KEY_TYPE"
        }
    end


    -- ========================================================
    -- 6. IDEMPOTÊNCIA
    -- ========================================================
    --
    -- A própria transaction key funciona como registro de
    -- idempotência.
    --
    -- Se a transaction já existir e estiver íntegra:
    --
    --     não realiza novo débito
    --     não renova o TTL
    --     retorna ALREADY_PROCESSED
    --
    -- ========================================================

    if txType == "hash" then

        local storedAccount = redis.call(
            "HGET",
            txKey,
            "accountId"
        )

        local storedAmount = redis.call(
            "HGET",
            txKey,
            "amount"
        )

        local storedNewBalance = redis.call(
            "HGET",
            txKey,
            "newBalance"
        )

        local storedCreatedAt = redis.call(
            "HGET",
            txKey,
            "createdAt"
        )


        -- ====================================================
        -- Transaction existente, mas inconsistente/corrompida.
        -- ====================================================

        if not storedAccount
           or not storedAmount
           or not storedNewBalance
           or not storedCreatedAt then

            return {
                0,
                "TRANSACTION_CORRUPTED"
            }
        end


        -- ====================================================
        -- Mesmo transactionId, mas dados diferentes.
        -- ====================================================

        if storedAccount ~= account
           or storedAmount ~= normalizedAmount then

            return {
                0,
                "TRANSACTION_ID_REUSED"
            }
        end


        -- ====================================================
        -- Transaction já processada.
        --
        -- Não renova o TTL.
        -- ====================================================

        return {
            1,
            "ALREADY_PROCESSED",
            storedNewBalance
        }
    end


    -- ========================================================
    -- 7. OBTÉM O SALDO
    -- ========================================================

    local balance = tonumber(
        redis.call(
            "GET",
            balanceKey
        )
    )

    -- Proteção contra saldo inválido.
    if not balance
       or balance ~= balance then

        return {
            0,
            "INVALID_BALANCE"
        }
    end


    -- ========================================================
    -- 8. VALIDA SALDO
    -- ========================================================

    if balance < amount then
        return {
            0,
            "INSUFFICIENT_BALANCE"
        }
    end


    -- ========================================================
    -- 9. DÉBITO
    -- ========================================================

    local debitResult = redis.pcall(
        "INCRBYFLOAT",
        balanceKey,
        -amount
    )

    if debitResult.err then
        return {
            0,
            "DEBIT_ERROR"
        }
    end

    local newBalance = tostring(
        debitResult
    )


    -- ========================================================
    -- 10. TIMESTAMP
    --
    -- O timestamp é obtido diretamente do Redis.
    --
    -- TIME retorna:
    --
    --     [1] = segundos desde Unix Epoch
    --     [2] = microssegundos
    --
    -- O valor é convertido para milissegundos.
    --
    -- ========================================================

    local redisTime = redis.call(
        "TIME"
    )

    local createdAt =
        tostring(
            tonumber(redisTime[1]) * 1000
            + math.floor(
                tonumber(redisTime[2]) / 1000
            )
        )


    -- ========================================================
    -- 11. REGISTRA TRANSAÇÃO + TTL
    --
    -- Redis 8 disponibiliza HSETEX.
    --
    -- HSETEX cria a transaction como HASH e configura o TTL
    -- de 24 horas na mesma operação.
    --
    -- Não utilizamos FNX aqui porque a existência da
    -- transaction já foi validada anteriormente dentro da
    -- mesma execução atômica da Function.
    --
    -- ========================================================

    local transactionResult = redis.pcall(
        "HSETEX",
        txKey,
        "EX",
        TRANSACTION_TTL,
        "FIELDS",
        4,
        "accountId", account,
        "amount", normalizedAmount,
        "newBalance", newBalance,
        "createdAt", createdAt
    )


    -- ========================================================
    -- 12. FALHA AO REGISTRAR TRANSAÇÃO
    -- ========================================================
    --
    -- O débito já aconteceu.
    --
    -- Portanto, tentamos compensar o débito.
    --
    -- Se a compensação também falhar:
    --
    --     saldo foi debitado
    --     transaction não foi registrada
    --
    -- Nesse cenário é necessária reconciliação externa.
    --
    -- ========================================================

    if transactionResult.err then

        local rollbackResult = redis.pcall(
            "INCRBYFLOAT",
            balanceKey,
            amount
        )


        -- ====================================================
        -- Rollback também falhou.
        -- ====================================================

        if rollbackResult.err then

            return {
                0,
                "ROLLBACK_ERROR"
            }
        end


        -- ====================================================
        -- Débito compensado.
        -- ====================================================

        return {
            0,
            "TRANSACTION_RECORD_ERROR"
        }
    end


    -- ========================================================
    -- 13. SUCESSO
    -- ========================================================
    --
    -- Neste ponto:
    --
    --     saldo foi debitado
    --     transaction foi registrada
    --     TTL de 24 horas foi configurado
    --
    -- Tudo ocorreu dentro da mesma execução atômica da
    -- Redis Function.
    --
    -- ========================================================

    return {
        1,
        "COMPLETED",
        newBalance
    }
end


-- ============================================================
-- REGISTRA A FUNCTION
-- ============================================================

redis.register_function(
    "debit",
    debit
)
                    """,
                    Long.class
            );

    private final StringRedisTemplate redis;

    public RedisSaldoStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void inicializar(
            UUID contaId,
            BigDecimal limite) {

        if (limite == null || limite.signum() < 0) {
            throw new IllegalArgumentException(
                    "Limite deve ser maior ou igual a zero"
            );
        }

        redis.opsForValue().set(
                saldoKey(contaId),
                limite.toPlainString(),
                SALDO_TTL
        );
    }

    @Override
    public ResultadoDebito debitar(
            UUID contaId,
            BigDecimal valor,
            UUID refId,
            UUID eventId) {

        validarDebito(valor, refId, eventId);

        var result = redis.execute(
                DEBITAR,
                List.of(
                        saldoKey(contaId),
                        refsKey(contaId),
                        STREAM
                ),
                refId.toString(),
                valor.toPlainString(),
                eventId.toString(),
                contaId.toString(),
                String.valueOf(IDEMPOTENCIA_MARGIN.toSeconds())
        );

        return interpretarResultado(result);
    }

    private void validarDebito(
            BigDecimal valor,
            UUID refId,
            UUID eventId) {

        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Valor do débito deve ser maior que zero"
            );
        }

        if (refId == null) {
            throw new IllegalArgumentException(
                    "RefId é obrigatório"
            );
        }

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "EventId é obrigatório"
            );
        }
    }

    private ResultadoDebito interpretarResultado(Long result) {

        if (result == null) {
            throw new IllegalStateException(
                    "Lua não retornou resultado"
            );
        }

        return switch (result.intValue()) {
            case DEBITADO ->
                    ResultadoDebito.DEBITADO;

            case JA_PROCESSADO ->
                    ResultadoDebito.JA_PROCESSADO;

            case SALDO_INSUFICIENTE ->
                    ResultadoDebito.SALDO_INSUFICIENTE;

            case CONTA_NAO_ENCONTRADA ->
                    throw new IllegalStateException(
                            "Saldo da conta não encontrado no Redis"
                    );

            case VALOR_INVALIDO ->
                    throw new IllegalArgumentException(
                            "Valor do débito deve ser maior que zero"
                    );

            default ->
                    throw new IllegalStateException(
                            "Resultado inesperado do Lua: " + result
                    );
        };
    }

    private String saldoKey(UUID contaId) {
        return "saldo:conta:" + contaId;
    }

    private String refsKey(UUID contaId) {
        return saldoKey(contaId) + ":refs";
    }
}