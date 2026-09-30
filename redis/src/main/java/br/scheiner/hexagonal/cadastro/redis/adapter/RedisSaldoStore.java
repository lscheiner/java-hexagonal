package br.scheiner.hexagonal.cadastro.redis.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;
import br.scheiner.hexagonal.cadastro.application.exceptions.DadosInvalidosException;
import br.scheiner.hexagonal.cadastro.application.exceptions.SaldoNotFoundException;
import br.scheiner.hexagonal.cadastro.application.saldo.ResultadoDebito;

@Component
public class RedisSaldoStore implements SaldoStore {

    private static final Logger log = LoggerFactory.getLogger(RedisSaldoStore.class);

    private static final DefaultRedisScript<String> DEBITAR =
            new DefaultRedisScript<>(
                    """
                    local balanceKey = KEYS[1]
                    local txKey = KEYS[2]
                    local transactionId = ARGV[1]
                    local amount = tonumber(ARGV[2])
                    local accountId = ARGV[3]

                    if not amount or amount ~= amount or amount <= 0 or amount == math.huge then
                        return 'INVALID_AMOUNT'
                    end

                    if redis.call('EXISTS', txKey) == 1 then
                        local stored = redis.call('GET', txKey)
                        return 'DUPLICATE|' .. stored
                    end

                    local balanceType = redis.call('TYPE', balanceKey).ok
                    if balanceType == 'none' then
                        return 'ACCOUNT_NOT_FOUND'
                    end
                    if balanceType ~= 'string' then
                        return 'INVALID_BALANCE_TYPE'
                    end

                    local currentBalance = tonumber(redis.call('GET', balanceKey))
                    if not currentBalance or currentBalance ~= currentBalance then
                        return 'INVALID_BALANCE'
                    end
                    if currentBalance < amount then
                        return 'INSUFFICIENT_BALANCE'
                    end

                    local newBalance = redis.call('INCRBYFLOAT', balanceKey, -amount)
                    local transaction = cjson.encode({
                        transactionId = transactionId,
                        amount = ARGV[2],
                        accountId = accountId,
                        operation = 'DEBIT',
                        balanceAfter = newBalance
                    })
                    redis.call('SET', txKey, transaction, 'EX', 3600)
                    return 'SUCCESS|' .. newBalance
                    """,
                    String.class
            );

    private final StringRedisTemplate redis;

    public RedisSaldoStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void inicializar(UUID contaId, BigDecimal limite) {
        if (contaId == null || limite == null || limite.signum() < 0) {
            throw new DadosInvalidosException("Conta e limite maior ou igual a zero são obrigatórios");
        }
        var key = saldoKey(contaId);
        log.info("Gravando saldo inicial no Redis: conta={}, chave={}, saldo={}", contaId, key, limite);
        redis.opsForValue().set(key, limite.toPlainString());
        log.info("Saldo inicial gravado no Redis: conta={}, chave={}", contaId, key);
    }

    @Override
    public BigDecimal consultar(UUID contaId) {
        if (contaId == null) {
            throw new DadosInvalidosException("Conta é obrigatória");
        }
        var key = saldoKey(contaId);
        var saldo = redis.opsForValue().get(key);
        if (saldo == null) {
            log.warn("Saldo não encontrado no Redis: conta={}, chave={}", contaId, key);
            return null;
        }
        log.debug("Saldo carregado do Redis: conta={}, saldo={}", contaId, saldo);
        return new BigDecimal(saldo);
    }

    @Override
    public ResultadoDebito debitar(UUID contaId, BigDecimal valor, UUID refId, UUID eventId) {
        validarDebito(contaId, valor, refId, eventId);

        var result = redis.execute(
                DEBITAR,
                List.of(saldoKey(contaId), transacaoKey(contaId, refId)),
                refId.toString(), valor.toPlainString(), contaId.toString()
        );

        if (result == null) {
            throw new IllegalStateException("Lua não retornou resultado");
        }
        if (result.startsWith("SUCCESS|")) {
            return ResultadoDebito.DEBITADO;
        }
        if (result.startsWith("DUPLICATE|")) {
            return ResultadoDebito.JA_PROCESSADO;
        }
        return switch (result) {
            case "INSUFFICIENT_BALANCE" -> ResultadoDebito.SALDO_INSUFICIENTE;
            case "ACCOUNT_NOT_FOUND" -> throw new SaldoNotFoundException();
            case "INVALID_AMOUNT" -> throw new DadosInvalidosException("Valor do débito deve ser maior que zero");
            default -> throw new IllegalStateException("Falha no débito Redis: " + result);
        };
    }

    private void validarDebito(UUID contaId, BigDecimal valor, UUID refId, UUID eventId) {
        if (contaId == null || refId == null || eventId == null) {
            throw new DadosInvalidosException("Conta, refId e eventId são obrigatórios");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new DadosInvalidosException("Valor do débito deve ser maior que zero");
        }
    }

    private String saldoKey(UUID contaId) {
        return "account:{" + contaId + "}:balance";
    }

    private String transacaoKey(UUID contaId, UUID transactionId) {
        return "account:{" + contaId + "}:tx:" + transactionId;
    }
}
