package folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class Colaborador {
    private final String matricula;
    private final String nome;
    private final BigDecimal salarioBase;

    protected Colaborador(String matricula, String nome, double salarioBase) {
        this(matricula, nome, BigDecimal.valueOf(salarioBase));
    }

    protected Colaborador(String matricula, String nome, BigDecimal salarioBase) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("A matrícula é obrigatória.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        validarNaoNegativo(salarioBase, "O salário base");
        this.matricula = matricula.trim();
        this.nome = nome.trim();
        this.salarioBase = valorMonetario(salarioBase);
    }

    protected static void validarNaoNegativo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
    }

    protected static void validarNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
    }

    protected static BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    protected static BigDecimal valorMonetario(BigDecimal valor) {
        Objects.requireNonNull(valor);
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Valores monetários devem ter no máximo duas casas decimais.");
        }
        return valor.setScale(2, RoundingMode.UNNECESSARY);
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public abstract String getTipo();

    public abstract String getCodigoPersistencia();

    public abstract BigDecimal getAdicional();

    public abstract String getDetalhesRemuneracao();

    public final BigDecimal getSalarioFinal() {
        return salarioBase.add(getAdicional()).setScale(2, RoundingMode.HALF_UP);
    }
}
