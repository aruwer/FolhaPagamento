package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoServiceTest {

    @Mock
    private RepositorioColaboradores repositorio;

    @InjectMocks
    private FolhaPagamentoService service;

    @Test
    @DisplayName("CT-028 e CT-029 - Deve gerar total geral e totais por categoria")
    void gerarResumoCalculaTotalEValoresPorCategoria() {
        when(repositorio.listar()).thenReturn(List.of(
                new ColaboradorPadrao("001", "Ana", 2_000),
                new ColaboradorComissionado("002", "Bruno", 1_500, 10_000, 5),
                new ColaboradorProducao("003", "Carla", 1_000, 100, 2)));

        ResumoFolha resumo = service.gerarResumo();

        assertEquals(3, resumo.quantidadeColaboradores());
        assertEquals(new BigDecimal("5200.00"), resumo.totalFolha());
        assertEquals(new BigDecimal("2000.00"), resumo.totaisPorCategoria().get("Padrão"));
        assertEquals(new BigDecimal("2000.00"), resumo.totaisPorCategoria().get("Comissionado"));
        assertEquals(new BigDecimal("1200.00"), resumo.totaisPorCategoria().get("Por produção"));
        assertEquals(1, resumo.quantidadePorCategoria().get("Padrão"));
    }

    @Test
    @DisplayName("CT-031 - Deve gerar resumo vazio")
    void gerarResumoVazio() {
        when(repositorio.listar()).thenReturn(List.of());

        ResumoFolha resumo = service.gerarResumo();

        assertEquals(0, resumo.quantidadeColaboradores());
        assertEquals(BigDecimal.ZERO, resumo.totalFolha());
        assertTrue(resumo.totaisPorCategoria().isEmpty());
    }

    @Test
    @DisplayName("Deve expor totais por categoria somente para leitura")
    void totaisPorCategoriaNaoPodeSerAlterado() {
        when(repositorio.listar()).thenReturn(List.of(new ColaboradorPadrao("001", "Ana", 2_000)));

        ResumoFolha resumo = service.gerarResumo();

        assertThrows(UnsupportedOperationException.class,
                () -> resumo.totaisPorCategoria().put("Outro", BigDecimal.TEN));
    }

    @Test
    @DisplayName("CT-009 - Deve impedir matrícula duplicada e propagar erro do repositório")
    void cadastrarPropagaDuplicidade() throws IOException {
        Colaborador colaborador = new ColaboradorPadrao("001", "Ana", 2_000);
        doThrow(new IllegalArgumentException("Já existe colaborador com essa matrícula."))
                .when(repositorio).cadastrar(colaborador);

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(colaborador));
        verify(repositorio).cadastrar(colaborador);
    }

    @Test
    @DisplayName("CT-023 - Deve delegar busca e listagem ao repositório")
    void buscaEListaColaboradores() {
        Colaborador colaborador = new ColaboradorPadrao("001", "Ana", 2_000);
        when(repositorio.buscar("001")).thenReturn(Optional.of(colaborador));
        when(repositorio.buscar("999")).thenReturn(Optional.empty());
        when(repositorio.listar()).thenReturn(List.of(colaborador));

        assertEquals(Optional.of(colaborador), service.buscar("001"));
        assertFalse(service.buscar("999").isPresent());
        assertEquals(List.of(colaborador), service.listar());
        verify(repositorio).listar();
    }

    @Test
    @DisplayName("Deve delegar atualização e exclusão")
    void atualizaEExcluiColaboradores() throws IOException {
        Colaborador colaborador = new ColaboradorPadrao("001", "Ana", 2_000);

        service.atualizar(colaborador);
        service.excluir("001");

        verify(repositorio).atualizar(colaborador);
        verify(repositorio).excluir("001");
    }

    @Test
    @DisplayName("Deve delegar processamento, consulta e resumo de competência")
    void delegaOperacoesDaFolhaProcessada() {
        FolhaProcessada item = new FolhaProcessada(1L, "001", "Ana", "PADRAO",
                3, 2099, new BigDecimal("2000.00"), BigDecimal.ZERO.setScale(2),
                new BigDecimal("2000.00"), java.time.OffsetDateTime.now());
        when(repositorio.processarFolha(3, 2099)).thenReturn(List.of(item));
        when(repositorio.consultarFolha(3, 2099)).thenReturn(List.of(item));
        when(repositorio.buscarFolha("001", 3, 2099)).thenReturn(Optional.of(item));

        assertEquals(List.of(item), service.processarFolha(3, 2099));
        assertEquals(List.of(item), service.consultarFolha(3, 2099));
        assertEquals(Optional.of(item), service.buscarFolha("001", 3, 2099));
        assertEquals(new BigDecimal("2000.00"), service.gerarResumo(3, 2099).totalFolha());
        verify(repositorio, times(2)).consultarFolha(3, 2099);
    }
}
