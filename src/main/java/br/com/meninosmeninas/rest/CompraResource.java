package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.*;
import br.com.meninosmeninas.rest.dto.CompraRequest;
import br.com.meninosmeninas.service.*;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/api/compras")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CompraResource {
    @Inject CompraService service;
    @POST public Response registrar(CompraRequest r) {
        if (r == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
        }
        var itens = new ArrayList<ItemCompra>();
        for (CompraRequest.Item item : r.itens()) {
            ItemCompra entidade = new ItemCompra();
            Produto produto = new Produto(); produto.setId(item.produtoId());
            entidade.setProduto(produto); entidade.setQuantidade(item.quantidade());
            entidade.setCustoUnitario(item.custoUnitario()); itens.add(entidade);
        }
        Compra compra = service.registrar(new Credenciais(r.login(), r.senha()), r.fornecedorId(), itens);
        return Response.status(Response.Status.CREATED).entity(compra).build();
    }
}
