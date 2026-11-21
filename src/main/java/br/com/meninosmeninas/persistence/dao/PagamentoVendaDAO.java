package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.PagamentoVenda;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PagamentoVendaDAO extends AbstractJpaDAO<PagamentoVenda> {
    @Override
    protected Class<PagamentoVenda> entityType() {
        return PagamentoVenda.class;
    }

    public List<PagamentoVenda> listarPorVenda(Long vendaId) {
        return entityManager.createQuery(
                        "FROM PagamentoVenda p WHERE p.venda.id = :vendaId ORDER BY p.id",
                        PagamentoVenda.class)
                .setParameter("vendaId", vendaId)
                .getResultList();
    }
}
