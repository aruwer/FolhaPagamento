package folhapagamento;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConformidadeRequisitosTest {

    @Test
    void deveValidarFormulasDeCalculoRN001aRN003() {
        Colaborador padrao = new ColaboradorPadrao("101", "Ana Silva", 3000.00);
        assertEquals(0.0, padrao.getAdicional(), 0.001);
        assertEquals(3000.00, padrao.getSalarioFinal(), 0.001);
        assertEquals("Padrão", padrao.getTipo());

        Colaborador comissionado = new ColaboradorComissionado("102", "Bruno Costa", 2500.00, 10000.00, 10.0);
        assertEquals(1000.00, comissionado.getAdicional(), 0.001);
        assertEquals(3500.00, comissionado.getSalarioFinal(), 0.001);
        assertEquals("Comissionado", comissionado.getTipo());

        Colaborador producao = new ColaboradorProducao("103", "Carla Dias", 2200.00, 150.0, 5.00);
        assertEquals(750.00, producao.getAdicional(), 0.001);
        assertEquals(2950.00, producao.getSalarioFinal(), 0.001);
        assertEquals("Por produção", producao.getTipo());
    }

    @Test
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
    void deveConstruirEConverterColaboradorFormCorretamente() {
        ColaboradorForm form = new ColaboradorForm();
        form.setMatricula("201");
        form.setNome("Carlos");
        form.setTipo("COMISSIONADO");
        form.setSalarioBase(3000.0);
        form.setValorVendas(5000.0);
        form.setPercentualComissao(10.0);

        Colaborador criado = form.construirColaborador();
        assertEquals(3500.0, criado.getSalarioFinal(), 0.001);

        ColaboradorForm convertido = ColaboradorForm.de(criado);
        assertEquals("COMISSIONADO", convertido.getTipo());
        assertEquals(5000.0, convertido.getValorVendas());
    }
}