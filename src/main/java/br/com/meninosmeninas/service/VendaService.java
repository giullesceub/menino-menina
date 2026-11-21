package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.*;
import br.com.meninosmeninas.persistence.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class VendaService {
    @Inject VendaDAO vendaDAO;
    @Inject ItemVendaDAO itemDAO;
    @Inject PagamentoVendaDAO pagamentoDAO;
    @Inject ProdutoDAO produtoDAO;
    @Inject ClienteDAO clienteDAO;
    @Inject CaixaDAO caixaDAO;
    @Inject MovimentacaoCaixaDAO movimentacaoDAO;
    @Inject AutenticacaoService autenticacaoService;
    @Inject HistoricoService historicoService;

    @Transactional
    public Venda registrar(Credenciais credenciais, Long clienteId, List<ItemVenda> itens,
                           List<PagamentoVenda> pagamentos) {
        Funcionario funcionario = autenticacaoService.autenticar(credenciais);
        if (itens == null || itens.isEmpty() || pagamentos == null || pagamentos.isEmpty()) {
            throw new RegraNegocioException("Itens e pagamentos são obrigatórios.");
        }
        Cliente cliente = clienteId == null ? null : clienteDAO.buscarPorId(clienteId);
        if (clienteId != null && (cliente == null || !cliente.isAtivo())) {
            throw new RegraNegocioException("Cliente não encontrado ou inativo.");
        }
        Venda venda = new Venda();
        venda.setFuncionario(funcionario);
        venda.setCliente(cliente);
        venda.setValorTotal(BigDecimal.ZERO);
        vendaDAO.inserir(venda);
        BigDecimal total = BigDecimal.ZERO;
        Map<Long, ItemVenda> consolidados = new LinkedHashMap<>();
        for (ItemVenda item : itens) {
            validarItem(item);
            Produto produto = produtoDAO.buscarPorId(item.getProduto().getId());
            if (produto == null || !produto.isAtivo() || produto.getEstoqueAtual() < item.getQuantidade()) {
                throw new RegraNegocioException("Produto indisponível ou sem estoque suficiente.");
            }
            ItemVenda existente = consolidados.get(produto.getId());
            if (existente != null) {
                existente.setQuantidade(existente.getQuantidade() + item.getQuantidade());
            } else {
                item.setProduto(produto);
                consolidados.put(produto.getId(), item);
            }
        }
        for (ItemVenda item : new ArrayList<>(consolidados.values())) {
            Produto produto = item.getProduto();
            if (produto.getEstoqueAtual() < item.getQuantidade()) {
                throw new RegraNegocioException("Estoque insuficiente para o produto.");
            }
            item.setVenda(venda);
            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            total = total.add(item.getSubtotal());
            produto.setEstoqueAtual(produto.getEstoqueAtual() - item.getQuantidade());
            produtoDAO.atualizar(produto);
            historicoService.registrar(produto, funcionario, "A");
            itemDAO.inserir(item);
        }
        BigDecimal totalPagamentos = BigDecimal.ZERO;
        for (PagamentoVenda pagamento : pagamentos) {
            validarPagamento(pagamento);
            pagamento.setVenda(venda);
            totalPagamentos = totalPagamentos.add(pagamento.getValor());
            pagamentoDAO.inserir(pagamento);
        }
        if (total.compareTo(totalPagamentos) != 0) {
            throw new RegraNegocioException("A soma dos pagamentos deve ser igual ao total da venda.");
        }
        venda.setValorTotal(total);
        Venda salva = vendaDAO.atualizar(venda);
        registrarDinheiroNoCaixa(funcionario, salva, pagamentos);
        return salva;
    }

    private void registrarDinheiroNoCaixa(Funcionario funcionario, Venda venda,
                                           List<PagamentoVenda> pagamentos) {
        BigDecimal dinheiro = pagamentos.stream()
                .filter(p -> p.getFormaPagamento() == PagamentoVenda.FormaPagamento.DINHEIRO)
                .map(PagamentoVenda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (dinheiro.signum() == 0) return;
        Caixa caixa = caixaDAO.buscarAberto()
                .orElseThrow(() -> new RegraNegocioException("É necessário abrir o caixa para receber dinheiro."));
        MovimentacaoCaixa movimentacao = new MovimentacaoCaixa();
        movimentacao.setCaixa(caixa);
        movimentacao.setFuncionario(funcionario);
        movimentacao.setVenda(venda);
        movimentacao.setTipo(MovimentacaoCaixa.Tipo.ENTRADA);
        movimentacao.setCategoria(MovimentacaoCaixa.Categoria.VENDA);
        movimentacao.setValor(dinheiro);
        movimentacao.setDescricao("Venda " + venda.getId());
        movimentacaoDAO.inserir(movimentacao);
    }

    private void validarItem(ItemVenda item) {
        if (item == null || item.getProduto() == null || item.getProduto().getId() == null
                || item.getQuantidade() <= 0) {
            throw new RegraNegocioException("Item de venda inválido.");
        }
    }

    private void validarPagamento(PagamentoVenda pagamento) {
        if (pagamento == null || pagamento.getFormaPagamento() == null
                || pagamento.getValor() == null || pagamento.getValor().signum() <= 0) {
            throw new RegraNegocioException("Pagamento inválido.");
        }
    }
}
