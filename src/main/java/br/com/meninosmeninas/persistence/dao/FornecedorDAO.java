package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Fornecedor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FornecedorDAO extends AbstractJpaDAO<Fornecedor> {
    @Override
    protected Class<Fornecedor> entityType() {
        return Fornecedor.class;
    }
}
