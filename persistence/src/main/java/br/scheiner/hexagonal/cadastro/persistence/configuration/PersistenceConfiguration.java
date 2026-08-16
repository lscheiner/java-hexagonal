package br.scheiner.hexagonal.cadastro.persistence.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:persistence.properties")
public class PersistenceConfiguration {
}
