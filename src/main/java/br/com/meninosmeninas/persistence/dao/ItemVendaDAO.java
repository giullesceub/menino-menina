package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.ItemVenda;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ItemVendaDAO extends AbstractJpaDAO<ItemVenda> {
    @Override
    protected Class<ItemVenda> entityType() {
        return ItemVenda.class;
    }

    public List<ItemVenda> listarPorVenda(Long vendaId) {
        return entityManager.createQuery(
                        "FROM ItemVenda i WHERE i.venda.id = :vendaId ORDER BY i.id", ItemVenda.class)
                .setParameter("vendaId", vendaId)
                .getResultList();
    }
}
