package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historico_produto")
@IdClass(HistoricoProduto.Chave.class)
@Getter @Setter @NoArgsConstructor
public class HistoricoProduto {
    @Id @Column(name = "produto_id") private Long produtoId;
    @Id @Column(name = "timestamp_alteracao") private LocalDateTime timestampAlteracao;
    @Column(nullable = false, length = 150) private String nome;
    @Column(name = "categoria_id", nullable = false) private Long categoriaId;
    @Column(length = 20) private String tamanho;
    @Column(length = 30) private String cor;
    @Column(name = "codigo_barras", length = 50) private String codigoBarras;
    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2) private BigDecimal precoVenda;
    @Column(name = "estoque_atual", nullable = false) private int estoqueAtual;
    @Column(name = "fornecedor_id") private Long fornecedorId;
    @Column(nullable = false) private boolean ativo;
    @Column(name = "tipo_atualizacao", nullable = false, length = 1) private String tipoAtualizacao;
    @Column(name = "usuario_id", nullable = false) private Long usuarioId;

    @Getter @Setter @NoArgsConstructor @EqualsAndHashCode
    public static class Chave {
        private Long produtoId;
        private LocalDateTime timestampAlteracao;
    }
}
