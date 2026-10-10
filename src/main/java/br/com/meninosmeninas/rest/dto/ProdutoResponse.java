package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.Produto;

public record ProdutoResponse(
        int id,
        String nome,
        int categoriaId,
        String categoriaNome,
        int quantidade,
        Double preco) {

    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getQuantidade(),
                produto.getPreco());
    }
}
