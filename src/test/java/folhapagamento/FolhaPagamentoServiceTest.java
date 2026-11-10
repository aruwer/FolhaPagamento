package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoServiceTest {

    @Mock
    private RepositorioColaboradores repositorio;

    @Test
    void gerarResumoCalculaTotalEValoresPorCategoria() {
        when(repositorio.listar()).thenReturn(List.of(
                new ColaboradorPadrao("001", "Ana", 2_000),
                new ColaboradorComissionado("002", "Bruno", 1_500, 10_000, 5),
                new ColaboradorProducao("003", "Carla", 1_000, 100, 2)));

        ResumoFolha resumo = new FolhaPagamentoService(repositorio).gerarResumo();

        assertEquals(3, resumo.quantidadeColaboradores());
        assertEquals(5_200, resumo.totalFolha());
        assertEquals(2_000, resumo.totaisPorCategoria().get("Padrão"));
        assertEquals(2_000, resumo.totaisPorCategoria().get("Comissionado"));
        assertEquals(1_200, resumo.totaisPorCategoria().get("Por produção"));
    }

    @Test
    void totaisPorCategoriaNaoPodeSerAlterado() {
        when(repositorio.listar()).thenReturn(List.of(new ColaboradorPadrao("001", "Ana", 2_000)));

        ResumoFolha resumo = new FolhaPagamentoService(repositorio).gerarResumo();

        assertThrows(UnsupportedOperationException.class,
                () -> resumo.totaisPorCategoria().put("Outro", 100.0));
    }
}
