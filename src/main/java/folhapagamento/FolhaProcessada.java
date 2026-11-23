package folhapagamento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FolhaProcessada(
        Long id,
        String matricula,
        String nome,
        String tipo,
        int mes,
        int ano,
        BigDecimal salarioBase,
        BigDecimal adicional,
        BigDecimal salarioFinal,
        OffsetDateTime processadaEm) {

    public String tipoDescricao() {
        return switch (tipo) {
            case "PADRAO" -> "Padrão";
            case "COMISSIONADO" -> "Comissionado";
            case "PRODUCAO" -> "Por produção";
            default -> tipo;
        };
    }

    public String codigoTipo() {
        return tipo.toLowerCase(java.util.Locale.ROOT);
    }
}
