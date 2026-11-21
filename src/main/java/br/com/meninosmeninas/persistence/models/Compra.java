package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compra")
@Getter @Setter @NoArgsConstructor
public class Compra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "fornecedor_id") private Fornecedor fornecedor;
    @Column(name = "data_compra", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") private LocalDateTime dataCompra;
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2) private BigDecimal valorTotal = BigDecimal.ZERO;
}
