package br.scheiner.hexagonal.cadastro.bootstrap;

import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.services.CadastrarPessoaService;
import br.scheiner.hexagonal.cadastro.application.services.ExcluirPessoaService;
import br.scheiner.hexagonal.cadastro.application.services.PessoaConsultaService;
import br.scheiner.hexagonal.cadastro.application.usecases.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    CadastrarPessoaUseCase cadastrarPessoaUseCase(PessoaRepository repository) {
        return new CadastrarPessoaService(repository);
    }
    @Bean PessoaConsultaService pessoaConsultaService(PessoaRepository repository) { return new PessoaConsultaService(repository); }
    @Bean BuscarPessoaUseCase buscarPessoaUseCase(PessoaConsultaService service) { return service; }
    @Bean AtualizarPessoaUseCase atualizarPessoaUseCase(PessoaConsultaService service) { return service; }
    @Bean ExcluirPessoaUseCase excluirPessoaUseCase(PessoaRepository repository) { return new ExcluirPessoaService(repository); }
    @Bean AdicionarEnderecoUseCase adicionarEnderecoUseCase(PessoaConsultaService service) { return service; }
}
