package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class FuncionarioDAO extends AbstractJpaDAO<Funcionario> {
    @Override
    protected Class<Funcionario> entityType() {
        return Funcionario.class;
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        return entityManager.createQuery(
                        "FROM Funcionario f WHERE f.login = :login", Funcionario.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst();
    }
}
