package folhapagamento;

import java.text.NumberFormat;
import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class FormatadorMoeda {
    public String formatar(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    public String formatar(Double valor) {
        return formatar(valor == null ? 0.0 : valor.doubleValue());
    }

    public String formatar(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
                .format(valor == null ? BigDecimal.ZERO : valor);
    }
}