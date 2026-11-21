package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.HistoricoCliente;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HistoricoClienteDAO extends AbstractJpaDAO<HistoricoCliente> {
    @Override
    protected Class<HistoricoCliente> entityType() {
        return HistoricoCliente.class;
    }

    public List<HistoricoCliente> listarPorCliente(Long clienteId) {
        return entityManager.createQuery(
                        "FROM HistoricoCliente h WHERE h.clienteId = :clienteId "
                                + "ORDER BY h.timestampAlteracao", HistoricoCliente.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
    }
}
