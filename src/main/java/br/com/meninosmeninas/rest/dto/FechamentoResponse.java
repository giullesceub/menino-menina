package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.Caixa;
import java.math.BigDecimal;

public record FechamentoResponse(Caixa caixa, BigDecimal diferenca, String situacao) {
    public static FechamentoResponse from(Caixa caixa) {
        BigDecimal diferenca = caixa.getValorFechamentoInformado()
                .subtract(caixa.getValorFechamentoEsperado());
        String situacao = diferenca.signum() == 0 ? "VALOR CONFERIDO"
                : diferenca.signum() > 0 ? "SOBRA DE CAIXA" : "FALTA DE CAIXA";
        return new FechamentoResponse(caixa, diferenca, situacao);
    }
}
