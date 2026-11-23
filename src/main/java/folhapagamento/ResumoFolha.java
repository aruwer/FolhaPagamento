package folhapagamento;

import java.math.BigDecimal;
import java.util.Map;

public record ResumoFolha(int quantidadeColaboradores, BigDecimal totalFolha,
        Map<String, BigDecimal> totaisPorCategoria, Map<String, Integer> quantidadePorCategoria) {

    public ResumoFolha(int quantidadeColaboradores, BigDecimal totalFolha,
            Map<String, BigDecimal> totaisPorCategoria) {
        this(quantidadeColaboradores, totalFolha, totaisPorCategoria, Map.of());
    }
}
