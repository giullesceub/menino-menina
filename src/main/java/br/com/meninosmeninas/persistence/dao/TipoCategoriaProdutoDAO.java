package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TipoCategoriaProdutoDAO {
    @PersistenceContext
    EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<TipoCategoriaProduto> listarTodos() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT id, nome FROM categoria ORDER BY id").getResultList();
        List<TipoCategoriaProduto> categorias = new ArrayList<>();
        for (Object[] row : rows) {
            categorias.add(new TipoCategoriaProduto(((Number) row[0]).intValue(), (String) row[1]));
        }
        return categorias;
    }

    public TipoCategoriaProduto buscarPorId(int idCategoria) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(
                        "SELECT id, nome FROM categoria WHERE id = :id")
                .setParameter("id", idCategoria)
                .getResultList();
        return rows.isEmpty()
                ? null
                : new TipoCategoriaProduto(((Number) rows.get(0)[0]).intValue(), (String) rows.get(0)[1]);
    }
}
