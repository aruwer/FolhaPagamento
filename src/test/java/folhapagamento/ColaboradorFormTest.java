package folhapagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ColaboradorFormTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Deve inicializar tipo padrão e permitir leitura/escrita de todos os campos")
    void valoresPadraoEGettersSetters() {
        ColaboradorForm form = new ColaboradorForm();

        assertEquals("PADRAO", form.getTipo());
        assertNull(form.getMatricula());
        assertNull(form.getNome());
        assertNull(form.getSalarioBase());
        assertNull(form.getValorVendas());
        assertNull(form.getPercentualComissao());
        assertNull(form.getQuantidadeProduzida());
        assertNull(form.getValorPorUnidade());

        form.setMatricula("001");
        form.setNome("Ana");
        form.setTipo("COMISSIONADO");
        form.setSalarioBase(1_000.0);
        form.setValorVendas(2_000.0);
        form.setPercentualComissao(10.0);
        form.setQuantidadeProduzida(3.0);
        form.setValorPorUnidade(4.0);

        assertEquals("001", form.getMatricula());
        assertEquals("Ana", form.getNome());
        assertEquals("COMISSIONADO", form.getTipo());
        assertEquals(1_000.0, form.getSalarioBase());
        assertEquals(2_000.0, form.getValorVendas());
        assertEquals(10.0, form.getPercentualComissao());
        assertEquals(3.0, form.getQuantidadeProduzida());
        assertEquals(4.0, form.getValorPorUnidade());
    }

    @Nested
    @DisplayName("Validação Bean Validation")
    class ValidacaoBeanValidation {
        @Test
        @DisplayName("CT-001 - Deve validar e construir formulário padrão válido")
        void formularioPadraoValido() {
            ColaboradorForm form = formularioValido();

            assertTrue(validator.validate(form).isEmpty());
            assertEquals("Padrão", form.construirColaborador().getTipo());
        }
    }

    @Test
    @DisplayName("CT-010, CT-011, CT-012, CT-013 - Deve rejeitar dados cadastrais inválidos")
    void validaCamposCadastrais() {
        ColaboradorForm form = formularioValido();
        form.setMatricula("A-1");
        form.setNome(" ");
        form.setSalarioBase(null);
        form.setTipo("DESCONHECIDO");

        Set<String> propriedades = propriedadesComViolacao(form);

        assertTrue(propriedades.containsAll(Set.of("matricula", "nome", "salarioBase", "tipo")));
    }

    @Test
    @DisplayName("CT-014, CT-017, CT-018, CT-021 e CT-022 - Deve rejeitar valores negativos")
    void validaValoresVariaveisNegativos() {
        ColaboradorForm form = formularioValido();
        form.setSalarioBase(-1.0);
        form.setValorVendas(-1.0);
        form.setPercentualComissao(-1.0);
        form.setQuantidadeProduzida(-1.0);
        form.setValorPorUnidade(-1.0);

        Set<String> propriedades = propriedadesComViolacao(form);

        assertTrue(propriedades.containsAll(Set.of("salarioBase", "valorVendas", "percentualComissao",
                "quantidadeProduzida", "valorPorUnidade")));
    }

    @Test
    @DisplayName("RN006 - Deve rejeitar comissão acima de 100% pela validação do formulário")
    void validaComissaoAcimaDoLimite() {
        ColaboradorForm form = formularioValido();
        form.setTipo("COMISSIONADO");
        form.setValorVendas(100.0);
        form.setPercentualComissao(100.01);

        assertTrue(propriedadesComViolacao(form).contains("percentualComissao"));
    }

    @Test
    @DisplayName("CT-003 - Deve construir colaborador comissionado")
    void construiComissionado() {
        ColaboradorForm form = formularioValido();
        form.setTipo("COMISSIONADO");
        form.setValorVendas(10_000.0);
        form.setPercentualComissao(5.0);

        assertEquals(1_500.0, form.construirColaborador().getSalarioFinal());
    }

    @Test
    @DisplayName("CT-006 - Deve construir colaborador de produção")
    void construiProducao() {
        ColaboradorForm form = formularioValido();
        form.setTipo("PRODUCAO");
        form.setQuantidadeProduzida(100.0);
        form.setValorPorUnidade(2.0);

        assertEquals(1_200.0, form.construirColaborador().getSalarioFinal());
    }

    @Test
    @DisplayName("CT-015 - Deve exigir vendas e comissão ao construir formulário comissionado")
    void exigeDadosDeComissionadoNaConstrucao() {
        ColaboradorForm form = formularioValido();
        form.setTipo("COMISSIONADO");

        assertFalse(validator.validate(form).stream()
                .anyMatch(violacao -> Set.of("valorVendas", "percentualComissao")
                        .contains(violacao.getPropertyPath().toString())));
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, form::construirColaborador);
        assertEquals("Informe o valor das vendas.", erro.getMessage());
    }

    @Test
    @DisplayName("CT-019 e CT-020 - Deve exigir quantidade e valor unitário na construção")
    void exigeDadosDeProducaoNaConstrucao() {
        ColaboradorForm form = formularioValido();
        form.setTipo("PRODUCAO");

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, form::construirColaborador);
        assertEquals("Informe a quantidade produzida.", erro.getMessage());
    }

    @Test
    @DisplayName("Deve rejeitar formulário sem salário base ou com tipo desconhecido ao construir")
    void rejeitaSalarioAusenteETipoDesconhecidoNaConstrucao() {
        ColaboradorForm form = formularioValido();
        form.setSalarioBase(null);
        assertThrows(IllegalArgumentException.class, form::construirColaborador);

        form.setSalarioBase(1_000.0);
        form.setTipo("DESCONHECIDO");
        assertThrows(IllegalArgumentException.class, form::construirColaborador);
    }

    @Test
    @DisplayName("Deve converter cada subtipo para seus dados de formulário")
    void converteSubtiposParaForm() {
        ColaboradorForm comissionado = ColaboradorForm.de(
                new ColaboradorComissionado("002", "Bia", 1_000, 500, 10));
        ColaboradorForm producao = ColaboradorForm.de(
                new ColaboradorProducao("003", "Caio", 1_000, 20, 3));
        ColaboradorForm padrao = ColaboradorForm.de(new ColaboradorPadrao("004", "Dani", 1_000));

        assertEquals("COMISSIONADO", comissionado.getTipo());
        assertEquals(500.0, comissionado.getValorVendas());
        assertEquals(10.0, comissionado.getPercentualComissao());
        assertEquals("PRODUCAO", producao.getTipo());
        assertEquals(20.0, producao.getQuantidadeProduzida());
        assertEquals(3.0, producao.getValorPorUnidade());
        assertEquals("PADRAO", padrao.getTipo());
    }

    private ColaboradorForm formularioValido() {
        ColaboradorForm form = new ColaboradorForm();
        form.setMatricula("001");
        form.setNome("Ana");
        form.setSalarioBase(1_000.0);
        return form;
    }

    private Set<String> propriedadesComViolacao(ColaboradorForm form) {
        return validator.validate(form).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
