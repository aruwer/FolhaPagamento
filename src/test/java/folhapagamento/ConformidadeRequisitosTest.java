package folhapagamento;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ConformidadeRequisitosTest {

    @Test
    @DisplayName("RF002–RF004, RN008: fórmulas de cálculo por categoria")
    void deveValidarFormulasDeCalculoRN001aRN003() {
        Colaborador padrao = new ColaboradorPadrao("101", "Ana Silva", 3000.00);
        assertEquals(new BigDecimal("0.00"), padrao.getAdicional());
        assertEquals(new BigDecimal("3000.00"), padrao.getSalarioFinal());
        assertEquals("Padrão", padrao.getTipo());

        Colaborador comissionado = new ColaboradorComissionado("102", "Bruno Costa", 2500.00, 10000.00, 10.0);
        assertEquals(new BigDecimal("1000.00"), comissionado.getAdicional());
        assertEquals(new BigDecimal("3500.00"), comissionado.getSalarioFinal());
        assertEquals("Comissionado", comissionado.getTipo());

        Colaborador producao = new ColaboradorProducao("103", "Carla Dias", 2200.00, 150.0, 5.00);
        assertEquals(new BigDecimal("750.00"), producao.getAdicional());
        assertEquals(new BigDecimal("2950.00"), producao.getSalarioFinal());
        assertEquals("Por produção", producao.getTipo());
    }

    @Test
    @DisplayName("RNF002, RN002–RN007: dados vazios e negativos bloqueados")
    void deveBloquearDadosVaziosNegativosOuAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao("", "Nome", 2000.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao("001", "   ", 2000.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorPadrao("001", "Nome", -1.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorComissionado("002", "Nome", 2000.0, -10.0, 5.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorComissionado("002", "Nome", 2000.0, 1000.0, -5.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorComissionado("002", "Nome", 2000.0, 1000.0, 120.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorProducao("003", "Nome", 2000.0, -1.0, 5.0));
        assertThrows(IllegalArgumentException.class, () -> new ColaboradorProducao("003", "Nome", 2000.0, 10.0, -2.0));
    }

    @Test
    @DisplayName("RF001, RNF004: conversão do formulário para o domínio")
    void deveConstruirEConverterColaboradorFormCorretamente() {
        ColaboradorForm form = new ColaboradorForm();
        form.setMatricula("201");
        form.setNome("Carlos");
        form.setTipo("COMISSIONADO");
        form.setSalarioBase(new BigDecimal("3000.00"));
        form.setValorVendas(new BigDecimal("5000.00"));
        form.setPercentualComissao(new BigDecimal("10.00"));

        Colaborador criado = form.construirColaborador();
        assertEquals(new BigDecimal("3500.00"), criado.getSalarioFinal());

        ColaboradorForm convertido = ColaboradorForm.de(criado);
        assertEquals("COMISSIONADO", convertido.getTipo());
        assertEquals(new BigDecimal("5000.00"), convertido.getValorVendas());
    }
}