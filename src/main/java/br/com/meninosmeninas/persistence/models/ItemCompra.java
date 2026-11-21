package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_compra")
@Getter @Setter @NoArgsConstructor
public class ItemCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "compra_id") private Compra compra;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "produto_id") private Produto produto;
    @Column(nullable = false) private int quantidade;
    @Column(name = "custo_unitario", nullable = false, precision = 10, scale = 2) private BigDecimal custoUnitario;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal subtotal;
}
