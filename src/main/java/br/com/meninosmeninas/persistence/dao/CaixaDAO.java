package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Caixa;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CaixaDAO extends AbstractJpaDAO<Caixa> {
    @Override
    protected Class<Caixa> entityType() {
        return Caixa.class;
    }

    public Optional<Caixa> buscarAberto() {
        return entityManager.createQuery(
                        "FROM Caixa c WHERE c.status = :status ORDER BY c.dataAbertura DESC",
                        Caixa.class)
                .setParameter("status", Caixa.Status.ABERTO)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
