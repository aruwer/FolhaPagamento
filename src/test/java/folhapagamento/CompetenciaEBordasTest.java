package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de borda de competência (RF006, RF007, RF010, RNF002) e utilidades de domínio.
 * Criada para cobrir os ramos apontados como não exercitados no relatório JaCoCo.
 */
class CompetenciaEBordasTest {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Transactional
    @TestPropertySource(properties = {
            "spring.datasource.url=jdbc:h2:mem:folha-bordas-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop"
    })
    class Web {
        @Autowired
        private MockMvc mockMvc;

        @ParameterizedTest(name = "mes={0}, ano={1}")
        @CsvSource({ "3,", ",2099", "1,1999", "1,10000", "13,2099", "0,2099" })
        @DisplayName("CT-036, RNF002: consulta com competência inválida exibe erro e resumo vazio")
        void consultaComCompetenciaInvalida(String mes, String ano) throws Exception {
            var requisicao = get("/folha");
            if (mes != null) {
                requisicao.param("mes", mes);
            }
            if (ano != null) {
                requisicao.param("ano", ano);
            }
            mockMvc.perform(requisicao)
                    .andExpect(status().isOk())
                    .andExpect(view().name("folha"))
                    .andExpect(model().attribute("erro", "Informe uma competência válida."));
        }

        @Test
        @DisplayName("CT-036, RNF002: processar com competência inválida não gera folha")
        void processarComCompetenciaInvalida() throws Exception {
            mockMvc.perform(post("/folha/processar").param("mes", "13").param("ano", "2099"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/folha"))
                    .andExpect(flash().attribute("erro", "Informe uma competência válida."));
        }

        @Test
        @DisplayName("CT-036, RF010: demonstrativo individual com competência inválida")
        void demonstrativoComCompetenciaInvalida() throws Exception {
            mockMvc.perform(get("/folha/{matricula}", "X1").param("mes", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("erro", "Informe uma competência válida."));
        }

        @Test
        @DisplayName("CT-027, RF010: demonstrativo de colaborador ativo sem folha processada")
        void demonstrativoDeFolhaNaoProcessada() throws Exception {
            mockMvc.perform(post("/colaboradores")
                            .param("matricula", "B001")
                            .param("nome", "Bia Bordas")
                            .param("salarioBase", "1500")
                            .param("tipo", "PADRAO"))
                    .andExpect(status().is3xxRedirection());

            mockMvc.perform(get("/folha/{matricula}", "B001").param("mes", "4").param("ano", "2099"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/folha?mes=4&ano=2099"))
                    .andExpect(flash().attribute("erro",
                            "A folha desta competência ainda não foi processada."));
        }

        @Test
        @DisplayName("BUG-006, RNF001: competência não processada orienta a processar, não a cadastrar")
        void competenciaNaoProcessadaOrientaProcessamento() throws Exception {
            mockMvc.perform(get("/folha").param("mes", "1").param("ano", "2099"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("ainda não tem folha processada")))
                    .andExpect(content().string(not(containsString("Cadastre colaboradores para gerar"))));
            mockMvc.perform(get("/folha").param("mes", "13").param("ano", "2099"))
                    .andExpect(content().string(containsString("Selecione um mês e um ano válidos")));
        }

        @Test
        @DisplayName("RF007: sem parâmetros, a folha usa a competência corrente")
        void folhaSemParametrosUsaCompetenciaAtual() throws Exception {
            java.time.YearMonth agora = java.time.YearMonth.now();
            mockMvc.perform(get("/folha"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("mes", agora.getMonthValue()))
                    .andExpect(model().attribute("ano", agora.getYear()));
        }
    }

    @Nested
    class Dominio {
        @Test
        @DisplayName("RN010: formatador apresenta BigDecimal nulo como R$ 0,00 e Double preenchido")
        void formatadorTrataNulosEPreenchidos() {
            FormatadorMoeda formatador = new FormatadorMoeda();
            assertTrue(formatador.formatar((BigDecimal) null).contains("0,00"));
            assertTrue(formatador.formatar(new BigDecimal("10.50")).contains("10,50"));
            assertTrue(formatador.formatar(Double.valueOf(7.25)).contains("7,25"));
        }

        @Test
        @DisplayName("RN003: validação numérica rejeita negativo e valores não finitos")
        void validacaoNumericaRejeitaNegativoENaoFinito() {
            assertThrows(IllegalArgumentException.class,
                    () -> Colaborador.validarNaoNegativo(-0.01, "Valor"));
            assertThrows(IllegalArgumentException.class,
                    () -> Colaborador.validarNaoNegativo(Double.NaN, "Valor"));
            assertThrows(IllegalArgumentException.class,
                    () -> Colaborador.validarNaoNegativo(Double.POSITIVE_INFINITY, "Valor"));
            Colaborador.validarNaoNegativo(0.0, "Valor");
        }

        @Test
        @DisplayName("RN008: valores monetários com mais de duas casas são rejeitados")
        void rejeitaMaisDeDuasCasasDecimais() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ColaboradorPadrao("M1", "Teste", new BigDecimal("10.001")));
            assertEquals(new BigDecimal("10.00"),
                    new ColaboradorPadrao("M1", "Teste", new BigDecimal("10.0000")).getSalarioBase());
        }

        @Test
        @DisplayName("RF010: tipo desconhecido no snapshot é exibido como está")
        void tipoDesconhecidoNoSnapshot() {
            FolhaProcessada folha = new FolhaProcessada(1L, "M1", "Teste", "ESTAGIO", 1, 2099,
                    BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, null);
            assertEquals("ESTAGIO", folha.tipoDescricao());
            assertEquals("estagio", folha.codigoTipo());
        }

        @Test
        @DisplayName("BUG-005: data de processamento é normalizada para o fuso do sistema")
        void normalizaDataDeProcessamento() {
            OffsetDateTime utc = OffsetDateTime.of(2099, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime local = FolhaProcessadaJpaEntity.noFusoDoSistema(utc);
            assertTrue(local.isEqual(utc));
            assertEquals(ZoneId.systemDefault().getRules().getOffset(utc.toInstant()), local.getOffset());
            assertNull(FolhaProcessadaJpaEntity.noFusoDoSistema(null));
        }
    }
}
