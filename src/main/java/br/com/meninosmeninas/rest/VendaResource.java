package br.com.meninosmeninas.rest;

import br.com.meninosmeninas.persistence.models.*;
import br.com.meninosmeninas.rest.dto.VendaRequest;
import br.com.meninosmeninas.service.*;
import br.com.meninosmeninas.rest.dto.ErroResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/api/vendas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendaResource {
    @Inject VendaService service;
    @POST public Response registrar(VendaRequest r) {
        if (r == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("O corpo da requisição é obrigatório.")).build();
        }
        var itens = new ArrayList<ItemVenda>();
        for (VendaRequest.Item item : r.itens()) {
            ItemVenda entidade = new ItemVenda();
            Produto produto = new Produto(); produto.setId(item.produtoId());
            entidade.setProduto(produto); entidade.setQuantidade(item.quantidade()); itens.add(entidade);
        }
        var pagamentos = new ArrayList<PagamentoVenda>();
        for (VendaRequest.Pagamento pagamento : r.pagamentos()) {
            PagamentoVenda entidade = new PagamentoVenda();
            entidade.setFormaPagamento(pagamento.formaPagamento()); entidade.setValor(pagamento.valor());
            pagamentos.add(entidade);
        }
        Venda venda = service.registrar(new Credenciais(r.login(), r.senha()), r.clienteId(), itens, pagamentos);
        return Response.status(Response.Status.CREATED).entity(venda).build();
    }
}
