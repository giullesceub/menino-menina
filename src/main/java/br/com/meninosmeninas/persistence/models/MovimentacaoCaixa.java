package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movimentacao_caixa")
@Getter @Setter @NoArgsConstructor
public class MovimentacaoCaixa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "caixa_id") private Caixa caixa;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "funcionario_id") private Funcionario funcionario;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "venda_id") private Venda venda;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15) private Tipo tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 25) private Categoria categoria;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal valor;
    @Column(nullable = false, length = 255) private String descricao;
    @Column(name = "data_movimentacao", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") private LocalDateTime dataMovimentacao;

    public enum Tipo { ENTRADA, SAIDA }
    public enum Categoria { VENDA, RETIRADA, ENTRADA_MANUAL }
}
