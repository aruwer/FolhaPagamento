package folhapagamento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "colaboradores")
public class ColaboradorJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String matricula;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_colaborador", nullable = false, length = 20)
    private TipoColaborador tipoColaborador;

    @Column(name = "salario_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "valor_vendas", precision = 12, scale = 2)
    private BigDecimal valorVendas;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "quantidade_produzida", precision = 13, scale = 3)
    private BigDecimal quantidadeProduzida;

    @Column(name = "valor_por_unidade", precision = 12, scale = 2)
    private BigDecimal valorPorUnidade;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected ColaboradorJpaEntity() {
    }

    static ColaboradorJpaEntity de(Colaborador colaborador) {
        ColaboradorJpaEntity entity = new ColaboradorJpaEntity();
        entity.matricula = colaborador.getMatricula();
        entity.nome = colaborador.getNome();
        entity.tipoColaborador = TipoColaborador.valueOf(colaborador.getCodigoPersistencia());
        entity.salarioBase = colaborador.getSalarioBase();
        entity.ativo = true;
        if (colaborador instanceof ColaboradorComissionado comissionado) {
            entity.valorVendas = comissionado.getValorVendas();
            entity.percentualComissao = comissionado.getPercentualComissao();
        } else if (colaborador instanceof ColaboradorProducao producao) {
            entity.quantidadeProduzida = producao.getQuantidadeProduzida();
            entity.valorPorUnidade = producao.getValorPorUnidade();
        }
        return entity;
    }

    Colaborador paraDominio() {
        return switch (tipoColaborador) {
            case PADRAO -> new ColaboradorPadrao(matricula, nome, salarioBase);
            case COMISSIONADO -> new ColaboradorComissionado(
                    matricula, nome, salarioBase, valorVendas, percentualComissao);
            case PRODUCAO -> new ColaboradorProducao(
                    matricula, nome, salarioBase, quantidadeProduzida, valorPorUnidade);
        };
    }

    void atualizarCom(Colaborador colaborador) {
        ColaboradorJpaEntity atualizado = de(colaborador);
        nome = atualizado.nome;
        tipoColaborador = atualizado.tipoColaborador;
        salarioBase = atualizado.salarioBase;
        valorVendas = atualizado.valorVendas;
        percentualComissao = atualizado.percentualComissao;
        quantidadeProduzida = atualizado.quantidadeProduzida;
        valorPorUnidade = atualizado.valorPorUnidade;
    }

    void excluirLogicamente() {
        ativo = false;
        excluidoEm = OffsetDateTime.now();
    }

    @PreUpdate
    void atualizarDataModificacao() {
        atualizadoEm = OffsetDateTime.now();
    }

    @PrePersist
    void inicializarDatas() {
        OffsetDateTime agora = OffsetDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    Long getId() { return id; }
    String getMatricula() { return matricula; }
    String getNome() { return nome; }
    TipoColaborador getTipoColaborador() { return tipoColaborador; }
    BigDecimal getSalarioBase() { return salarioBase; }
    OffsetDateTime getCriadoEm() { return criadoEm; }
    OffsetDateTime getExcluidoEm() { return excluidoEm; }

    enum TipoColaborador {
        PADRAO,
        COMISSIONADO,
        PRODUCAO
    }
}
