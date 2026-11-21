package br.com.meninosmeninas.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record CompraRequest(Long fornecedorId, List<Item> itens, String login, String senha) {
    public record Item(Long produtoId, int quantidade, BigDecimal custoUnitario) {
    }
}
