package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.RedirectView;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoControllerTest {
    @Mock
    private FolhaPagamentoService service;

    @InjectMocks
    private FolhaPagamentoController controller;

    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validatorFactory;

    @BeforeEach
    void configurarMockMvc() {
        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validatorFactory)
                .setViewResolvers((viewName, locale) -> viewName.startsWith("redirect:")
                        ? new RedirectView(viewName.substring("redirect:".length()))
                        : (model, request, response) -> { })
                .build();
    }

    @AfterEach
    void fecharValidator() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("CT-023 - GET / deve exibir página inicial, colaboradores e resumo")
    void paginaInicial() throws Exception {
        when(service.listar()).thenReturn(List.of(new ColaboradorPadrao("001", "Ana", 1_000)));
        when(service.gerarResumo()).thenReturn(new ResumoFolha(1, new BigDecimal("1000.00"),
                java.util.Map.of("Padrão", new BigDecimal("1000.00"))));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("colaboradores", "resumo", "mes", "ano"));
    }

    @Test
    @DisplayName("GET /colaboradores/novo deve exibir formulário padrão de cadastro")
    void formularioNovo() throws Exception {
        mockMvc.perform(get("/colaboradores/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attribute("edicao", false))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @DisplayName("CT-001 - POST /colaboradores deve cadastrar colaborador válido")
    void cadastraColaboradorValido() throws Exception {
        mockMvc.perform(post("/colaboradores")
                        .param("matricula", "001")
                        .param("nome", "Ana")
                        .param("salarioBase", "1000")
                        .param("tipo", "PADRAO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("sucesso", "Colaborador cadastrado."));

        ArgumentCaptor<Colaborador> captor = ArgumentCaptor.forClass(Colaborador.class);
        verify(service).cadastrar(captor.capture());
        assertEquals(new BigDecimal("1000.00"), captor.getValue().getSalarioFinal());
    }

    @Test
    @DisplayName("CT-010 e CT-017 - POST deve vincular erros de validação ao campo correspondente")
    void errosDeValidacaoDoFormulario() throws Exception {
        mockMvc.perform(post("/colaboradores")
                        .param("matricula", "A-1")
                        .param("nome", "Ana")
                        .param("salarioBase", "1000")
                        .param("tipo", "COMISSIONADO")
                        .param("valorVendas", "-1")
                        .param("percentualComissao", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attributeHasFieldErrors("form", "matricula", "valorVendas"));
    }

    @Test
    @DisplayName("RN010 - Campos variáveis ausentes devem ser aceitos como zero")
    void valoresVariaveisAusentesAceitosComoZero() throws Exception {
        mockMvc.perform(post("/colaboradores")
                        .param("matricula", "002")
                        .param("nome", "Bia")
                        .param("salarioBase", "1000")
                        .param("tipo", "COMISSIONADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        ArgumentCaptor<Colaborador> captor = ArgumentCaptor.forClass(Colaborador.class);
        verify(service).cadastrar(captor.capture());
        assertEquals(new BigDecimal("0.00"), captor.getValue().getAdicional());
    }

    @Test
    @DisplayName("CT-009 - POST deve devolver duplicidade como erro de matrícula")
    void cadastroDuplicado() throws Exception {
        doThrow(new IllegalArgumentException("Já existe colaborador com essa matrícula."))
                .when(service).cadastrar(any(Colaborador.class));

        mockMvc.perform(post("/colaboradores")
                        .param("matricula", "001")
                        .param("nome", "Ana")
                        .param("salarioBase", "1000")
                        .param("tipo", "PADRAO"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attributeHasFieldErrors("form", "matricula"));
    }

    @Test
    @DisplayName("CT-027 - GET /folha/{matricula} deve mostrar folha individual existente")
    void folhaIndividualExistente() throws Exception {
        FolhaProcessada colaborador = new FolhaProcessada(1L, "001", "Ana", "PADRAO",
                java.time.YearMonth.now().getMonthValue(), java.time.YearMonth.now().getYear(),
                new BigDecimal("1000.00"), BigDecimal.ZERO.setScale(2),
                new BigDecimal("1000.00"), OffsetDateTime.now());
        when(service.buscar("001")).thenReturn(Optional.of(new ColaboradorPadrao("001", "Ana", 1_000)));
        when(service.buscarFolha("001", colaborador.mes(), colaborador.ano()))
                .thenReturn(Optional.of(colaborador));

        mockMvc.perform(get("/folha/001").param("mes", Integer.toString(colaborador.mes()))
                        .param("ano", Integer.toString(colaborador.ano())))
                .andExpect(status().isOk())
                .andExpect(view().name("folha-individual"))
                .andExpect(model().attribute("folha", colaborador));
    }

    @Test
    @DisplayName("GET /folha/{matricula} inexistente deve redirecionar com mensagem")
    void folhaIndividualInexistente() throws Exception {
        when(service.buscar("999")).thenReturn(Optional.empty());
        when(service.buscarFolha("999", 3, 2099)).thenReturn(Optional.empty());

        mockMvc.perform(get("/folha/999").param("mes", "3").param("ano", "2099"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("erro", "Colaborador não encontrado."));
    }

    @Test
    @DisplayName("CT-026 - GET /folha deve mostrar lista consolidada e resumo")
    void folhaConsolidada() throws Exception {
        when(service.consultarFolha(3, 2099)).thenReturn(List.of());
        when(service.resumir(List.of())).thenReturn(
                new ResumoFolha(0, BigDecimal.ZERO, java.util.Map.of()));

        mockMvc.perform(get("/folha").param("mes", "3").param("ano", "2099"))
                .andExpect(status().isOk())
                .andExpect(view().name("folha"))
                .andExpect(model().attributeExists("colaboradores", "resumo"));
    }

    @Test
    @DisplayName("GET /folha deve rejeitar competência inválida sem erro interno")
    void folhaComCompetenciaInvalida() throws Exception {
        mockMvc.perform(get("/folha").param("mes", "13").param("ano", "2099"))
                .andExpect(status().isOk())
                .andExpect(view().name("folha"))
                .andExpect(model().attribute("erro", "Informe uma competência válida."));
    }

    @Test
    @DisplayName("CT-036 - POST deve processar a folha e redirecionar para a competência")
    void processarFolha() throws Exception {
        mockMvc.perform(post("/folha/processar").param("mes", "3").param("ano", "2099"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/folha?mes=3&ano=2099"))
                .andExpect(flash().attribute("sucesso", "Folha processada para 03/2099."));

        verify(service).processarFolha(3, 2099);
    }

    @Test
    @DisplayName("CT-025 - POST de exclusão deve chamar serviço e redirecionar")
    void excluirColaboradorExistente() throws Exception {
        mockMvc.perform(post("/colaboradores/001/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("sucesso", "Colaborador excluído."));

        verify(service).excluir("001");
    }

    @Test
    @DisplayName("POST de exclusão inexistente deve redirecionar com erro")
    void excluirColaboradorInexistente() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalArgumentException("Matrícula não encontrada."))
                .when(service).excluir("999");

        mockMvc.perform(post("/colaboradores/999/excluir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Matrícula não encontrada."));
    }

    @Test
    @DisplayName("GET de edição deve carregar formulário ou redirecionar se ausente")
    void editarColaborador() throws Exception {
        when(service.buscar("001")).thenReturn(Optional.of(new ColaboradorPadrao("001", "Ana", 1_000)));
        when(service.buscar("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/colaboradores/001/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attribute("edicao", true));
        mockMvc.perform(get("/colaboradores/999/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", "Colaborador não encontrado."));
    }

    @Test
    @DisplayName("POST de edição válida deve atualizar cadastro existente")
    void atualizarColaborador() throws Exception {
        mockMvc.perform(post("/colaboradores/001")
                        .param("matricula", "999")
                        .param("nome", "Ana Atualizada")
                        .param("salarioBase", "1200")
                        .param("tipo", "PADRAO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        ArgumentCaptor<Colaborador> captor = ArgumentCaptor.forClass(Colaborador.class);
        verify(service).atualizar(captor.capture());
        assertEquals("001", captor.getValue().getMatricula());
    }

    @Test
    @DisplayName("POST de edição deve exibir erro de negócio sem redirecionar")
    void erroDeNegocioAoAtualizar() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalArgumentException("Matrícula não encontrada."))
                .when(service).atualizar(any(Colaborador.class));

        mockMvc.perform(post("/colaboradores/001")
                        .param("matricula", "001")
                        .param("nome", "Ana")
                        .param("salarioBase", "1200")
                        .param("tipo", "PADRAO"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attribute("erro", "Matrícula não encontrada."));
    }

    @Test
    @DisplayName("POST de edição inválida deve retornar formulário com erro no campo correspondente")
    void atualizarComErroDeValidacao() throws Exception {
        mockMvc.perform(post("/colaboradores/001")
                        .param("matricula", "001")
                        .param("nome", " ")
                        .param("salarioBase", "1200")
                        .param("tipo", "PADRAO"))
                .andExpect(status().isOk())
                .andExpect(view().name("colaboradores/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome"));
    }
}
