package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ColaboradorTest {

    @Test
    void colaboradorPadraoRecebeSalarioBaseSemAdicional() {
        Colaborador colaborador = new ColaboradorPadrao("  001  ", "  Ana  ", 2_000);

        assertEquals("001", colaborador.getMatricula());
        assertEquals("Ana", colaborador.getNome());
        assertEquals(0, colaborador.getAdicional());
        assertEquals(2_000, colaborador.getSalarioFinal());
    }

    @Test
    void colaboradorComissionadoCalculaAdicionalSobreVendas() {
        Colaborador colaborador = new ColaboradorComissionado("002", "Bruno", 2_000, 10_000, 5);

        assertEquals(500, colaborador.getAdicional());
        assertEquals(2_500, colaborador.getSalarioFinal());
    }

    @Test
    void colaboradorProducaoCalculaAdicionalPelaQuantidadeProduzida() {
        Colaborador colaborador = new ColaboradorProducao("003", "Carla", 1_800, 120, 2.5);

        assertEquals(300, colaborador.getAdicional());
        assertEquals(2_100, colaborador.getSalarioFinal());
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.0, Double.NaN, Double.POSITIVE_INFINITY })
    void rejeitaSalarioBaseNegativoOuNaoFinito(double salarioBase) {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorPadrao("004", "Diego", salarioBase));
    }

    @Test
    void rejeitaMatriculaOuNomeEmBranco() {
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao(" ", "Ana", 1_000));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao("001", " ", 1_000));
    }

    @Test
    void rejeitaValoresAdicionaisNegativos() {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorComissionado("005", "Eva", 1_000, -1, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorProducao("006", "Fábio", 1_000, 10, -1));
    }
}
