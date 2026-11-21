package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.PagamentoVenda;

import java.math.BigDecimal;
import java.util.List;

public record VendaRequest(Long clienteId, List<Item> itens, List<Pagamento> pagamentos,
                           String login, String senha) {
    public record Item(Long produtoId, int quantidade) {
    }

    public record Pagamento(PagamentoVenda.FormaPagamento formaPagamento, BigDecimal valor) {
    }
}
