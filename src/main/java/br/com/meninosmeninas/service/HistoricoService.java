package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.*;
import br.com.meninosmeninas.persistence.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class HistoricoService {
    @Inject HistoricoClienteDAO clienteDAO;
    @Inject HistoricoFornecedorDAO fornecedorDAO;
    @Inject HistoricoFuncionarioDAO funcionarioDAO;
    @Inject HistoricoProdutoDAO produtoDAO;
    @Inject AutenticacaoService autenticacaoService;

    public List<HistoricoCliente> listarClientes(Credenciais credenciais, Long id) {
        autenticacaoService.exigirProprietaria(credenciais);
        return clienteDAO.listarPorCliente(id);
    }

    public List<HistoricoFornecedor> listarFornecedores(Credenciais credenciais, Long id) {
        autenticacaoService.exigirProprietaria(credenciais);
        return fornecedorDAO.listarPorFornecedor(id);
    }

    public List<HistoricoFuncionario> listarFuncionarios(Credenciais credenciais, Long id) {
        autenticacaoService.exigirProprietaria(credenciais);
        return funcionarioDAO.listarPorFuncionario(id);
    }

    public List<HistoricoProduto> listarProdutos(Credenciais credenciais, Long id) {
        autenticacaoService.exigirProprietaria(credenciais);
        return produtoDAO.listarPorProduto(id);
    }

    @Transactional
    public void registrar(Cliente entidade, Funcionario usuario, String tipo) {
        HistoricoCliente historico = new HistoricoCliente();
        historico.setClienteId(entidade.getId());
        historico.setNome(entidade.getNome());
        historico.setCpf(entidade.getCpf());
        historico.setTelefone(entidade.getTelefone());
        historico.setAtivo(entidade.isAtivo());
        preencher(historico, usuario, tipo);
        clienteDAO.inserir(historico);
    }

    @Transactional
    public void registrar(Fornecedor entidade, Funcionario usuario, String tipo) {
        HistoricoFornecedor historico = new HistoricoFornecedor();
        historico.setFornecedorId(entidade.getId());
        historico.setRazaoSocial(entidade.getRazaoSocial());
        historico.setNomeFantasia(entidade.getNomeFantasia());
        historico.setCnpj(entidade.getCnpj());
        historico.setTelefone(entidade.getTelefone());
        historico.setEmail(entidade.getEmail());
        historico.setAtivo(entidade.isAtivo());
        preencher(historico, usuario, tipo);
        fornecedorDAO.inserir(historico);
    }

    @Transactional
    public void registrar(Funcionario entidade, Funcionario usuario, String tipo) {
        HistoricoFuncionario historico = new HistoricoFuncionario();
        historico.setFuncionarioId(entidade.getId());
        historico.setNome(entidade.getNome());
        historico.setCpf(entidade.getCpf());
        historico.setTelefone(entidade.getTelefone());
        historico.setLogin(entidade.getLogin());
        historico.setPerfil(entidade.getPerfil());
        historico.setAtivo(entidade.isAtivo());
        preencher(historico, usuario, tipo);
        funcionarioDAO.inserir(historico);
    }

    @Transactional
    public void registrar(Produto entidade, Funcionario usuario, String tipo) {
        HistoricoProduto historico = new HistoricoProduto();
        historico.setProdutoId(entidade.getId());
        historico.setNome(entidade.getNome());
        historico.setCategoriaId(entidade.getCategoria().getId());
        historico.setTamanho(entidade.getTamanho());
        historico.setCor(entidade.getCor());
        historico.setCodigoBarras(entidade.getCodigoBarras());
        historico.setPrecoVenda(entidade.getPrecoVenda());
        historico.setEstoqueAtual(entidade.getEstoqueAtual());
        historico.setFornecedorId(entidade.getFornecedorId());
        historico.setAtivo(entidade.isAtivo());
        preencher(historico, usuario, tipo);
        produtoDAO.inserir(historico);
    }

    private void preencher(HistoricoCliente historico, Funcionario usuario, String tipo) {
        historico.setTimestampAlteracao(LocalDateTime.now());
        historico.setTipoAtualizacao(tipo);
        historico.setUsuarioId(usuario.getId());
    }

    private void preencher(HistoricoFornecedor historico, Funcionario usuario, String tipo) {
        historico.setTimestampAlteracao(LocalDateTime.now());
        historico.setTipoAtualizacao(tipo);
        historico.setUsuarioId(usuario.getId());
    }

    private void preencher(HistoricoFuncionario historico, Funcionario usuario, String tipo) {
        historico.setTimestampAlteracao(LocalDateTime.now());
        historico.setTipoAtualizacao(tipo);
        historico.setUsuarioId(usuario.getId());
    }

    private void preencher(HistoricoProduto historico, Funcionario usuario, String tipo) {
        historico.setTimestampAlteracao(LocalDateTime.now());
        historico.setTipoAtualizacao(tipo);
        historico.setUsuarioId(usuario.getId());
    }
}
