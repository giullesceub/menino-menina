package br.com.meninosmeninas.persistence.dao;

import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class TipoCategoriaProdutoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<TipoCategoriaProduto> listarTodos() {
        return entityManager
                .createQuery("SELECT c FROM TipoCategoriaProduto c ORDER BY c.nome", TipoCategoriaProduto.class)
                .getResultList();
    }

    public TipoCategoriaProduto buscarPorId(int idCategoria) {
        return entityManager.find(TipoCategoriaProduto.class, idCategoria);
    }
}
