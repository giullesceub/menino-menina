package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caixa")
@Getter @Setter @NoArgsConstructor
public class Caixa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "funcionario_abertura_id") private Funcionario funcionarioAbertura;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "funcionario_fechamento_id") private Funcionario funcionarioFechamento;
    @Column(name = "data_abertura", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") private LocalDateTime dataAbertura;
    @Column(name = "data_fechamento") private LocalDateTime dataFechamento;
    @Column(name = "valor_abertura", nullable = false, precision = 10, scale = 2) private BigDecimal valorAbertura = BigDecimal.ZERO;
    @Column(name = "valor_fechamento_esperado", precision = 10, scale = 2) private BigDecimal valorFechamentoEsperado;
    @Column(name = "valor_fechamento_informado", precision = 10, scale = 2) private BigDecimal valorFechamentoInformado;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15) private Status status = Status.ABERTO;

    public enum Status { ABERTO, FECHADO }
}
