package folhapagamento;

import java.math.BigDecimal;

public final class ColaboradorPadrao extends Colaborador {
    public ColaboradorPadrao(String matricula, String nome, double salarioBase) {
        super(matricula, nome, salarioBase);
    }

    public ColaboradorPadrao(String matricula, String nome, BigDecimal salarioBase) {
        super(matricula, nome, salarioBase);
    }

    @Override
    public String getTipo() {
        return "Padrão";
    }

    @Override
    public String getCodigoPersistencia() {
        return "PADRAO";
    }

    @Override
    public BigDecimal getAdicional() {
        return BigDecimal.ZERO.setScale(2);
    }

    @Override
    public String getDetalhesRemuneracao() {
        return "Sem remuneração adicional";
    }
}
