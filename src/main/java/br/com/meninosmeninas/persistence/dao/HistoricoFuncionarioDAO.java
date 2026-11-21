package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.HistoricoFuncionario;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HistoricoFuncionarioDAO extends AbstractJpaDAO<HistoricoFuncionario> {
    @Override
    protected Class<HistoricoFuncionario> entityType() {
        return HistoricoFuncionario.class;
    }

    public List<HistoricoFuncionario> listarPorFuncionario(Long funcionarioId) {
        return entityManager.createQuery(
                        "FROM HistoricoFuncionario h WHERE h.funcionarioId = :funcionarioId "
                                + "ORDER BY h.timestampAlteracao", HistoricoFuncionario.class)
                .setParameter("funcionarioId", funcionarioId)
                .getResultList();
    }
}
