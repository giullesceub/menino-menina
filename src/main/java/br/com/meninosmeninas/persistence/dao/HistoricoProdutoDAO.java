package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.HistoricoProduto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HistoricoProdutoDAO extends AbstractJpaDAO<HistoricoProduto> {
    @Override
    protected Class<HistoricoProduto> entityType() {
        return HistoricoProduto.class;
    }

    public List<HistoricoProduto> listarPorProduto(Long produtoId) {
        return entityManager.createQuery(
                        "FROM HistoricoProduto h WHERE h.produtoId = :produtoId "
                                + "ORDER BY h.timestampAlteracao", HistoricoProduto.class)
                .setParameter("produtoId", produtoId)
                .getResultList();
    }
}
