package br.scheiner.hexagonal.cadastro.redis.mapper;

import java.math.BigDecimal;
import java.util.UUID;
import br.scheiner.hexagonal.cadastro.application.mapper.Mapper;
import br.scheiner.hexagonal.cadastro.application.saldo.EventoSaldo;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Component
public class EventoSaldoMapper implements Mapper<MapRecord<String, Object, String>, EventoSaldo> {

    @Override
    public EventoSaldo map(MapRecord<String, Object, String> record) {
        if (record == null) return null;
        var valores = record.getValue();
        return new EventoSaldo(
                UUID.fromString(valores.get("eventId")),
                UUID.fromString(valores.get("refId")),
                UUID.fromString(valores.get("contaId")),
                new BigDecimal(valores.get("valor")),
                new BigDecimal(valores.get("saldoAtual")),
                valores.get("eventType"));
    }
}
