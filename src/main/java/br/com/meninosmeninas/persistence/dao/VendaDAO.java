package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Venda;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VendaDAO extends AbstractJpaDAO<Venda> {
    @Override
    protected Class<Venda> entityType() {
        return Venda.class;
    }
}
