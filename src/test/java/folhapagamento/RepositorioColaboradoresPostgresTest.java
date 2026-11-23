package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers(disabledWithoutDocker = true)
class RepositorioColaboradoresPostgresTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("folha_teste")
            .withUsername("teste")
            .withPassword("teste");

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private FolhaPagamentoService service;

    @Test
    void persisteCadastroERejeitaMatriculaDuplicada() throws Exception {
        String matricula = matriculaUnica();
        service.cadastrar(new ColaboradorComissionado(
                matricula, "Ana Teste", new BigDecimal("2000.00"),
                new BigDecimal("1000.00"), new BigDecimal("5.00")));

        assertEquals(new BigDecimal("2050.00"), service.buscar(matricula).orElseThrow().getSalarioFinal());
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(
                new ColaboradorPadrao(matricula, "Duplicado", new BigDecimal("1000.00"))));
    }

    @Test
    void congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete() throws Exception {
        String matricula = matriculaUnica();
        service.cadastrar(new ColaboradorPadrao(matricula, "Colaborador Histórico",
                new BigDecimal("2500.00")));

        FolhaProcessada processada = service.processarFolha(2, 2099).stream()
                .filter(item -> item.matricula().equals(matricula))
                .findFirst()
                .orElseThrow();
        service.atualizar(new ColaboradorPadrao(matricula, "Nome Atualizado",
                new BigDecimal("3000.00")));
        assertEquals(processada, service.buscarFolha(matricula, 2, 2099).orElseThrow());
        assertEquals(new BigDecimal("3000.00"), service.processarFolha(3, 2099).stream()
                .filter(item -> item.matricula().equals(matricula))
                .findFirst()
                .orElseThrow().salarioBase());

        service.excluir(matricula);

        assertTrue(service.buscar(matricula).isEmpty());
        assertTrue(service.consultarFolha(2, 2099).stream()
                .anyMatch(item -> item.matricula().equals(matricula)));
    }

    @Test
    void validaCompetenciaEPrecisaoMonetaria() {
        ColaboradorComissionado colaborador = new ColaboradorComissionado(
                matriculaUnica(), "Arredondamento", new BigDecimal("1.00"),
                new BigDecimal("0.01"), new BigDecimal("50.00"));
        assertEquals(new BigDecimal("0.01"), colaborador.getAdicional());
        assertThrows(IllegalArgumentException.class, () -> service.consultarFolha(13, 2099));
    }

    private String matriculaUnica() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
