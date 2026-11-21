package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.Cliente;
import br.com.meninosmeninas.rest.dto.ClienteRequest;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.service.ClienteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {
    @Inject ClienteService service;

    @GET public Response listar() { return Response.ok(service.listar()).build(); }
    @GET @Path("/{id}") public Response buscar(@PathParam("id") Long id) {
        Cliente cliente = service.buscar(id);
        return cliente == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(cliente).build();
    }
    @POST public Response cadastrar(ClienteRequest request) {
        if (request == null) return erroRequest();
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome()); cliente.setCpf(request.cpf()); cliente.setTelefone(request.telefone());
        return Response.status(Response.Status.CREATED).entity(service.cadastrar(
                new br.com.meninosmeninas.service.Credenciais(request.login(), request.senha()), cliente)).build();
    }
    @PUT @Path("/{id}") public Response atualizar(@PathParam("id") Long id, ClienteRequest request) {
        if (request == null) return erroRequest();
        Cliente cliente = new Cliente(); cliente.setId(id); cliente.setNome(request.nome());
        cliente.setCpf(request.cpf()); cliente.setTelefone(request.telefone());
        return Response.ok(service.atualizar(
                new br.com.meninosmeninas.service.Credenciais(request.login(), request.senha()), cliente)).build();
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
    private Response erroRequest() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
    }
}
