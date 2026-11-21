package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.rest.dto.CredenciaisRequest;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.service.HistoricoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/historico")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HistoricoResource {
    @Inject HistoricoService service;

    @POST @Path("/clientes/{id}")
    public Response clientes(@PathParam("id") Long id, CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.listarClientes(request.toCredentials(), id)).build();
    }

    @POST @Path("/fornecedores/{id}")
    public Response fornecedores(@PathParam("id") Long id, CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.listarFornecedores(request.toCredentials(), id)).build();
    }

    @POST @Path("/funcionarios/{id}")
    public Response funcionarios(@PathParam("id") Long id, CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.listarFuncionarios(request.toCredentials(), id)).build();
    }

    @POST @Path("/produtos/{id}")
    public Response produtos(@PathParam("id") Long id, CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.listarProdutos(request.toCredentials(), id)).build();
    }
    private Response erroRequest() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
    }
}
