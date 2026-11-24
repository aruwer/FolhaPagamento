package folhapagamento;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RepositorioColaboradoresJpa implements RepositorioColaboradores {
    private static final ZoneId FUSO_FOLHA = FolhaProcessadaJpaEntity.FUSO_FOLHA;

    private final ColaboradorJpaRepository colaboradores;
    private final FolhaProcessadaJpaRepository folhas;

    public RepositorioColaboradoresJpa(
            ColaboradorJpaRepository colaboradores,
            FolhaProcessadaJpaRepository folhas) {
        this.colaboradores = colaboradores;
        this.folhas = folhas;
    }

    @Override
    @Transactional
    public void cadastrar(Colaborador colaborador) throws IOException {
        if (colaboradores.findByMatriculaAndAtivoTrue(colaborador.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Já existe colaborador com essa matrícula.");
        }
        try {
            colaboradores.save(ColaboradorJpaEntity.de(colaborador));
        } catch (DataIntegrityViolationException erro) {
            throw new IllegalArgumentException("Já existe colaborador com essa matrícula.", erro);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Colaborador> buscar(String matricula) {
        return colaboradores.findByMatriculaAndAtivoTrue(matricula).map(ColaboradorJpaEntity::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Colaborador> listar() {
        return colaboradores.findAllByAtivoTrueOrderByMatricula().stream()
                .map(ColaboradorJpaEntity::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void atualizar(Colaborador colaborador) throws IOException {
        ColaboradorJpaEntity existente = colaboradores.findByMatriculaAndAtivoTrue(colaborador.getMatricula())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
        existente.atualizarCom(colaborador);
    }

    @Override
    @Transactional
    public void excluir(String matricula) throws IOException {
        ColaboradorJpaEntity existente = colaboradores.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
        existente.excluirLogicamente();
    }

    @Override
    @Transactional
    public List<FolhaProcessada> processarFolha(int mes, int ano) {
        validarCompetencia(mes, ano);
        YearMonth competencia = YearMonth.of(ano, mes);
        for (ColaboradorJpaEntity colaborador : colaboradores.findAll()) {
            if (!participaDaCompetencia(colaborador, competencia)) {
                continue;
            }
            if (!folhas.existsByColaborador_IdAndAnoAndMes(colaborador.getId(), ano, mes)) {
                folhas.save(FolhaProcessadaJpaEntity.criar(colaborador, mes, ano));
            }
        }
        return folhas.findAllByAnoAndMesOrderByMatriculaSnapshot(ano, mes).stream()
                .map(FolhaProcessadaJpaEntity::paraDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolhaProcessada> consultarFolha(int mes, int ano) {
        validarCompetencia(mes, ano);
        return folhas.findAllByAnoAndMesOrderByMatriculaSnapshot(ano, mes).stream()
                .map(FolhaProcessadaJpaEntity::paraDominio)
                .toList();
    }

    private static boolean participaDaCompetencia(ColaboradorJpaEntity colaborador, YearMonth competencia) {
        LocalDate inicio = competencia.atDay(1);
        LocalDate fim = competencia.atEndOfMonth();
        LocalDate cadastro = colaborador.getCriadoEm().atZoneSameInstant(FUSO_FOLHA).toLocalDate();
        if (cadastro.isAfter(fim)) {
            return false;
        }
        if (colaborador.getExcluidoEm() == null) {
            return true;
        }
        LocalDate exclusao = colaborador.getExcluidoEm().atZoneSameInstant(FUSO_FOLHA).toLocalDate();
        return !exclusao.isBefore(inicio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FolhaProcessada> buscarFolha(String matricula, int mes, int ano) {
        validarCompetencia(mes, ano);
        return folhas.findByMatriculaSnapshotAndAnoAndMes(matricula, ano, mes)
                .map(FolhaProcessadaJpaEntity::paraDominio);
    }

    private void validarCompetencia(int mes, int ano) {
        if (ano < 2000 || ano > 9999) {
            throw new IllegalArgumentException("O ano da competência deve estar entre 2000 e 9999.");
        }
        try {
            YearMonth.of(ano, mes);
        } catch (java.time.DateTimeException erro) {
            throw new IllegalArgumentException("Informe uma competência válida (mês de 1 a 12 e ano válido).", erro);
        }
    }
}
