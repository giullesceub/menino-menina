package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Compra;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompraDAO extends AbstractJpaDAO<Compra> {
    @Override
    protected Class<Compra> entityType() {
        return Compra.class;
    }
}
