package br.com.meninosmeninas.rest.dto;

import java.math.BigDecimal;

public record CaixaRequest(BigDecimal valor, String descricao, String login, String senha) {
}
