package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.rest.dto.ErroResponse;
import br.com.meninosmeninas.service.RegraNegocioException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<RegraNegocioException> {
    @Override
    public Response toResponse(RegraNegocioException exception) {
        String mensagem = exception.getMessage();
        Response.Status status = mensagem != null && mensagem.startsWith("Credenciais")
                ? Response.Status.UNAUTHORIZED
                : mensagem != null && mensagem.contains("permissão")
                ? Response.Status.FORBIDDEN
                : Response.Status.BAD_REQUEST;
        return Response.status(status).entity(new ErroResponse(mensagem)).build();
    }
}
