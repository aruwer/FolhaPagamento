package folhapagamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface ColaboradorJpaRepository extends JpaRepository<ColaboradorJpaEntity, Long> {
    List<ColaboradorJpaEntity> findAllByAtivoTrueOrderByMatricula();

    Optional<ColaboradorJpaEntity> findByMatriculaAndAtivoTrue(String matricula);
}
