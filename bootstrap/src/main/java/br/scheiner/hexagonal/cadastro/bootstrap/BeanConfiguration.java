package br.scheiner.hexagonal.cadastro.bootstrap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.scheiner.hexagonal.cadastro.application.ports.in.ContaService;
import br.scheiner.hexagonal.cadastro.application.ports.in.DebitarSaldo;
import br.scheiner.hexagonal.cadastro.application.ports.in.PessoaService;
import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarEventoSaldo;
import br.scheiner.hexagonal.cadastro.application.ports.in.ProcessarOutbox;
import br.scheiner.hexagonal.cadastro.application.ports.out.ContaRepository;
import br.scheiner.hexagonal.cadastro.application.ports.out.OutboxRepository;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.ports.out.SaldoStore;
import br.scheiner.hexagonal.cadastro.application.services.ContaServiceImpl;
import br.scheiner.hexagonal.cadastro.application.services.DebitarSaldoService;
import br.scheiner.hexagonal.cadastro.application.services.PessoaServiceImpl;
import br.scheiner.hexagonal.cadastro.application.services.ProcessarEventoSaldoService;
import br.scheiner.hexagonal.cadastro.application.services.ProcessarOutboxService;

@Configuration
public class BeanConfiguration {

	@Bean
	public PessoaService pessoaService(PessoaRepository repository) {
		return new PessoaServiceImpl(repository);
	}

	@Bean
	public ContaService contaService(ContaRepository repository) {
		return new ContaServiceImpl(repository);
	}

	@Bean
	public DebitarSaldo debitarSaldo(SaldoStore saldoStore) {
		return new DebitarSaldoService(saldoStore);
	}

	@Bean
	public ProcessarOutbox processarOutbox(OutboxRepository outbox, SaldoStore saldoStore) {
		return new ProcessarOutboxService(outbox, saldoStore);
	}

	@Bean
	public ProcessarEventoSaldo processarEventoSaldo() {
		return new ProcessarEventoSaldoService();
	}
	
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
