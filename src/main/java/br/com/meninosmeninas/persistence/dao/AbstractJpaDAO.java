package br.com.meninosmeninas.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class AbstractJpaDAO<T> {
    @PersistenceContext
    protected EntityManager entityManager;

    protected abstract Class<T> entityType();

    public T buscarPorId(Long id) {
        return id == null ? null : entityManager.find(entityType(), id);
    }

    public List<T> listarTodos() {
        String entityName = entityType().getSimpleName();
        return entityManager.createQuery("FROM " + entityName, entityType()).getResultList();
    }

    @Transactional
    public T inserir(T entidade) {
        entityManager.persist(entidade);
        entityManager.flush();
        return entidade;
    }

    @Transactional
    public T atualizar(T entidade) {
        T gerenciada = entityManager.merge(entidade);
        entityManager.flush();
        return gerenciada;
    }

    @Transactional
    public void remover(T entidade) {
        entityManager.remove(entityManager.contains(entidade) ? entidade : entityManager.merge(entidade));
    }
}
