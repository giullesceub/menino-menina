package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.rest.dto.CategoriaResponse;
import br.com.meninosmeninas.service.TipoCategoriaProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/categorias")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    @Inject
    TipoCategoriaProdutoService categoriaService;

    @GET
    public List<CategoriaResponse> listar() {
        return categoriaService.listarTodos().stream()
                .map(categoria -> new CategoriaResponse(categoria.getId(), categoria.getNome()))
                .toList();
    }
}
