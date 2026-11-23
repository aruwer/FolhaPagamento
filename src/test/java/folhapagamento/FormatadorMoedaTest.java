package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormatadorMoedaTest {

    @Test
    @DisplayName("RN008 - Deve formatar valor positivo no padrão brasileiro")
    void formataValorComMoedaBrasileira() {
        String valorFormatado = new FormatadorMoeda().formatar(1_234.56);

        assertTrue(valorFormatado.startsWith("R$"));
        assertTrue(valorFormatado.contains("1.234,56"));
    }

    @Test
    @DisplayName("RN008 - Deve formatar zero, valor negativo e arredondar casas decimais")
    void formataZeroNegativoEArredonda() {
        FormatadorMoeda formatador = new FormatadorMoeda();

        assertTrue(formatador.formatar(0).contains("0,00"));
        assertTrue(formatador.formatar(-12.345).startsWith("-"));
        assertTrue(formatador.formatar(-12.345).contains("12,35"));
        assertTrue(formatador.formatar(12.345).contains("12,35"));
    }

    @Test
    @DisplayName("RN008 - Deve agrupar valores altos em milhares e milhões")
    void formataValoresAltos() {
        String valor = new FormatadorMoeda().formatar(1_234_567.89);

        assertTrue(valor.contains("1.234.567,89"));
    }

    @Test
    @DisplayName("RN010 - Deve apresentar valor variável nulo como R$ 0,00")
    void formataValorNuloComoZero() {
        assertEquals("R$ 0,00", new FormatadorMoeda().formatar((Double) null));
    }
}
