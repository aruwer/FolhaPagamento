package folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ColaboradorComissionado extends Colaborador {
    private final BigDecimal valorVendas;
    private final BigDecimal percentualComissao;

    public ColaboradorComissionado(String matricula, String nome, double salarioBase,
            double valorVendas, double percentualComissao) {
        this(matricula, nome, BigDecimal.valueOf(salarioBase),
                BigDecimal.valueOf(valorVendas), BigDecimal.valueOf(percentualComissao));
    }

    public ColaboradorComissionado(String matricula, String nome, BigDecimal salarioBase,
            BigDecimal valorVendas, BigDecimal percentualComissao) {
        super(matricula, nome, salarioBase);
        valorVendas = valorOuZero(valorVendas);
        percentualComissao = valorOuZero(percentualComissao);
        validarNaoNegativo(valorVendas, "O valor das vendas");
        validarNaoNegativo(percentualComissao, "O percentual de comissão");
        this.valorVendas = valorMonetario(valorVendas);
        this.percentualComissao = valorMonetario(percentualComissao);
    }

    @Override
    public String getTipo() {
        return "Comissionado";
    }

    @Override
    public String getCodigoPersistencia() {
        return "COMISSIONADO";
    }

    @Override
    public BigDecimal getAdicional() {
        return valorVendas.multiply(percentualComissao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorVendas() {
        return valorVendas;
    }

    public BigDecimal getPercentualComissao() {
        return percentualComissao;
    }

    @Override
    public String getDetalhesRemuneracao() {
        return "Vendas: " + valorVendas.toPlainString() + " | Comissão: "
                + percentualComissao.toPlainString() + "%";
    }
}
