package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Categoria;
import br.com.meninosmeninas.persistence.models.Fornecedor;
import br.com.meninosmeninas.persistence.models.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProdutoDAO extends AbstractJpaDAO<Produto> {

    @Override
    protected Class<Produto> entityType() {
        return Produto.class;
    }

    private static final String SELECT = "SELECT p.id, p.nome, c.id, c.nome, p.tamanho, p.cor, "
            + "p.codigo_barras, p.preco_venda, p.estoque_atual, p.fornecedor_id, p.ativo "
            + "FROM produto p JOIN categoria c ON c.id = p.categoria_id ";

    public List<Produto> listarTodos() {
        return mapear(nativeQuery(SELECT + "WHERE p.ativo = TRUE ORDER BY p.id"));
    }

    public List<Produto> listarPorCategoria(String categoria) {
        if (categoria == null) {
            return listarTodos();
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(SELECT
                        + "WHERE p.ativo = TRUE AND c.nome = :categoria ORDER BY p.id")
                .setParameter("categoria", categoria)
                .getResultList();
        return mapear(rows);
    }

    public Produto buscarPorId(Long idProduto) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(SELECT + "WHERE p.id = :id")
                .setParameter("id", idProduto)
                .getResultList();
        return rows.isEmpty() ? null : mapear(rows).get(0);
    }

    public Produto buscarPorId(int idProduto) {
        return buscarPorId((long) idProduto);
    }

    @Transactional
    public Produto inserir(Produto produto) {
        if (produto.getCategoria() != null) {
            produto.setCategoria(entityManager.find(Categoria.class, produto.getCategoria().getId()));
        }
        if (produto.getFornecedorId() != null) {
            produto.setFornecedor(entityManager.getReference(Fornecedor.class, produto.getFornecedorId()));
        }
        entityManager.persist(produto);
        entityManager.flush();
        return produto;
    }

    @Transactional
    public Produto atualizar(Produto produto) {
        entityManager.merge(produto);
        entityManager.flush();
        return buscarPorId(produto.getId());
    }

    @Transactional
    public Produto atualizarQuantidade(int idProduto, int novaQuantidade) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
        Produto produto = buscarPorId(idProduto);
        if (produto == null) {
            return null;
        }
        entityManager.createNativeQuery("UPDATE produto SET estoque_atual = :quantidade WHERE id = :id")
                .setParameter("quantidade", novaQuantidade)
                .setParameter("id", idProduto)
                .executeUpdate();
        produto.setEstoqueAtual(novaQuantidade);
        return produto;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> nativeQuery(String sql) {
        return entityManager.createNativeQuery(sql).getResultList();
    }

    private List<Produto> mapear(List<Object[]> rows) {
        List<Produto> produtos = new ArrayList<>();
        for (Object[] row : rows) {
            Produto produto = new Produto();
            produto.setId(((Number) row[0]).longValue());
            produto.setNome((String) row[1]);
            Categoria categoria = new Categoria();
            categoria.setId(((Number) row[2]).longValue());
            categoria.setNome((String) row[3]);
            produto.setCategoria(categoria);
            produto.setTamanho((String) row[4]);
            produto.setCor((String) row[5]);
            produto.setCodigoBarras((String) row[6]);
            produto.setPrecoVenda(toBigDecimal(row[7]));
            produto.setEstoqueAtual(((Number) row[8]).intValue());
            produto.setFornecedorId(row[9] == null ? null : ((Number) row[9]).longValue());
            produto.setAtivo(row[10] instanceof Boolean && (Boolean) row[10]);
            produtos.add(produto);
        }
        return produtos;
    }

    private BigDecimal toBigDecimal(Object value) {
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }
}
