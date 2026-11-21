package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.ItemCompra;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ItemCompraDAO extends AbstractJpaDAO<ItemCompra> {
    @Override
    protected Class<ItemCompra> entityType() {
        return ItemCompra.class;
    }

    public List<ItemCompra> listarPorCompra(Long compraId) {
        return entityManager.createQuery(
                        "SELECT i FROM ItemCompra i JOIN FETCH i.produto WHERE i.compra.id = :compraId ORDER BY i.id", ItemCompra.class)
                .setParameter("compraId", compraId)
                .getResultList();
    }
}
