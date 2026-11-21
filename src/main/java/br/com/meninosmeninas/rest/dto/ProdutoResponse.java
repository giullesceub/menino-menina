package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.Produto;
import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;

public record ProdutoResponse(
        int id,
        String nome,
        int categoriaId,
        String categoriaNome,
        int quantidade,
        Double preco) {

    public static ProdutoResponse from(Produto produto) {
        TipoCategoriaProduto categoria = TipoCategoriaProduto.porNome(produto.getCategoriaNome());
        return new ProdutoResponse(
                produto.getIdInt(),
                produto.getNome(),
                categoria == null ? 0 : categoria.getId(),
                produto.getCategoriaNome(),
                produto.getQuantidade(),
                produto.getPreco());
    }
}
