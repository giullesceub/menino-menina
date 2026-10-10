package br.com.meninosmeninas.rest.dto;

import java.math.BigDecimal;

public record CriarProdutoRequest(
        String nome,
        Integer categoriaId,
        Integer quantidade,
        BigDecimal preco) {
}
