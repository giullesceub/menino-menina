package br.com.meninosmeninas.service;

import br.com.meninosmeninas.persistence.dao.ProdutoDAO;
import br.com.meninosmeninas.persistence.dao.TipoCategoriaProdutoDAO;
import br.com.meninosmeninas.persistence.models.Produto;
import br.com.meninosmeninas.persistence.models.Categoria;
import br.com.meninosmeninas.persistence.models.TipoCategoriaProduto;
import br.com.meninosmeninas.persistence.models.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoDAO produtoDAO;

    @Inject
    TipoCategoriaProdutoDAO categoriaDAO;

    @Inject
    AutenticacaoService autenticacaoService;
    @Inject
    HistoricoService historicoService;

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    @Transactional
    public Produto alterarAtivo(Credenciais credenciais, Long id, boolean ativo) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null) throw new RegraNegocioException("Produto não encontrado.");
        if (!ativo && produto.getEstoqueAtual() != 0) {
            throw new RegraNegocioException("O produto só pode ser desativado com estoque zero.");
        }
        produto.setAtivo(ativo);
        Produto salvo = produtoDAO.atualizar(produto);
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    public List<Produto> listarPorCategoria(Integer idCategoria) {
        if (idCategoria == null) {
            return produtoDAO.listarTodos();
        }
        TipoCategoriaProduto categoria = categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null) {
            return List.of();
        }
        return produtoDAO.listarPorCategoria(categoria.getNome());
    }

    private Produto criar(String nome, int idCategoria, int quantidade, BigDecimal preco) {
        TipoCategoriaProduto categoria = categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null) {
            throw new RegraNegocioException("A categoria informada não existe.");
        }
        if (nome == null || nome.isBlank() || quantidade < 0 || preco == null || preco.signum() < 0) {
            throw new RegraNegocioException("Dados do produto inválidos.");
        }
        Produto produto = new Produto();
        produto.setNome(nome.trim());
        Categoria categoriaEntity = new Categoria();
        categoriaEntity.setId((long) categoria.getId());
        categoriaEntity.setNome(categoria.getNome());
        produto.setCategoria(categoriaEntity);
        produto.setEstoqueAtual(quantidade);
        produto.setPrecoVenda(preco);
        return produto;
    }

    @Transactional
    public Produto inserir(Produto produto) {
        validarValoresProduto(produto);
        normalizar(produto);
        if (produto.getFornecedor() != null && !produto.getFornecedor().isAtivo()) {
            throw new RegraNegocioException("Fornecedor inativo não pode ser associado ao produto.");
        }
        return produtoDAO.inserir(produto);
    }

    public Produto inserir(Credenciais credenciais, String nome, int idCategoria,
                           int quantidade, BigDecimal preco) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Produto produto = criar(nome, idCategoria, quantidade, preco);
        Produto salvo = inserir(produto);
        historicoService.registrar(salvo, usuario, "I");
        return salvo;
    }

    public Produto inserir(String nome, int idCategoria, int quantidade, BigDecimal preco) {
        TipoCategoriaProduto categoria = categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null || quantidade < 0 || preco == null || preco.signum() < 0) {
            throw new RegraNegocioException("Categoria ou valores do produto inválidos.");
        }

        Produto produto = new Produto();
        produto.setNome(nome.trim());
        Categoria categoriaEntity = new Categoria();
        categoriaEntity.setId((long) categoria.getId());
        categoriaEntity.setNome(categoria.getNome());
        produto.setCategoria(categoriaEntity);
        produto.setEstoqueAtual(quantidade);
        produto.setPrecoVenda(preco);
        return produtoDAO.inserir(produto);
    }

    public Produto atualizar(Produto produto) {
        validarValoresProduto(produto);
        return produtoDAO.atualizar(produto);
    }

    public Produto atualizarQuantidade(int idProduto, int novaQuantidade) {
        if (novaQuantidade < 0) {
            return null;
        }
        return produtoDAO.atualizarQuantidade(idProduto, novaQuantidade);
    }

    public Produto atualizarQuantidade(Credenciais credenciais, int idProduto, int novaQuantidade) {
        Funcionario usuario = autenticacaoService.exigirProprietaria(credenciais);
        Produto salvo = atualizarQuantidade(idProduto, novaQuantidade);
        if (salvo == null) return null;
        historicoService.registrar(salvo, usuario, "A");
        return salvo;
    }

    public Produto atualizar(Credenciais credenciais, Produto produto) {
        Funcionario funcionario = autenticacaoService.autenticar(credenciais);
        if (funcionario.getPerfil() == Funcionario.Perfil.VENDEDOR) {
            Produto atual = buscarPorId(produto == null || produto.getId() == null
                    ? 0 : produto.getId().intValue());
            if (atual == null || produto.getNome() == null || produto.getCategoria() == null) {
                throw new RegraNegocioException("Produto não encontrado ou dados inválidos.");
            }
            atual.setNome(produto.getNome());
            atual.setCategoria(produto.getCategoria());
            Produto salvo = atualizar(atual);
            historicoService.registrar(salvo, funcionario, "A");
            return salvo;
        }
        validarValoresProduto(produto);
        normalizar(produto);
        Produto salvo = produtoDAO.atualizar(produto);
        historicoService.registrar(salvo, funcionario, "A");
        return salvo;
    }

    public Produto buscarPorId(int idProduto) {
        return produtoDAO.buscarPorId(idProduto);
    }

    private void validarValoresProduto(Produto produto) {
        if (produto == null || produto.getEstoqueAtual() < 0
                || produto.getNome() == null || produto.getNome().isBlank()
                || produto.getCategoria() == null
                || produto.getPrecoVenda() == null
                || produto.getPrecoVenda().signum() < 0) {
            throw new IllegalArgumentException("Produto contém valores inválidos.");
        }
    }

    private void normalizar(Produto produto) {
        produto.setCodigoBarras(DadosCadastrais.codigoBarrasOpcional(produto.getCodigoBarras()));
    }
}
