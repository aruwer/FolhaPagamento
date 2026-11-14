package folhapagamento;

public final class ColaboradorComissionado extends Colaborador {
    private final double valorVendas;
    private final double percentualComissao;

    public ColaboradorComissionado(String matricula, String nome, double salarioBase,
            double valorVendas, double percentualComissao) {
        super(matricula, nome, salarioBase);
        validarNaoNegativo(valorVendas, "O valor das vendas");
        validarNaoNegativo(percentualComissao, "O percentual de comissão");
        if (percentualComissao > 100.0) {
            throw new IllegalArgumentException("O percentual de comissão não pode ser maior que 100%.");
        }
        this.valorVendas = valorVendas;
        this.percentualComissao = percentualComissao;
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
    public double getAdicional() {
        return valorVendas * percentualComissao / 100;
    }

    public double getValorVendas() {
        return valorVendas;
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    @Override
    public String getDetalhesRemuneracao() {
        return "Vendas: " + valorVendas + " | Comissão: " + percentualComissao + "%";
    }

    @Override
    public String[] getDadosEspecificos() {
        return new String[] { Double.toString(valorVendas), Double.toString(percentualComissao) };
    }
}