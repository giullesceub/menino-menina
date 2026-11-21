package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.CaixaDAO;
import br.com.meninosmeninas.persistence.dao.MovimentacaoCaixaDAO;
import br.com.meninosmeninas.persistence.models.Caixa;
import br.com.meninosmeninas.persistence.models.Funcionario;
import br.com.meninosmeninas.persistence.models.MovimentacaoCaixa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class CaixaService {
    @Inject CaixaDAO caixaDAO;
    @Inject MovimentacaoCaixaDAO movimentacaoDAO;
    @Inject AutenticacaoService autenticacaoService;

    public Caixa buscarAberto() { return caixaDAO.buscarAberto().orElse(null); }
    public List<MovimentacaoCaixa> listarMovimentacoes(Long caixaId) {
        return movimentacaoDAO.listarPorCaixa(caixaId);
    }

    @Transactional
    public Caixa abrir(Credenciais credenciais, BigDecimal valorAbertura) {
        Funcionario funcionario = autenticacaoService.exigirProprietaria(credenciais);
        exigirNaoNegativo(valorAbertura, "Valor de abertura");
        if (caixaDAO.buscarAberto().isPresent()) {
            throw new RegraNegocioException("Já existe um caixa aberto.");
        }
        Caixa caixa = new Caixa();
        caixa.setFuncionarioAbertura(funcionario);
        caixa.setValorAbertura(valorAbertura);
        return caixaDAO.inserir(caixa);
    }

    @Transactional
    public MovimentacaoCaixa registrarEntradaManual(Credenciais credenciais, BigDecimal valor,
                                                      String descricao) {
        return registrarMovimentacao(credenciais, valor, descricao,
                MovimentacaoCaixa.Tipo.ENTRADA, MovimentacaoCaixa.Categoria.ENTRADA_MANUAL);
    }

    @Transactional
    public MovimentacaoCaixa registrarRetirada(Credenciais credenciais, BigDecimal valor,
                                                String descricao) {
        return registrarMovimentacao(credenciais, valor, descricao,
                MovimentacaoCaixa.Tipo.SAIDA, MovimentacaoCaixa.Categoria.RETIRADA);
    }

    @Transactional
    public Caixa fechar(Credenciais credenciais, BigDecimal valorInformado) {
        Funcionario funcionario = autenticacaoService.exigirProprietaria(credenciais);
        exigirNaoNegativo(valorInformado, "Valor de fechamento");
        Caixa caixa = caixaDAO.buscarAberto()
                .orElseThrow(() -> new RegraNegocioException("Não existe caixa aberto."));
        BigDecimal esperado = saldoEsperado(caixa);
        caixa.setValorFechamentoEsperado(esperado);
        caixa.setValorFechamentoInformado(valorInformado);
        caixa.setFuncionarioFechamento(funcionario);
        caixa.setDataFechamento(java.time.LocalDateTime.now());
        caixa.setStatus(Caixa.Status.FECHADO);
        return caixaDAO.atualizar(caixa);
    }

    private BigDecimal saldoEsperado(Caixa caixa) {
        BigDecimal esperado = caixa.getValorAbertura();
        for (MovimentacaoCaixa movimentacao : movimentacaoDAO.listarPorCaixa(caixa.getId())) {
            esperado = movimentacao.getTipo() == MovimentacaoCaixa.Tipo.ENTRADA
                    ? esperado.add(movimentacao.getValor())
                    : esperado.subtract(movimentacao.getValor());
        }
        return esperado;
    }

    private MovimentacaoCaixa registrarMovimentacao(Credenciais credenciais, BigDecimal valor,
                                                     String descricao, MovimentacaoCaixa.Tipo tipo,
                                                     MovimentacaoCaixa.Categoria categoria) {
        Funcionario funcionario = autenticacaoService.exigirProprietaria(credenciais);
        exigirNaoNegativo(valor, "Valor da movimentação");
        if (valor.signum() <= 0) {
            throw new RegraNegocioException("O valor da movimentação deve ser maior que zero.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraNegocioException("Descrição da movimentação é obrigatória.");
        }
        Caixa caixa = caixaDAO.buscarAberto()
                .orElseThrow(() -> new RegraNegocioException("Não existe caixa aberto."));
        if (tipo == MovimentacaoCaixa.Tipo.SAIDA && valor.compareTo(saldoEsperado(caixa)) > 0) {
            throw new RegraNegocioException("A retirada não pode superar o saldo disponível.");
        }
        MovimentacaoCaixa movimentacao = new MovimentacaoCaixa();
        movimentacao.setCaixa(caixa);
        movimentacao.setFuncionario(funcionario);
        movimentacao.setTipo(tipo);
        movimentacao.setCategoria(categoria);
        movimentacao.setValor(valor);
        movimentacao.setDescricao(descricao.trim());
        return movimentacaoDAO.inserir(movimentacao);
    }

    private void exigirNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new RegraNegocioException(campo + " não pode ser negativo.");
        }
    }
}
