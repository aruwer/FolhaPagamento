package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ColaboradorTest {

    @Test
    @DisplayName("CT-001 - Deve calcular salário padrão e higienizar dados cadastrais")
    void colaboradorPadraoRecebeSalarioBaseSemAdicional() {
        Colaborador colaborador = new ColaboradorPadrao("  001  ", "  Ana  ", 2_000);

        assertEquals("001", colaborador.getMatricula());
        assertEquals("Ana", colaborador.getNome());
        assertEquals(new BigDecimal("0.00"), colaborador.getAdicional());
        assertEquals(new BigDecimal("2000.00"), colaborador.getSalarioFinal());
        assertEquals("Padrão", colaborador.getTipo());
        assertEquals("PADRAO", colaborador.getCodigoPersistencia());
        assertEquals("Sem remuneração adicional", colaborador.getDetalhesRemuneracao());
    }

    @Test
    @DisplayName("CT-003 - Deve calcular comissão sobre vendas")
    void colaboradorComissionadoCalculaAdicionalSobreVendas() {
        ColaboradorComissionado colaborador = new ColaboradorComissionado("002", "Bruno", 2_000, 10_000, 5);

        assertEquals(new BigDecimal("500.00"), colaborador.getAdicional());
        assertEquals(new BigDecimal("2500.00"), colaborador.getSalarioFinal());
        assertEquals(new BigDecimal("10000.00"), colaborador.getValorVendas());
        assertEquals(new BigDecimal("5.00"), colaborador.getPercentualComissao());
        assertEquals("Comissionado", colaborador.getTipo());
        assertEquals("COMISSIONADO", colaborador.getCodigoPersistencia());
        assertTrue(colaborador.getDetalhesRemuneracao().contains("Vendas: 10000.00"));
    }

    @Test
    @DisplayName("CT-006 - Deve calcular remuneração por produção")
    void colaboradorProducaoCalculaAdicionalPelaQuantidadeProduzida() {
        ColaboradorProducao colaborador = new ColaboradorProducao("003", "Carla", 1_800, 120, 2.5);

        assertEquals(new BigDecimal("300.00"), colaborador.getAdicional());
        assertEquals(new BigDecimal("2100.00"), colaborador.getSalarioFinal());
        assertEquals(new BigDecimal("120"), colaborador.getQuantidadeProduzida());
        assertEquals(new BigDecimal("2.50"), colaborador.getValorPorUnidade());
        assertEquals("Por produção", colaborador.getTipo());
        assertEquals("PRODUCAO", colaborador.getCodigoPersistencia());
        assertTrue(colaborador.getDetalhesRemuneracao().contains("Produção: 120 unidades"));
    }

    @ParameterizedTest
    @DisplayName("CT-014 - Deve rejeitar salário base negativo ou não finito")
    @ValueSource(doubles = { -1.0, Double.NaN, Double.POSITIVE_INFINITY })
    void rejeitaSalarioBaseNegativoOuNaoFinito(double salarioBase) {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorPadrao("004", "Diego", salarioBase));
    }

    @Test
    @DisplayName("CT-011 e CT-012 - Deve rejeitar matrícula e nome em branco")
    void rejeitaMatriculaOuNomeEmBranco() {
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao(" ", "Ana", 1_000));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao("001", " ", 1_000));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("CT-011 - Deve rejeitar matrícula nula ou vazia")
    void rejeitaMatriculaNulaOuVazia(String matricula) {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorPadrao(matricula, "Ana", 1_000));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("CT-012 - Deve rejeitar nome nulo ou vazio")
    void rejeitaNomeNuloOuVazio(String nome) {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorPadrao("001", nome, 1_000));
    }

    @Test
    @DisplayName("CT-017, CT-018, CT-021 e CT-022 - Deve rejeitar adicionais negativos")
    void rejeitaValoresAdicionaisNegativos() {
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorComissionado("005", "Eva", 1_000, -1, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorComissionado("005", "Eva", 1_000, 1, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorProducao("006", "Fábio", 1_000, 10, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorProducao("006", "Fábio", 1_000, -1, 1));
    }

    @Test
    @DisplayName("CT-005 / BUG-007 - Deve aceitar comissão acima de 100% (RN005 só proíbe negativo)")
    void aceitaComissaoAcimaDeCemPorCento() {
        ColaboradorComissionado acima = new ColaboradorComissionado("007", "Gabi", 1_000, 2_000, 150);
        assertEquals(new BigDecimal("3000.00"), acima.getAdicional());
        assertEquals(new BigDecimal("4000.00"), acima.getSalarioFinal());
        assertThrows(IllegalArgumentException.class,
                () -> new ColaboradorComissionado("008", "Hugo", 1_000, 1, -0.01));
    }

    @Test
    @DisplayName("CT-002, CT-004, CT-007 e CT-008 - Deve aceitar limites zero")
    void aceitaSalariosEAdicionaisZero() {
        assertEquals(new BigDecimal("0.00"), new ColaboradorPadrao("009", "Iara", 0).getSalarioFinal());
        assertEquals(new BigDecimal("0.00"),
                new ColaboradorComissionado("010", "João", 0, 0, 0).getSalarioFinal());
        assertEquals(new BigDecimal("0.00"),
                new ColaboradorProducao("011", "Katia", 0, 0, 0).getSalarioFinal());
    }

    @Test
    @DisplayName("RN008: calcula valores decimais e altos com precisão monetária")
    void calculaValoresDecimaisEAltos() {
        ColaboradorComissionado comissionado =
                new ColaboradorComissionado("012", "Lia", 1_000_000.75, 9_876_543.21, 12.5);
        ColaboradorProducao producao =
                new ColaboradorProducao("013", "Mauro", 0.01, 0.1, 0.2);

        assertEquals(new BigDecimal("2234568.65"), comissionado.getSalarioFinal());
        assertEquals(new BigDecimal("0.03"), producao.getSalarioFinal());
    }

    @Test
    @DisplayName("RN001: mantém identidade, hash e representação textual do colaborador")
    void usaSemanticaDeIdentidadeHerdadaDeObject() {
        Colaborador primeira = new ColaboradorPadrao("014", "Nina", 1_000);
        Colaborador igualEmDados = new ColaboradorPadrao("014", "Nina", 1_000);

        assertSame(primeira, primeira);
        assertNotEquals(primeira, igualEmDados);
        assertEquals(primeira.hashCode(), primeira.hashCode());
        assertTrue(primeira.toString().startsWith("folhapagamento.ColaboradorPadrao@"));
    }
}
