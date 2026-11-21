package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.FuncionarioDAO;
import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class FuncionarioService {
    @Inject FuncionarioDAO funcionarioDAO;
    @Inject AutenticacaoService autenticacaoService;
    @Inject HistoricoService historicoService;

    public List<Funcionario> listar() { return funcionarioDAO.listarTodos(); }
    public Funcionario buscar(Long id) { return funcionarioDAO.buscarPorId(id); }

    @Transactional
    public Funcionario cadastrar(Credenciais credenciais, Funcionario funcionario, String senha) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        if (funcionario == null || funcionario.getLogin() == null || senha == null || senha.isBlank()
                || funcionario.getPerfil() == null || funcionario.getNome() == null
                || funcionario.getNome().isBlank()) {
            throw new RegraNegocioException("Dados do funcionário são inválidos.");
        }
        String loginNormalizado = LoginNormalizer.normalizar(funcionario.getLogin());
        if (loginNormalizado.isBlank()) {
            throw new RegraNegocioException("Login inválido.");
        }
        if (funcionarioDAO.buscarPorLogin(loginNormalizado).isPresent()) {
            throw new RegraNegocioException("Login já cadastrado.");
        }

        funcionario.setLogin(loginNormalizado);
        funcionario.setCpf(DadosCadastrais.cpfOpcional(funcionario.getCpf()));
        funcionario.setTelefone(DadosCadastrais.telefoneOpcional(funcionario.getTelefone()));
        funcionario.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt(10)));
        funcionario.setAtivo(true);
        Funcionario salvo = funcionarioDAO.inserir(funcionario);
        historicoService.registrar(salvo, usuario, "I");
        return salvo;
    }

    @Transactional
    public Funcionario atualizarCadastro(Long id, AtualizarCadastroFuncionario dados) {
        Funcionario operador = autenticacaoService.autenticar(
                new Credenciais(dados.login(), dados.senha()));
        Funcionario existente = funcionarioDAO.buscarPorId(id);
        if (existente == null) {
            throw new RegraNegocioException("Funcionário não encontrado.");
        }

        boolean alteraProprioCadastro = existente.getId().equals(operador.getId());
        if (!alteraProprioCadastro && operador.getPerfil() != Funcionario.Perfil.PROPRIETARIA) {
            throw new RegraNegocioException("Funcionário sem permissão para esta operação.");
        }

        if (dados.nome() == null || dados.nome().trim().isBlank()) {
            throw new RegraNegocioException("Nome do funcionário é obrigatório.");
        }

        existente.setNome(dados.nome().trim().replaceAll("\\s+", " "));
        existente.setTelefone(DadosCadastrais.telefoneOpcional(dados.telefone()));

        if (dados.novaSenha() != null && !dados.novaSenha().isBlank()) {
            existente.setSenhaHash(BCrypt.hashpw(dados.novaSenha(), BCrypt.gensalt(10)));
        }

        // Login, CPF, perfil e ativo são deliberadamente preservados neste endpoint.
        Funcionario salvo = funcionarioDAO.atualizar(existente);
        historicoService.registrar(salvo, operador, "A");
        return salvo;
    }

    public record AtualizarCadastroFuncionario(String nome, String telefone, String novaSenha,
                                               String login, String senha) {
    }

    @Transactional
    public Funcionario desativar(Credenciais credenciais, Long id) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Funcionario funcionario = funcionarioDAO.buscarPorId(id);
        if (funcionario == null) {
            throw new RegraNegocioException("Funcionário não encontrado.");
        }
        if (funcionario.getId().equals(autenticacaoService.autenticar(credenciais).getId())) {
            throw new RegraNegocioException("A própria conta não pode ser desativada.");
        }
        if (funcionario.getPerfil() == Funcionario.Perfil.PROPRIETARIA
                && funcionarioDAO.listarTodos().stream()
                .filter(f -> f.getPerfil() == Funcionario.Perfil.PROPRIETARIA && f.isAtivo()).count() <= 1) {
            throw new RegraNegocioException("A última proprietária ativa não pode ser desativada.");
        }
        funcionario.setAtivo(false);
        Funcionario salvo = funcionarioDAO.atualizar(funcionario);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    @Transactional
    public Funcionario reativar(Credenciais credenciais, Long id) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Funcionario funcionario = funcionarioDAO.buscarPorId(id);
        if (funcionario == null) throw new RegraNegocioException("Funcionário não encontrado.");
        funcionario.setAtivo(true);
        Funcionario salvo = funcionarioDAO.atualizar(funcionario);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }
}
