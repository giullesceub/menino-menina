package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.FornecedorDAO;
import br.com.meninosmeninas.persistence.models.Fornecedor;
import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FornecedorService {
    @Inject FornecedorDAO fornecedorDAO;
    @Inject AutenticacaoService autenticacaoService;
    @Inject HistoricoService historicoService;

    public List<Fornecedor> listar() { return fornecedorDAO.listarTodos(); }
    public Fornecedor buscar(Long id) { return fornecedorDAO.buscarPorId(id); }

    @Transactional
    public Fornecedor cadastrar(Credenciais credenciais, Fornecedor fornecedor) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        validar(fornecedor);
        normalizar(fornecedor);
        Fornecedor salvo = fornecedorDAO.inserir(fornecedor);
        historicoService.registrar(salvo, usuario, "I");
        return salvo;
    }

    @Transactional
    public Fornecedor atualizar(Credenciais credenciais, Fornecedor fornecedor) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        validar(fornecedor);
        normalizar(fornecedor);
        Fornecedor existente = fornecedor == null || fornecedor.getId() == null
                ? null : fornecedorDAO.buscarPorId(fornecedor.getId());
        if (existente == null) {
            throw new RegraNegocioException("Fornecedor não encontrado.");
        }
        fornecedor.setAtivo(existente.isAtivo());
        Fornecedor salvo = fornecedorDAO.atualizar(fornecedor);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    @Transactional
    public Fornecedor alterarAtivo(Credenciais credenciais, Long id, boolean ativo) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Fornecedor fornecedor = fornecedorDAO.buscarPorId(id);
        if (fornecedor == null) throw new RegraNegocioException("Fornecedor não encontrado.");
        fornecedor.setAtivo(ativo);
        Fornecedor salvo = fornecedorDAO.atualizar(fornecedor);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    private void normalizar(Fornecedor fornecedor) {
        fornecedor.setCnpj(DadosCadastrais.cnpjOpcional(fornecedor.getCnpj()));
        fornecedor.setTelefone(DadosCadastrais.telefoneOpcional(fornecedor.getTelefone()));
        fornecedor.setEmail(DadosCadastrais.emailOpcional(fornecedor.getEmail()));
    }

    private void validar(Fornecedor fornecedor) {
        if (fornecedor == null || fornecedor.getRazaoSocial() == null
                || fornecedor.getRazaoSocial().isBlank()) {
            throw new RegraNegocioException("Razão social do fornecedor é obrigatória.");
        }
    }
}
