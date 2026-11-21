package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.FuncionarioDAO;
import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Objects;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AutenticacaoService {
    @Inject
    FuncionarioDAO funcionarioDAO;

    public Funcionario autenticar(Credenciais credenciais) {
        if (credenciais == null || credenciais.login() == null || credenciais.senha() == null) {
            throw new RegraNegocioException("Login e senha são obrigatórios.");
        }

        Funcionario funcionario = funcionarioDAO.buscarPorLogin(credenciais.login().trim())
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas."));
        if (!funcionario.isAtivo()
                || !BCrypt.checkpw(credenciais.senha(), funcionario.getSenhaHash())) {
            throw new RegraNegocioException("Credenciais inválidas.");
        }
        return funcionario;
    }

    public Funcionario exigirPerfil(Credenciais credenciais, Funcionario.Perfil perfil) {
        Funcionario funcionario = autenticar(credenciais);
        if (!Objects.equals(funcionario.getPerfil(), perfil)) {
            throw new RegraNegocioException("Funcionário sem permissão para esta operação.");
        }
        return funcionario;
    }

    public Funcionario exigirProprietaria(Credenciais credenciais) {
        return exigirPerfil(credenciais, Funcionario.Perfil.PROPRIETARIA);
    }
}
