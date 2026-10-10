package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProdutoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Produto> listarTodos() {
        return entityManager
                .createNamedQuery("Produto.listarTodos", Produto.class)
                .getResultList();
    }

    public List<Produto> listarPorCategoria(Integer idCategoria) {
        if (idCategoria == null) {
            return listarTodos();
        }

        return entityManager
                .createNamedQuery("Produto.listarPorCategoria", Produto.class)
                .setParameter("idCategoria", idCategoria)
                .getResultList();
    }

    public Produto buscarPorId(int idProduto) {
        return entityManager.find(Produto.class, idProduto);
    }

    @Transactional
    public Produto inserir(Produto produto) {
        entityManager.persist(produto);
        return produto;
    }

    @Transactional
    public Produto atualizar(Produto produto) {
        return entityManager.merge(produto);
    }

    @Transactional
    public Produto atualizarQuantidade(int idProduto, int novaQuantidade) {
        List<Produto> produtos = entityManager
                .createQuery(
                        "SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.id = :idProduto",
                        Produto.class)
                .setParameter("idProduto", idProduto)
                .getResultList();
        Produto produto = produtos.isEmpty() ? null : produtos.get(0);

        if (produto == null) {
            return null;
        }

        produto.setQuantidade(novaQuantidade);
        return produto;
    }
}
