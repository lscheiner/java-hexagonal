package br.scheiner.hexagonal.cadastro.redis.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;

import br.scheiner.hexagonal.cadastro.redis.adapter.ConsumidorSaldoRedis;

@Configuration
public class RedisStreamConfiguration {

    private static final String STREAM = "saldo-events";

    @Bean
    StreamMessageListenerContainer<String, MapRecord<String, Object, String>> saldoStreamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {

        StreamMessageListenerContainerOptions<String, MapRecord<String, Object, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .keySerializer(RedisSerializer.string())
                        .hashKeySerializer(RedisSerializer.string())
                        .hashValueSerializer(RedisSerializer.string())
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();

        return StreamMessageListenerContainer.create(
                connectionFactory,
                options
        );
    }

    @Bean
    Subscription assinaturaSaldoEvents(
            StreamMessageListenerContainer<String, MapRecord<String, Object, String>> container,
            ConsumidorSaldoRedis consumidor,
            @Value("${saldo.consumer-name:${HOSTNAME:consumer-default}}") String nomeConsumidor) {

        return container.receive(
                Consumer.from(
                        ConsumidorSaldoRedis.GRUPO,
                        nomeConsumidor
                ),
                StreamOffset.create(
                        STREAM,
                        ReadOffset.lastConsumed()
                ),
                consumidor
        );
    }
}