package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.ClienteDAO;
import br.com.meninosmeninas.persistence.models.Cliente;
import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ClienteService {
    @Inject ClienteDAO clienteDAO;
    @Inject AutenticacaoService autenticacaoService;
    @Inject HistoricoService historicoService;

    public List<Cliente> listar() { return clienteDAO.listarTodos(); }
    public Cliente buscar(Long id) { return clienteDAO.buscarPorId(id); }

    @Transactional
    public Cliente cadastrar(Credenciais credenciais, Cliente cliente) {
        Funcionario usuario = autenticacaoService.autenticar(credenciais);
        validar(cliente);
        cliente.setCpf(DadosCadastrais.cpfOpcional(cliente.getCpf()));
        cliente.setTelefone(DadosCadastrais.telefoneOpcional(cliente.getTelefone()));
        Cliente salvo = clienteDAO.inserir(cliente);
        historicoService.registrar(salvo, usuario, "I");
        return salvo;
    }

    @Transactional
    public Cliente atualizar(Credenciais credenciais, Cliente cliente) {
        Funcionario usuario = autenticacaoService.autenticar(credenciais);
        validar(cliente);
        cliente.setCpf(DadosCadastrais.cpfOpcional(cliente.getCpf()));
        cliente.setTelefone(DadosCadastrais.telefoneOpcional(cliente.getTelefone()));
        Cliente existente = cliente.getId() == null ? null : clienteDAO.buscarPorId(cliente.getId());
        if (existente == null) {
            throw new RegraNegocioException("Cliente não encontrado.");
        }
        cliente.setAtivo(existente.isAtivo());
        Cliente salvo = clienteDAO.atualizar(cliente);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    @Transactional
    public Cliente alterarAtivo(Credenciais credenciais, Long id, boolean ativo) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) throw new RegraNegocioException("Cliente não encontrado.");
        cliente.setAtivo(ativo);
        Cliente salvo = clienteDAO.atualizar(cliente);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    private void validar(Cliente cliente) {
        if (cliente == null || cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RegraNegocioException("Nome do cliente é obrigatório.");
        }
    }
}
