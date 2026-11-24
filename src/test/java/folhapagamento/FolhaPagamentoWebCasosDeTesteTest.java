package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:folha-web-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FolhaPagamentoWebCasosDeTesteTest {
    private static final int ANO = 2099;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("CT-001: cadastro padrão e salário final (ACE-1)")
    void ct001CalculaSalarioPadrao() throws Exception {
        cadastra(formulario("W001", "Ana Padrão", "2500", "PADRAO"));
        processa(1, ANO);

        FolhaProcessada folha = folhaIndividual("W001", 1);
        assertDinheiro(folha.adicional(), "0.00");
        assertDinheiro(folha.salarioFinal(), "2500.00");
    }

    @Test
    @DisplayName("CT-003, CT-004 e CT-005: cálculo e comissão nula (ACE-3)")
    void ct003CalculaComissaoOuZero() throws Exception {
        cadastra(comissionado("W003", "Bruno Comissão", "2000", "10000", "5"));
        cadastra(comissionado("W004", "Caio Sem Vendas", "2000", "0", "5"));
        cadastra(comissionado("W005", "Dora Sem Percentual", "2000", "10000", "0"));
        processa(1, ANO);

        assertFolha("W003", 500, 2500);
        assertFolha("W004", 0, 2000);
        assertFolha("W005", 0, 2000);
    }

    @Test
    @DisplayName("CT-006, CT-007 e CT-008: cálculo e adicional de produção nulo (ACE-3)")
    void ct006CalculaProducaoOuZero() throws Exception {
        cadastra(producao("W006", "Eva Produção", "1800", "120", "2.50"));
        cadastra(producao("W007", "Fabio Sem Quantidade", "1800", "0", "2.50"));
        cadastra(producao("W008", "Gabi Sem Valor", "1800", "120", "0"));
        processa(1, ANO);

        assertFolha("W006", 300, 2100);
        assertFolha("W007", 0, 1800);
        assertFolha("W008", 0, 1800);
    }

    @Test
    @DisplayName("CT-009: matrícula duplicada é rejeitada sem duplicar o registro (ACE-1)")
    void ct009RejeitaMatriculaDuplicada() throws Exception {
        cadastra(formulario("W009", "Helena Original", "2100", "PADRAO"))
                .andExpect(status().is3xxRedirection());
        assertCampoInvalido(formulario("W009", "Helena Duplicada", "2100", "PADRAO"), "matricula");
        assertEquals(1, listaColaboradores().size());
    }

    @Test
    @DisplayName("CT-010: matrícula com pontuação é rejeitada (ACE-2)")
    void ct010RejeitaMatriculaComPontuacao() throws Exception {
        assertCampoInvalido(formulario("A-102", "Ivo Teste", "2200", "PADRAO"), "matricula");
        assertTrue(listaColaboradores().isEmpty());
    }

    @Test
    @DisplayName("CT-011 e CT-012: matrícula e nome obrigatórios (ACE-2)")
    void ct011E012ValidaMatriculaENomeObrigatorios() throws Exception {
        assertCampoInvalido(formulario("", "Joana Silva", "2300", "PADRAO"), "matricula");
        assertCampoInvalido(formulario("W012", "   ", "2300", "PADRAO"), "nome");
        assertTrue(listaColaboradores().isEmpty());
    }

    @Test
    @DisplayName("CT-013: salário base obrigatório (ACE-2)")
    void ct013ValidaSalarioBaseObrigatorio() throws Exception {
        Map<String, String> dados = formulario("W013", "Katia Silva", null, "PADRAO");
        assertCampoInvalido(dados, "salarioBase");
        assertTrue(listaColaboradores().isEmpty());
    }

    @Test
    @DisplayName("CT-014, CT-017, CT-018, CT-021 e CT-022: valores negativos são rejeitados (ACE-2)")
    void ct014E018RejeitaValoresNegativos() throws Exception {
        List<Map.Entry<Map<String, String>, String>> invalidos = List.of(
                Map.entry(formulario("W014", "Lia Salário", "-0.01", "PADRAO"), "salarioBase"),
                Map.entry(comissionado("W017", "Luca Vendas", "2000", "-0.01", "5"), "valorVendas"),
                Map.entry(comissionado("W018", "Mia Comissão", "2000", "10000", "-0.01"),
                        "percentualComissao"),
                Map.entry(producao("W021", "Nina Quantidade", "1800", "-1", "2.50"),
                        "quantidadeProduzida"),
                Map.entry(producao("W022", "Otavio Unidade", "1800", "120", "-0.01"),
                        "valorPorUnidade"));

        for (Map.Entry<Map<String, String>, String> invalido : invalidos) {
            assertCampoInvalido(invalido.getKey(), invalido.getValue());
        }
        assertTrue(listaColaboradores().isEmpty());
    }

    @Test
    @DisplayName("CT-015 e CT-016: campos de comissão em branco valem zero (ACE-2)")
    void ct015E016AceitaCamposComissionadosEmBranco() throws Exception {
        cadastra(comissionado("W015", "Paula Sem Vendas", "2000", null, "5"))
                .andExpect(status().is3xxRedirection());
        cadastra(comissionado("W016", "Rui Sem Percentual", "2000", "10000", null))
                .andExpect(status().is3xxRedirection());
        processa(1, ANO);

        assertFolha("W015", 0, 2000);
        assertFolha("W016", 0, 2000);
    }

    @Test
    @DisplayName("CT-019 e CT-020: campos de produção em branco valem zero (ACE-2)")
    void ct019E020AceitaCamposProducaoEmBranco() throws Exception {
        cadastra(producao("W019", "Sara Sem Quantidade", "1800", null, "2.50"))
                .andExpect(status().is3xxRedirection());
        cadastra(producao("W020", "Tadeu Sem Valor", "1800", "120", null))
                .andExpect(status().is3xxRedirection());
        processa(1, ANO);

        assertFolha("W019", 0, 1800);
        assertFolha("W020", 0, 1800);
    }

    @Test
    @DisplayName("CT-023: lista colaboradores das três categorias (ACE-1)")
    void ct023ListaTresCategorias() throws Exception {
        cadastra(formulario("W023A", "Uma Padrão", "1500", "PADRAO"));
        cadastra(comissionado("W023B", "Vera Comissão", "1800", "4000", "5"));
        cadastra(producao("W023C", "Wes Produção", "1600", "20", "3"));

        assertEquals(3, listaColaboradores().size());
    }

    @Test
    @DisplayName("CT-024: atualiza nome e salário base do colaborador (ACE-1)")
    void ct024AtualizaColaborador() throws Exception {
        cadastra(formulario("W024", "Xavier Original", "2000", "PADRAO"));
        Map<String, String> dadosAtualizados = formulario("W024", "Xavier Atualizado", "2300", "PADRAO");
        postar("/colaboradores/W024", dadosAtualizados)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        List<?> colaboradores = listaColaboradores();
        assertEquals(1, colaboradores.size());
        Colaborador atualizado = (Colaborador) colaboradores.get(0);
        assertEquals("Xavier Atualizado", atualizado.getNome());
        assertDinheiro(atualizado.getSalarioBase(), "2300.00");
    }

    @Test
    @DisplayName("CT-025: exclusão lógica remove o colaborador da lista ativa (ACE-1)")
    void ct025ExcluiColaboradorAtivo() throws Exception {
        cadastra(formulario("W025", "Yara Excluída", "2500", "PADRAO"));
        mockMvc.perform(post("/colaboradores/W025/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(listaColaboradores().isEmpty());
    }

    @Test
    @DisplayName("CT-026, CT-028 e CT-029: total e subtotais por categoria (ACE-5)")
    void ct026CalculaResumoPorCategoria() throws Exception {
        cadastra(formulario("W026A", "Zeca Padrão", "2500", "PADRAO"));
        cadastra(comissionado("W026B", "Ana Comissão", "2000", "10000", "5"));
        cadastra(producao("W026C", "Beto Produção", "1800", "120", "2.50"));
        processa(1, ANO);

        ResumoFolha resumo = resumoDaFolha(1, ANO);
        assertEquals(3, resumo.quantidadeColaboradores());
        assertDinheiro(resumo.totalFolha(), "7100.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Padrão"), "2500.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Comissionado"), "2500.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Por produção"), "2100.00");
    }

    @Test
    @DisplayName("CT-039: resumo por categoria considera só a competência selecionada (ACE-4, ACE-5)")
    void ct039ResumePorCategoriaECompetencia() throws Exception {
        cadastra(formulario("W039A", "Iris Padrão", "2500", "PADRAO"));
        cadastra(comissionado("W039B", "Jonas Comissão", "2000", "10000", "5"));
        cadastra(producao("W039C", "Kelly Produção", "1800", "120", "2.50"));
        processa(3, ANO);
        cadastra(formulario("W039D", "Lia Distratora", "9000", "PADRAO"));
        processa(4, ANO);
        processa(3, ANO - 1);

        ResumoFolha resumo = resumoDaFolha(3, ANO);
        assertEquals(3, resumo.quantidadeColaboradores());
        assertDinheiro(resumo.totalFolha(), "7100.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Padrão"), "2500.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Comissionado"), "2500.00");
        assertDinheiro(resumo.totaisPorCategoria().get("Por produção"), "2100.00");
        assertDinheiro(resumoDaFolha(4, ANO).totalFolha(), "16100.00");
        assertEquals(4, resumoDaFolha(3, ANO - 1).quantidadeColaboradores());
    }

    @Test
    @DisplayName("CT-031: resumo de folha sem colaboradores é vazio (ACE-5)")
    void ct031ResumoVazio() throws Exception {
        ResumoFolha resumo = resumoDaFolha(1, ANO);

        assertEquals(0, resumo.quantidadeColaboradores());
        assertDinheiro(resumo.totalFolha(), "0.00");
        assertTrue(resumo.totaisPorCategoria().isEmpty());
    }

    @Test
    @DisplayName("CT-027: demonstrativo individual exibe comissão e salário final (ACE-3)")
    void ct027ExibeFolhaIndividualComissionada() throws Exception {
        cadastra(comissionado("W027", "Caio Demonstrativo", "2000", "10000", "5"));
        processa(1, ANO);

        FolhaProcessada folha = folhaIndividual("W027", 1);
        assertDinheiro(folha.adicional(), "500.00");
        assertDinheiro(folha.salarioFinal(), "2500.00");
    }

    @Test
    @DisplayName("CT-036: filtra folha por competência, mês e ano (ACE-4)")
    void ct036FiltraPorCompetencia() throws Exception {
        cadastra(formulario("W036", "Davi Competência", "2500", "PADRAO"));
        processa(3, ANO);

        assertEquals(1, folhaDoMes(3, ANO).size());
        assertTrue(folhaDoMes(4, ANO).isEmpty());
        assertTrue(folhaDoMes(3, 2025).isEmpty());
    }

    @Test
    @DisplayName("CT-037: snapshot individual permanece após exclusão (ACE-4)")
    void ct037PreservaSnapshotDepoisDaExclusao() throws Exception {
        cadastra(formulario("W037", "Elisa Histórico", "2500", "PADRAO"));
        processa(2, ANO);
        mockMvc.perform(post("/colaboradores/W037/excluir"))
                .andExpect(status().is3xxRedirection());

        assertTrue(listaColaboradores().isEmpty());
        assertDinheiro(folhaIndividual("W037", 2).salarioFinal(), "2500.00");
    }

    @Test
    @DisplayName("CT-038: valores variáveis zero são exibidos como adicional zero (ACE-5)")
    void ct038ExibeAdicionaisZerados() throws Exception {
        cadastra(comissionado("W038A", "Fabi Comissão Zero", "2000", "0", "5"));
        cadastra(producao("W038B", "Gui Produção Zero", "1800", "0", "2.50"));
        processa(1, ANO);

        assertDinheiro(folhaIndividual("W038A", 1).adicional(), "0.00");
        assertDinheiro(folhaIndividual("W038B", 1).adicional(), "0.00");
    }

    @Test
    @DisplayName("CT-038 variante: vendas em branco resultam em adicional zero (ACE-5)")
    void ct038BrancoAceitaVendasEmBranco() throws Exception {
        cadastra(comissionado("W038C", "Hugo Sem Vendas", "2000", null, "5"));
        processa(1, ANO);

        assertDinheiro(folhaIndividual("W038C", 1).adicional(), "0.00");
    }

    private void assertFolha(String matricula, double adicional, double salarioFinal) throws Exception {
        FolhaProcessada folha = folhaIndividual(matricula, 1);
        assertDinheiro(folha.adicional(), BigDecimal.valueOf(adicional));
        assertDinheiro(folha.salarioFinal(), BigDecimal.valueOf(salarioFinal));
    }

    private void assertDinheiro(BigDecimal atual, String esperado) {
        assertDinheiro(atual, new BigDecimal(esperado));
    }

    private void assertDinheiro(BigDecimal atual, BigDecimal esperado) {
        assertNotNull(atual);
        assertEquals(0, esperado.compareTo(atual));
    }

    private Map<String, String> formulario(String matricula, String nome, String salarioBase, String tipo) {
        Map<String, String> dados = new LinkedHashMap<>();
        if (matricula != null) {
            dados.put("matricula", matricula);
        }
        if (nome != null) {
            dados.put("nome", nome);
        }
        if (salarioBase != null) {
            dados.put("salarioBase", salarioBase);
        }
        dados.put("tipo", tipo);
        return dados;
    }

    private Map<String, String> comissionado(String matricula, String nome, String salarioBase,
            String vendas, String percentual) {
        Map<String, String> dados = formulario(matricula, nome, salarioBase, "COMISSIONADO");
        if (vendas != null) {
            dados.put("valorVendas", vendas);
        }
        if (percentual != null) {
            dados.put("percentualComissao", percentual);
        }
        return dados;
    }

    private Map<String, String> producao(String matricula, String nome, String salarioBase,
            String quantidade, String valorPorUnidade) {
        Map<String, String> dados = formulario(matricula, nome, salarioBase, "PRODUCAO");
        if (quantidade != null) {
            dados.put("quantidadeProduzida", quantidade);
        }
        if (valorPorUnidade != null) {
            dados.put("valorPorUnidade", valorPorUnidade);
        }
        return dados;
    }

    private org.springframework.test.web.servlet.ResultActions cadastra(Map<String, String> dados)
            throws Exception {
        return postar("/colaboradores", dados);
    }

    private org.springframework.test.web.servlet.ResultActions postar(String url, Map<String, String> dados)
            throws Exception {
        MockHttpServletRequestBuilder requisicao = post(url);
        dados.forEach((campo, valor) -> requisicao.param(campo, valor));
        return mockMvc.perform(requisicao);
    }

    private void assertCampoInvalido(Map<String, String> dados, String campo) throws Exception {
        cadastra(dados)
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attributeHasFieldErrors("form", campo));
    }

    private void processa(int mes, int ano) throws Exception {
        mockMvc.perform(post("/folha/processar")
                        .param("mes", Integer.toString(mes))
                        .param("ano", Integer.toString(ano)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/folha?mes=" + mes + "&ano=" + ano))
                .andExpect(flash().attributeExists("sucesso"));
    }

    private FolhaProcessada folhaIndividual(String matricula, int mes) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/folha/{matricula}", matricula)
                        .param("mes", Integer.toString(mes))
                        .param("ano", Integer.toString(ANO)))
                .andExpect(status().isOk())
                .andExpect(view().name("folha-individual"))
                .andExpect(model().attributeExists("folha"))
                .andReturn();
        return (FolhaProcessada) resultado.getModelAndView().getModel().get("folha");
    }

    private List<?> folhaDoMes(int mes, int ano) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/folha")
                        .param("mes", Integer.toString(mes))
                        .param("ano", Integer.toString(ano)))
                .andExpect(status().isOk())
                .andExpect(view().name("folha"))
                .andExpect(model().attributeExists("colaboradores"))
                .andReturn();
        return (List<?>) resultado.getModelAndView().getModel().get("colaboradores");
    }

    private ResumoFolha resumoDaFolha(int mes, int ano) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/folha")
                        .param("mes", Integer.toString(mes))
                        .param("ano", Integer.toString(ano)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("resumo"))
                .andReturn();
        return (ResumoFolha) resultado.getModelAndView().getModel().get("resumo");
    }

    private List<?> listaColaboradores() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("colaboradores"))
                .andReturn();
        List<?> colaboradores = (List<?>) resultado.getModelAndView().getModel().get("colaboradores");
        return new ArrayList<>(colaboradores);
    }
}
