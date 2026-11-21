package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.HistoricoFornecedor;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HistoricoFornecedorDAO extends AbstractJpaDAO<HistoricoFornecedor> {
    @Override
    protected Class<HistoricoFornecedor> entityType() {
        return HistoricoFornecedor.class;
    }

    public List<HistoricoFornecedor> listarPorFornecedor(Long fornecedorId) {
        return entityManager.createQuery(
                        "FROM HistoricoFornecedor h WHERE h.fornecedorId = :fornecedorId "
                                + "ORDER BY h.timestampAlteracao", HistoricoFornecedor.class)
                .setParameter("fornecedorId", fornecedorId)
                .getResultList();
    }
}
