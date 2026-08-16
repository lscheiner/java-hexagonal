package br.scheiner.hexagonal.cadastro.bootstrap;

import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.services.PessoaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PessoaService pessoaService(PessoaRepository repository) {
        return new PessoaService(repository);
    }
}
