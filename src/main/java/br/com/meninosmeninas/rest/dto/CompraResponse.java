package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.Compra;
import br.com.meninosmeninas.persistence.models.ItemCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Long id,
        Long fornecedorId,
        String fornecedorNome,
        LocalDateTime dataCompra,
        BigDecimal valorTotal,
        List<Item> itens) {

    public record Item(
            Long id,
            Long produtoId,
            String produtoNome,
            int quantidade,
            BigDecimal custoUnitario,
            BigDecimal subtotal) {
    }

    public static CompraResponse from(Compra compra, List<ItemCompra> itens) {
        List<Item> itensResponse = itens.stream()
                .map(item -> new Item(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getCustoUnitario(),
                        item.getSubtotal()))
                .toList();

        return new CompraResponse(
                compra.getId(),
                compra.getFornecedor().getId(),
                compra.getFornecedor().getRazaoSocial(),
                compra.getDataCompra(),
                compra.getValorTotal(),
                itensResponse);
    }
}
