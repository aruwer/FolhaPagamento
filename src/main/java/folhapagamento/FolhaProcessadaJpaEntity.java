package folhapagamento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "folhas_processadas", uniqueConstraints = @UniqueConstraint(
        name = "uq_folha_colaborador_competencia",
        columnNames = { "colaborador_id", "mes", "ano" }))
public class FolhaProcessadaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private ColaboradorJpaEntity colaborador;

    @Column(name = "matricula_snapshot", nullable = false, length = 40)
    private String matriculaSnapshot;

    @Column(name = "nome_snapshot", nullable = false, length = 120)
    private String nomeSnapshot;

    @Column(name = "tipo_snapshot", nullable = false, length = 20)
    private String tipoSnapshot;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int ano;

    @Column(name = "salario_base_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioBaseSnapshot;

    @Column(name = "adicional_calculado", nullable = false, precision = 12, scale = 2)
    private BigDecimal adicionalCalculado;

    @Column(name = "salario_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioFinal;

    @Column(name = "data_processamento", nullable = false)
    private OffsetDateTime dataProcessamento;

    protected FolhaProcessadaJpaEntity() {
    }

    static FolhaProcessadaJpaEntity criar(ColaboradorJpaEntity colaborador, int mes, int ano) {
        Colaborador dominio = colaborador.paraDominio();
        FolhaProcessadaJpaEntity folha = new FolhaProcessadaJpaEntity();
        folha.colaborador = colaborador;
        folha.matriculaSnapshot = colaborador.getMatricula();
        folha.nomeSnapshot = colaborador.getNome();
        folha.tipoSnapshot = colaborador.getTipoColaborador().name();
        folha.mes = mes;
        folha.ano = ano;
        folha.salarioBaseSnapshot = dominio.getSalarioBase();
        folha.adicionalCalculado = dominio.getAdicional();
        folha.salarioFinal = dominio.getSalarioFinal();
        folha.dataProcessamento = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
        return folha;
    }

    FolhaProcessada paraDominio() {
        return new FolhaProcessada(id, matriculaSnapshot, nomeSnapshot, tipoSnapshot,
                mes, ano, salarioBaseSnapshot, adicionalCalculado, salarioFinal, dataProcessamento);
    }
}
