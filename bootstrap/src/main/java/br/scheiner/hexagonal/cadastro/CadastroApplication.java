package br.scheiner.hexagonal.cadastro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CadastroApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadastroApplication.class, args);
    }
}
