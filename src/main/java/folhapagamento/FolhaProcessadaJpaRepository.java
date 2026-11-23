package folhapagamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface FolhaProcessadaJpaRepository extends JpaRepository<FolhaProcessadaJpaEntity, Long> {
    List<FolhaProcessadaJpaEntity> findAllByAnoAndMesOrderByMatriculaSnapshot(int ano, int mes);

    Optional<FolhaProcessadaJpaEntity> findByMatriculaSnapshotAndAnoAndMes(
            String matriculaSnapshot, int ano, int mes);

    boolean existsByColaborador_IdAndAnoAndMes(Long colaboradorId, int ano, int mes);
}
