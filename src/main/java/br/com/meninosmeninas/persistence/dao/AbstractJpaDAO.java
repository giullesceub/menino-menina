package br.com.meninosmeninas.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        trimStrings(entidade);
        entityManager.persist(entidade);
        entityManager.flush();
        return entidade;
    }

    @Transactional
    public T atualizar(T entidade) {
        trimStrings(entidade);
        T gerenciada = entityManager.merge(entidade);
        entityManager.flush();
        return gerenciada;
    }

    @Transactional
    public void remover(T entidade) {
        entityManager.remove(entityManager.contains(entidade) ? entidade : entityManager.merge(entidade));
    }

    private void trimStrings(Object entidade) {
        if (entidade == null) return;

        Class<?> tipo = entidade.getClass();
        while (tipo != null && tipo != Object.class) {
            for (Field campo : tipo.getDeclaredFields()) {
                if (campo.getType() == String.class && !Modifier.isStatic(campo.getModifiers())) {
                    try {
                        campo.setAccessible(true);
                        Object valor = campo.get(entidade);
                        if (valor instanceof String texto) {
                            campo.set(entidade, texto.trim());
                        }
                    } catch (IllegalAccessException exception) {
                        throw new IllegalStateException("Não foi possível normalizar campos String da entidade.", exception);
                    }
                }
            }
            tipo = tipo.getSuperclass();
        }
    }
}
