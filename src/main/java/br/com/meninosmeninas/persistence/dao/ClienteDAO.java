package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Cliente;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClienteDAO extends AbstractJpaDAO<Cliente> {
    @Override
    protected Class<Cliente> entityType() {
        return Cliente.class;
    }
}
