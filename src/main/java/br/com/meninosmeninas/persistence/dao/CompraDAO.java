package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Compra;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CompraDAO extends AbstractJpaDAO<Compra> {
    @Override
    protected Class<Compra> entityType() {
        return Compra.class;
    }

    public List<Compra> listarTodasComFornecedor() {
        return entityManager.createQuery(
                        "SELECT c FROM Compra c JOIN FETCH c.fornecedor ORDER BY c.id", Compra.class)
                .getResultList();
    }

    public Compra buscarComFornecedor(Long id) {
        if (id == null) return null;
        return entityManager.createQuery(
                        "SELECT c FROM Compra c JOIN FETCH c.fornecedor WHERE c.id = :id", Compra.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
