package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(length = 20)
    private String tamanho;

    @Column(length = 30)
    private String cor;

    @Column(name = "codigo_barras", length = 50, unique = true)
    private String codigoBarras;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "estoque_atual", nullable = false)
    private int estoqueAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;

    public int getIdInt() {
        return id == null ? 0 : id.intValue();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = (long) id;
    }

    public int getQuantidade() {
        return estoqueAtual;
    }

    public void setQuantidade(int quantidade) {
        this.estoqueAtual = quantidade;
    }

    public String getCategoriaNome() {
        return categoria == null ? null : categoria.getNome();
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Mantido temporariamente para o contrato REST legado.
     * O modelo persistente usa BigDecimal em precoVenda.
     */
    @Deprecated
    public Double getPreco() {
        return precoVenda == null ? null : precoVenda.doubleValue();
    }

    /**
     * Mantido temporariamente para o contrato REST legado.
     * O modelo persistente usa BigDecimal em precoVenda.
     */
    @Deprecated
    public void setPreco(Double preco) {
        this.precoVenda = preco == null ? null : BigDecimal.valueOf(preco);
    }

    public Long getFornecedorId() {
        return fornecedor == null ? null : fornecedor.getId();
    }

    public void setFornecedorId(Long fornecedorId) {
        if (fornecedorId == null) {
            fornecedor = null;
            return;
        }
        fornecedor = new Fornecedor();
        fornecedor.setId(fornecedorId);
    }
}
