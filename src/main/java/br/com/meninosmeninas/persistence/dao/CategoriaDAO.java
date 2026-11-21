package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Categoria;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoriaDAO extends AbstractJpaDAO<Categoria> {
    @Override
    protected Class<Categoria> entityType() {
        return Categoria.class;
    }
}
