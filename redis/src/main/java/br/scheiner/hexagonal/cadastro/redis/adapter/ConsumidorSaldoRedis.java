package br.scheiner.hexagonal.cadastro.redis.adapter;

import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarEventoSaldo;
import br.scheiner.hexagonal.cadastro.redis.mapper.EventoSaldoMapper;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorSaldoRedis implements StreamListener<String, MapRecord<String, Object, String>> {

    public static final String GRUPO = "saldo-consumers";

    private final ProcessarEventoSaldo processarEventoSaldo;
    private final EventoSaldoMapper eventoSaldoMapper;
    private final StringRedisTemplate redis;

    public ConsumidorSaldoRedis(ProcessarEventoSaldo processarEventoSaldo, EventoSaldoMapper eventoSaldoMapper,
            StringRedisTemplate redis) {
        this.processarEventoSaldo = processarEventoSaldo;
        this.eventoSaldoMapper = eventoSaldoMapper;
        this.redis = redis;
    }

    @Override
    public void onMessage(MapRecord<String, Object, String> mensagem) {
        processarEventoSaldo.processar(eventoSaldoMapper.map(mensagem));
        redis.opsForStream().acknowledge(GRUPO, mensagem);
    }
}
