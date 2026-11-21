package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.Fornecedor;
import br.com.meninosmeninas.rest.dto.FornecedorRequest;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.service.FornecedorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/fornecedores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FornecedorResource {
    @Inject FornecedorService service;
    @GET public Response listar() { return Response.ok(service.listar()).build(); }
    @GET @Path("/{id}") public Response buscar(@PathParam("id") Long id) {
        Fornecedor fornecedor = service.buscar(id);
        return fornecedor == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(fornecedor).build();
    }
    @POST public Response cadastrar(FornecedorRequest request) {
        if (request == null) return erroRequest();
        return Response.status(Response.Status.CREATED).entity(service.cadastrar(
                credenciais(request.login(), request.senha()), entidade(request, null))).build();
    }
    @PUT @Path("/{id}") public Response atualizar(@PathParam("id") Long id, FornecedorRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.atualizar(
                credenciais(request.login(), request.senha()), entidade(request, id))).build();
    }
    @PATCH @Path("/{id}/desativar") public Response desativar(@PathParam("id") Long id,
            br.com.meninosmeninas.rest.dto.CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.alterarAtivo(request.toCredentials(), id, false)).build();
    }
    @PATCH @Path("/{id}/ativar") public Response ativar(@PathParam("id") Long id,
            br.com.meninosmeninas.rest.dto.CredenciaisRequest request) {
        if (request == null) return erroRequest();
        return Response.ok(service.alterarAtivo(request.toCredentials(), id, true)).build();
    }
    private Fornecedor entidade(FornecedorRequest r, Long id) {
        Fornecedor f = new Fornecedor(); f.setId(id); f.setRazaoSocial(r.razaoSocial());
        f.setNomeFantasia(r.nomeFantasia()); f.setCnpj(r.cnpj()); f.setTelefone(r.telefone()); f.setEmail(r.email());
        return f;
    }
    private br.com.meninosmeninas.service.Credenciais credenciais(String l, String s) {
        return new br.com.meninosmeninas.service.Credenciais(l, s);
    }
    private Response erroRequest() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
    }
}
