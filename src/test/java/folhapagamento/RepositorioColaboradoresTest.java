package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:folha-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RepositorioColaboradoresTest {
    @Autowired
    private FolhaPagamentoService service;

    @Test
    @DisplayName("CT-009, CT-023: persistência dos três tipos e matrícula duplicada")
    void persisteTiposDeColaboradorERejeitaDuplicidade() throws Exception {
        String padrao = matriculaUnica();
        String comissionado = matriculaUnica();
        String producao = matriculaUnica();
        service.cadastrar(new ColaboradorPadrao(padrao, "Ana Silva", new BigDecimal("1000.25")));
        service.cadastrar(new ColaboradorComissionado(comissionado, "Bia Costa",
                new BigDecimal("2000.00"), new BigDecimal("500.00"), new BigDecimal("10.00")));
        service.cadastrar(new ColaboradorProducao(producao, "Caio Lima",
                new BigDecimal("1500.00"), new BigDecimal("12.500"), new BigDecimal("2.75")));

        assertEquals(new BigDecimal("1000.25"), service.buscar(padrao).orElseThrow().getSalarioFinal());
        assertEquals(new BigDecimal("2050.00"), service.buscar(comissionado).orElseThrow().getSalarioFinal());
        assertEquals(new BigDecimal("1534.38"), service.buscar(producao).orElseThrow().getSalarioFinal());
        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar(new ColaboradorPadrao(padrao, "Duplicada", 1_000)));
    }

    @Test
    @DisplayName("CT-024, CT-025: atualização e exclusão lógica")
    void atualizaCadastroEExcluiColaboradorAtivo() throws Exception {
        String matricula = matriculaUnica();
        service.cadastrar(new ColaboradorPadrao(matricula, "Cadastro Inicial", 2_000));
        service.atualizar(new ColaboradorPadrao(matricula, "Cadastro Atualizado", 2_500));

        assertEquals("Cadastro Atualizado", service.buscar(matricula).orElseThrow().getNome());
        service.excluir(matricula);

        assertTrue(service.buscar(matricula).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(new ColaboradorPadrao(matricula, "Inativo", 1_000)));
        assertThrows(IllegalArgumentException.class, () -> service.excluir(matricula));
    }

    @Test
    @DisplayName("CT-037: histórico preservado após exclusão lógica")
    void preservaSnapshotDaFolhaDepoisDeAtualizarEExcluir() throws Exception {
        String matricula = matriculaUnica();
        YearMonth competencia = YearMonth.now();
        service.cadastrar(new ColaboradorComissionado(matricula, "Histórico",
                new BigDecimal("2000.00"), new BigDecimal("1000.00"), new BigDecimal("5.00")));
        FolhaProcessada original = service.processarFolha(
                        competencia.getMonthValue(), competencia.getYear()).stream()
                .filter(folha -> folha.matricula().equals(matricula))
                .findFirst()
                .orElseThrow();

        service.atualizar(new ColaboradorPadrao(matricula, "Nome Atualizado", new BigDecimal("3000.00")));
        service.excluir(matricula);

        assertTrue(service.buscar(matricula).isEmpty());
        assertEquals(original, service.buscarFolha(
                matricula, competencia.getMonthValue(), competencia.getYear()).orElseThrow());
        assertTrue(service.consultarFolha(competencia.getMonthValue(), competencia.getYear())
                .contains(original));
    }

    @Test
    @DisplayName("CT-036: processamento idempotente e competência inválida rejeitada")
    void processamentoELeituraSaoIdempotentesERejeitamCompetenciaInvalida() throws Exception {
        String matricula = matriculaUnica();
        service.cadastrar(new ColaboradorPadrao(matricula, "Competência", 1_000));
        var primeiro = service.processarFolha(3, 2099).stream()
                .filter(folha -> folha.matricula().equals(matricula))
                .findFirst()
                .orElseThrow();
        service.atualizar(new ColaboradorPadrao(matricula, "Atualizado", 2_000));

        assertEquals(primeiro, service.processarFolha(3, 2099).stream()
                .filter(folha -> folha.matricula().equals(matricula))
                .findFirst()
                .orElseThrow());
        assertTrue(service.consultarFolha(3, 2001).stream()
                .noneMatch(folha -> folha.matricula().equals(matricula)));
        assertThrows(IllegalArgumentException.class, () -> service.consultarFolha(0, 2099));
        assertThrows(IllegalArgumentException.class, () -> service.buscarFolha(matricula, 13, 2099));
        assertThrows(IllegalArgumentException.class, () -> service.consultarFolha(1, 1999));
    }

    private String matriculaUnica() {
        return "H" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
