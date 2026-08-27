package br.scheiner.hexagonal.cadastro.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import br.scheiner.hexagonal.cadastro.application.ports.in.PessoaService;
import br.scheiner.hexagonal.cadastro.application.ports.out.PessoaRepository;
import br.scheiner.hexagonal.cadastro.application.services.PessoaServiceImpl;
import br.scheiner.hexagonal.cadastro.persistence.adapter.JpaPessoaRepository;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "br.scheiner.hexagonal.cadastro";

    private final com.tngtech.archunit.core.domain.JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

    @Test
    void domainNaoDeveDependerDasCamadasExternas() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..api..", "..persistence..", "..bootstrap..")
                .check(classes);
    }

    @Test
    void domainNaoDeveConhecerFrameworks() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..")
                .check(classes);
    }

    @Test
    void applicationNaoDeveDependerDeAdaptersOuFrameworks() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..api..",
                        "..persistence..",
                        "..bootstrap..",
                        "org.springframework..",
                        "jakarta.persistence..")
                .check(classes);
    }

    @Test
    void adaptersNaoDevemDependerUnsDosOutros() {
        noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..persistence..", "..bootstrap..")
                .check(classes);

        noClasses().that().resideInAPackage("..persistence..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..bootstrap..")
                .check(classes);
    }

    @Test
    void portasDeEntradaESaidaDevemSerInterfaces() {
        classes().that().resideInAPackage("..application.ports.in..")
                .should().beInterfaces()
                .check(classes);

        classes().that().resideInAPackage("..application.ports.out..")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void adaptersDeEntradaDevemDependerSomenteDasPortasDeEntrada() {
        noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application.services..")
                .check(classes);
    }

    @Test
    void implementacaoDoCasoDeUsoDeveImplementarAPortaDeEntrada() {
        classes().that().areAssignableTo(PessoaServiceImpl.class)
                .should().implement(PessoaService.class)
                .check(classes);
    }

    @Test
    void adapterJpaDeveImplementarAPortaDeSaida() {
        classes().that().areAssignableTo(JpaPessoaRepository.class)
                .should().implement(PessoaRepository.class)
                .check(classes);
    }
}
