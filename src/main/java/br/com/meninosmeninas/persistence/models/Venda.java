package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "venda")
@Getter @Setter @NoArgsConstructor
public class Venda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "funcionario_id") private Funcionario funcionario;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cliente_id") private Cliente cliente;
    @Column(name = "data_venda", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") private LocalDateTime dataVenda;
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2) private BigDecimal valorTotal;
}
