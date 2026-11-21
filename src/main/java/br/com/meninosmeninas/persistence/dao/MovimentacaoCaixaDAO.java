package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.MovimentacaoCaixa;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MovimentacaoCaixaDAO extends AbstractJpaDAO<MovimentacaoCaixa> {
    @Override
    protected Class<MovimentacaoCaixa> entityType() {
        return MovimentacaoCaixa.class;
    }

    public List<MovimentacaoCaixa> listarPorCaixa(Long caixaId) {
        return entityManager.createQuery(
                        "FROM MovimentacaoCaixa m WHERE m.caixa.id = :caixaId ORDER BY m.dataMovimentacao, m.id",
                        MovimentacaoCaixa.class)
                .setParameter("caixaId", caixaId)
                .getResultList();
    }

    public List<MovimentacaoCaixa> listarPorVenda(Long vendaId) {
        return entityManager.createQuery(
                        "FROM MovimentacaoCaixa m WHERE m.venda.id = :vendaId ORDER BY m.id",
                        MovimentacaoCaixa.class)
                .setParameter("vendaId", vendaId)
                .getResultList();
    }
}
