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
                    -- =====================================================
                    -- 1. Verifica idempotência
                    --
                    -- KEYS[2] = saldo:conta:<contaId>:refs
                    -- ARGV[1] = refId
                    --
                    -- Se o refId já foi processado, não realiza o débito.
                    -- =====================================================

                    if redis.call(
                        'SISMEMBER',
                        KEYS[2],
                        ARGV[1]
                    ) == 1 then
                        return 0
                    end


                    -- =====================================================
                    -- 2. Obtém e valida o saldo
                    --
                    -- KEYS[1] = saldo:conta:<contaId>
                    -- ARGV[2] = valor do débito
                    -- =====================================================

                    local saldo = tonumber(
                        redis.call('GET', KEYS[1])
                    )

                    local valor = tonumber(ARGV[2])

                    if not saldo then
                        return -2
                    end

                    if not valor or valor <= 0 then
                        return -3
                    end

                    if saldo < valor then
                        return -1
                    end


                    -- =====================================================
                    -- 3. Obtém o TTL restante do saldo
                    --
                    -- ARGV[5] = margem adicional de idempotência em segundos.
                    --
                    -- Exemplo:
                    --
                    -- saldo restante = 18 horas
                    -- margem          = 12 horas
                    -- refs             = 30 horas
                    -- =====================================================

                    local ttlSaldo = redis.call(
                        'TTL',
                        KEYS[1]
                    )

                    local ttlMargem = tonumber(ARGV[5])

                    local ttlRefs = ttlSaldo + ttlMargem


                    -- =====================================================
                    -- 4. Realiza o débito
                    -- =====================================================

                    local saldoAtual = redis.call(
                        'INCRBYFLOAT',
                        KEYS[1],
                        -valor
                    )


                    -- =====================================================
                    -- 5. Registra o refId
                    --
                    -- Se o Set ainda não existir, o Redis cria
                    -- automaticamente a chave.
                    -- =====================================================

                    redis.call(
                        'SADD',
                        KEYS[2],
                        ARGV[1]
                    )


                    -- =====================================================
                    -- 6. Define o TTL do Set de idempotência
                    --
                    -- O refs terá o TTL restante do saldo + 12 horas.
                    -- =====================================================

                    if ttlRefs > 0 then
                        redis.call(
                            'EXPIRE',
                            KEYS[2],
                            ttlRefs
                        )
                    end


                    -- =====================================================
                    -- 7. Publica o evento no Redis Stream
                    --
                    -- KEYS[3] = saldo-events
                    -- ARGV[3] = eventId
                    -- ARGV[1] = refId
                    -- ARGV[4] = contaId
                    -- ARGV[2] = valor
                    -- saldoAtual = saldo após o débito
                    -- =====================================================

                    redis.call(
                        'XADD',
                        KEYS[3],
                        '*',
                        'eventId', ARGV[3],
                        'refId', ARGV[1],
                        'contaId', ARGV[4],
                        'valor', ARGV[2],
                        'saldoAtual', saldoAtual,
                        'eventType', 'SALDO_DEBITADO'
                    )


                    -- =====================================================
                    -- 8. Sucesso
                    -- =====================================================

                    return 1
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