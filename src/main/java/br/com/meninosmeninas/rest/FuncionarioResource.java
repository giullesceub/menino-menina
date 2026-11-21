package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.Funcionario;
import br.com.meninosmeninas.rest.dto.FuncionarioRequest;
import br.com.meninosmeninas.service.FuncionarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/funcionarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FuncionarioResource {
    @Inject FuncionarioService service;
    @GET public Response listar() {
        return Response.ok(service.listar().stream().map(f -> new Resposta(f.getId(), f.getNome(),
                f.getCpf(), f.getTelefone(), f.getLogin(), f.getPerfil(), f.isAtivo())).toList()).build();
    }
    @POST public Response cadastrar(FuncionarioRequest r) {
        Funcionario f = new Funcionario(); f.setNome(r.nome()); f.setCpf(r.cpf()); f.setTelefone(r.telefone());
        f.setLogin(r.login()); f.setPerfil(r.perfil());
        Funcionario salvo = service.cadastrar(new br.com.meninosmeninas.service.Credenciais(r.operadorLogin(), r.operadorSenha()), f, r.senha());
        return Response.status(Response.Status.CREATED).entity(new Resposta(salvo.getId(), salvo.getNome(),
                salvo.getCpf(), salvo.getTelefone(), salvo.getLogin(), salvo.getPerfil(), salvo.isAtivo())).build();
    }
    @PUT @Path("/{id}") public Response atualizar(@PathParam("id") Long id,
            br.com.meninosmeninas.rest.dto.AtualizarFuncionarioRequest r) {
        if (r == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new br.com.meninosmeninas.rest.dto.ErroResponse("O corpo da requisição é obrigatório."))
                    .build();
        }

        Funcionario f = service.atualizarCadastro(id,
                new FuncionarioService.AtualizarCadastroFuncionario(
                        r.nome(), r.telefone(), r.novaSenha(), r.login(), r.senha()));
        return Response.ok(new Resposta(f.getId(), f.getNome(), f.getCpf(), f.getTelefone(),
                f.getLogin(), f.getPerfil(), f.isAtivo())).build();
    }

    @PATCH @Path("/{id}/desativar") public Response desativar(@PathParam("id") Long id, br.com.meninosmeninas.rest.dto.CredenciaisRequest r) {
        Funcionario f = service.desativar(r.toCredentials(), id);
        return Response.ok(new Resposta(f.getId(), f.getNome(), f.getCpf(), f.getTelefone(),
                f.getLogin(), f.getPerfil(), f.isAtivo())).build();
    }
    @PATCH @Path("/{id}/ativar") public Response ativar(@PathParam("id") Long id,
            br.com.meninosmeninas.rest.dto.CredenciaisRequest r) {
        Funcionario f = service.reativar(r.toCredentials(), id);
        return Response.ok(new Resposta(f.getId(), f.getNome(), f.getCpf(), f.getTelefone(),
                f.getLogin(), f.getPerfil(), f.isAtivo())).build();
    }
    public record Resposta(Long id, String nome, String cpf, String telefone, String login,
                           Funcionario.Perfil perfil, boolean ativo) {}
}
