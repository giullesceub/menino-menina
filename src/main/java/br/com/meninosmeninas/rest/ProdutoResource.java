package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.Produto;
import br.com.meninosmeninas.rest.dto.AtualizarQuantidadeRequest;
import br.com.meninosmeninas.rest.dto.CriarProdutoRequest;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.rest.dto.ProdutoResponse;
import br.com.meninosmeninas.service.ProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/produtos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService produtoService;

    @GET
    public List<ProdutoResponse> listar(@QueryParam("categoriaId") Integer categoriaId) {
        return produtoService.listarPorCategoria(categoriaId).stream()
                .map(ProdutoResponse::from)
                .toList();
    }

    @POST
    public Response inserir(CriarProdutoRequest request) {
        if (request == null
                || request.nome() == null
                || request.nome().isBlank()
                || request.categoriaId() == null
                || request.categoriaId() <= 0
                || request.quantidade() == null
                || request.quantidade() < 0
                || request.preco() == null
                || request.preco().signum() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("Informe nome, categoriaId, quantidade não negativa e preco não negativo."))
                    .build();
        }

        Produto produto = produtoService.inserir(
                request.nome(), request.categoriaId(), request.quantidade(), request.preco());
        if (produto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("A categoria informada não existe."))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(ProdutoResponse.from(produto))
                .build();
    }

    @PATCH
    @Path("/{id}/quantidade")
    public Response atualizarQuantidade(
            @PathParam("id") int idProduto,
            AtualizarQuantidadeRequest request) {
        if (idProduto <= 0 || request == null || request.quantidade() == null || request.quantidade() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("Informe um id válido e uma quantidade não negativa."))
                    .build();
        }

        Produto produto = produtoService.atualizarQuantidade(idProduto, request.quantidade());
        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErroResponse("Produto não encontrado."))
                    .build();
        }

        return Response.ok(ProdutoResponse.from(produto)).build();
    }
}
