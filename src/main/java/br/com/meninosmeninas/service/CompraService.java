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
public class CompraService {
    @Inject CompraDAO compraDAO;
    @Inject ItemCompraDAO itemDAO;
    @Inject FornecedorDAO fornecedorDAO;
    @Inject ProdutoDAO produtoDAO;
    @Inject AutenticacaoService autenticacaoService;
    @Inject HistoricoService historicoService;

    @Transactional
    public Compra registrar(Credenciais credenciais, Long fornecedorId, List<ItemCompra> itens) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Fornecedor fornecedor = fornecedorDAO.buscarPorId(fornecedorId);
        if (fornecedor == null || !fornecedor.isAtivo() || itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("Fornecedor ou itens da compra inválidos.");
        }
        Compra compra = new Compra();
        compra.setFornecedor(fornecedor);
        compra.setValorTotal(BigDecimal.ZERO);
        compraDAO.inserir(compra);
        BigDecimal total = BigDecimal.ZERO;
        Map<String, ItemCompra> consolidados = new LinkedHashMap<>();
        for (ItemCompra item : itens) {
            validarItem(item);
            Produto produto = produtoDAO.buscarPorId(item.getProduto().getId());
            if (produto == null || !produto.isAtivo()) {
                throw new RegraNegocioException("Produto da compra não encontrado ou inativo.");
            }
            String chave = produto.getId() + ":" + item.getCustoUnitario().toPlainString();
            ItemCompra existente = consolidados.get(chave);
            if (existente != null) {
                existente.setQuantidade(existente.getQuantidade() + item.getQuantidade());
            } else {
                item.setProduto(produto);
                consolidados.put(chave, item);
            }
        }
        for (ItemCompra item : new ArrayList<>(consolidados.values())) {
            Produto produto = item.getProduto();
            item.setCompra(compra);
            item.setSubtotal(item.getCustoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            total = total.add(item.getSubtotal());
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
            produtoDAO.atualizar(produto);
            historicoService.registrar(produto, usuario, "A");
            itemDAO.inserir(item);
        }
        compra.setValorTotal(total);
        return compraDAO.atualizar(compra);
    }

    private void validarItem(ItemCompra item) {
        if (item == null || item.getProduto() == null || item.getProduto().getId() == null
                || item.getQuantidade() <= 0 || item.getCustoUnitario() == null
                || item.getCustoUnitario().signum() < 0) {
            throw new RegraNegocioException("Item de compra inválido.");
        }
    }
}
