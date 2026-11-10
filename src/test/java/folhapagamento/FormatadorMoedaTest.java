package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FormatadorMoedaTest {

    @Test
    void formataValorComMoedaBrasileira() {
        String valorFormatado = new FormatadorMoeda().formatar(1_234.56);

        assertTrue(valorFormatado.startsWith("R$"));
        assertTrue(valorFormatado.contains("1.234,56"));
    }
}
