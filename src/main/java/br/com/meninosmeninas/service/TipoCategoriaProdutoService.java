package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.TipoCategoriaProdutoDAO;
import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TipoCategoriaProdutoService {

    @Inject
    TipoCategoriaProdutoDAO categoriaDAO;

    public List<TipoCategoriaProduto> listarTodos() {
        return categoriaDAO.listarTodos();
    }
}
