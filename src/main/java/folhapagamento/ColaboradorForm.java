package folhapagamento;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ColaboradorForm {
    @NotBlank(message = "Informe a matrícula.")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "A matrícula deve conter apenas letras sem acento e números.")
    @Size(max = 40, message = "A matrícula deve ter no máximo 40 caracteres.")
    private String matricula;

    @NotBlank(message = "Informe o nome.")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
    private String nome;

    @NotNull(message = "Informe o salário base.")
    @DecimalMin(value = "0.0", message = "O salário base não pode ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "Informe no máximo duas casas decimais.")
    private BigDecimal salarioBase;

    @NotBlank(message = "Selecione o tipo de colaborador.")
    @Pattern(regexp = "PADRAO|COMISSIONADO|PRODUCAO", message = "Selecione um tipo válido.")
    private String tipo = "PADRAO";

    @DecimalMin(value = "0.0", message = "O valor de vendas não pode ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "Informe no máximo duas casas decimais.")
    private BigDecimal valorVendas;

    @DecimalMin(value = "0.0", message = "A comissão não pode ser negativa.")
    @DecimalMax(value = "100.0", message = "A comissão não pode ser maior que 100%.")
    @Digits(integer = 3, fraction = 2, message = "Informe um percentual válido com até duas casas decimais.")
    private BigDecimal percentualComissao;

    @DecimalMin(value = "0.0", message = "A quantidade produzida não pode ser negativa.")
    @Digits(integer = 10, fraction = 3, message = "Informe até três casas decimais.")
    private BigDecimal quantidadeProduzida;

    @DecimalMin(value = "0.0", message = "O valor por unidade não pode ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "Informe no máximo duas casas decimais.")
    private BigDecimal valorPorUnidade;

    public Colaborador construirColaborador() {
        if (salarioBase == null) {
            throw new IllegalArgumentException("Informe o salário base.");
        }
        return switch (tipo) {
            case "PADRAO" -> new ColaboradorPadrao(matricula, nome, salarioBase);
            case "COMISSIONADO" -> new ColaboradorComissionado(matricula, nome, salarioBase,
                    valorVendas, percentualComissao);
            case "PRODUCAO" -> new ColaboradorProducao(matricula, nome, salarioBase,
                    quantidadeProduzida, valorPorUnidade);
            default -> throw new IllegalArgumentException("Selecione um tipo de colaborador válido.");
        };
    }

    public static ColaboradorForm de(Colaborador colaborador) {
        ColaboradorForm form = new ColaboradorForm();
        form.setMatricula(colaborador.getMatricula());
        form.setNome(colaborador.getNome());
        form.setSalarioBase(colaborador.getSalarioBase());
        if (colaborador instanceof ColaboradorComissionado comissionado) {
            form.setTipo("COMISSIONADO");
            form.setValorVendas(comissionado.getValorVendas());
            form.setPercentualComissao(comissionado.getPercentualComissao());
        } else if (colaborador instanceof ColaboradorProducao producao) {
            form.setTipo("PRODUCAO");
            form.setQuantidadeProduzida(producao.getQuantidadeProduzida());
            form.setValorPorUnidade(producao.getValorPorUnidade());
        } else {
            form.setTipo("PADRAO");
        }
        return form;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getSalarioBase() { return salarioBase; }
    public void setSalarioBase(BigDecimal salarioBase) { this.salarioBase = salarioBase; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getValorVendas() { return valorVendas; }
    public void setValorVendas(BigDecimal valorVendas) { this.valorVendas = valorVendas; }
    public BigDecimal getPercentualComissao() { return percentualComissao; }
    public void setPercentualComissao(BigDecimal percentualComissao) { this.percentualComissao = percentualComissao; }
    public BigDecimal getQuantidadeProduzida() { return quantidadeProduzida; }
    public void setQuantidadeProduzida(BigDecimal quantidadeProduzida) { this.quantidadeProduzida = quantidadeProduzida; }
    public BigDecimal getValorPorUnidade() { return valorPorUnidade; }
    public void setValorPorUnidade(BigDecimal valorPorUnidade) { this.valorPorUnidade = valorPorUnidade; }
}
