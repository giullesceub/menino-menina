package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.ProdutoDAO;
import br.com.meninosmeninas.persistence.dao.TipoCategoriaProdutoDAO;
import br.com.meninosmeninas.persistence.models.Produto;
import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoDAO produtoDAO;

    @Inject
    TipoCategoriaProdutoDAO categoriaDAO;

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<Produto> listarPorCategoria(Integer idCategoria) {
        return produtoDAO.listarPorCategoria(idCategoria);
    }

    public Produto inserir(Produto produto) {
        return produtoDAO.inserir(produto);
    }

    public Produto inserir(String nome, int idCategoria, int quantidade, BigDecimal preco) {
        TipoCategoriaProduto categoria = categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null) {
            return null;
        }

        Produto produto = new Produto();
        produto.setNome(nome.trim());
        produto.setCategoria(categoria);
        produto.setQuantidade(quantidade);
        produto.setPreco(preco.doubleValue());
        return produtoDAO.inserir(produto);
    }

    public Produto atualizar(Produto produto) {
        return produtoDAO.atualizar(produto);
    }

    public Produto atualizarQuantidade(int idProduto, int novaQuantidade) {
        return produtoDAO.atualizarQuantidade(idProduto, novaQuantidade);
    }

    public Produto buscarPorId(int idProduto) {
        return produtoDAO.buscarPorId(idProduto);
    }
}
