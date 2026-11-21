package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.Caixa;
import br.com.meninosmeninas.rest.dto.CaixaRequest;
import br.com.meninosmeninas.rest.dto.CredenciaisRequest;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.service.CaixaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/caixa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CaixaResource {
    @Inject CaixaService service;
    @GET @Path("/aberto") public Response aberto() {
        Caixa caixa = service.buscarAberto();
        return caixa == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(caixa).build();
    }
    @POST @Path("/abrir") public Response abrir(CaixaRequest r) {
        if (r == null) return erroRequest();
        return Response.status(Response.Status.CREATED).entity(service.abrir(
                new br.com.meninosmeninas.service.Credenciais(r.login(), r.senha()), r.valor())).build();
    }
    @POST @Path("/entrada") public Response entrada(CaixaRequest r) {
        if (r == null) return erroRequest();
        return Response.status(Response.Status.CREATED).entity(service.registrarEntradaManual(
                new br.com.meninosmeninas.service.Credenciais(r.login(), r.senha()), r.valor(), r.descricao())).build();
    }
    @POST @Path("/retirada") public Response retirada(CaixaRequest r) {
        if (r == null) return erroRequest();
        return Response.status(Response.Status.CREATED).entity(service.registrarRetirada(
                new br.com.meninosmeninas.service.Credenciais(r.login(), r.senha()), r.valor(), r.descricao())).build();
    }
    @POST @Path("/fechar") public Response fechar(CaixaRequest r) {
        if (r == null) return erroRequest();
        return Response.ok(br.com.meninosmeninas.rest.dto.FechamentoResponse.from(service.fechar(
                new br.com.meninosmeninas.service.Credenciais(r.login(), r.senha()), r.valor()))).build();
    }
    @GET @Path("/{id}/movimentacoes") public Response movimentacoes(@PathParam("id") Long id) {
        return Response.ok(service.listarMovimentacoes(id)).build();
    }
    private Response erroRequest() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
    }
}
