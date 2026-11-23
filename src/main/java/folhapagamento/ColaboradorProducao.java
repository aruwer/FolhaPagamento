package folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ColaboradorProducao extends Colaborador {
    private final BigDecimal quantidadeProduzida;
    private final BigDecimal valorPorUnidade;

    public ColaboradorProducao(String matricula, String nome, double salarioBase,
            double quantidadeProduzida, double valorPorUnidade) {
        this(matricula, nome, BigDecimal.valueOf(salarioBase),
                BigDecimal.valueOf(quantidadeProduzida), BigDecimal.valueOf(valorPorUnidade));
    }

    public ColaboradorProducao(String matricula, String nome, BigDecimal salarioBase,
            BigDecimal quantidadeProduzida, BigDecimal valorPorUnidade) {
        super(matricula, nome, salarioBase);
        quantidadeProduzida = valorOuZero(quantidadeProduzida);
        valorPorUnidade = valorOuZero(valorPorUnidade);
        validarNaoNegativo(quantidadeProduzida, "A quantidade produzida");
        validarNaoNegativo(valorPorUnidade, "O valor por unidade");
        BigDecimal quantidadeNormalizada = quantidadeProduzida.stripTrailingZeros();
        this.quantidadeProduzida = quantidadeNormalizada.scale() < 0
                ? quantidadeNormalizada.setScale(0)
                : quantidadeNormalizada;
        this.valorPorUnidade = valorMonetario(valorPorUnidade);
    }

    @Override
    public String getTipo() {
        return "Por produção";
    }

    @Override
    public String getCodigoPersistencia() {
        return "PRODUCAO";
    }

    @Override
    public BigDecimal getAdicional() {
        return quantidadeProduzida.multiply(valorPorUnidade).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public BigDecimal getValorPorUnidade() {
        return valorPorUnidade;
    }

    @Override
    public String getDetalhesRemuneracao() {
        return "Produção: " + quantidadeProduzida.toPlainString() + " unidades | Valor/unidade: "
                + valorPorUnidade.toPlainString();
    }
}
