package br.com.meninosmeninas.persistence.models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pagamento_venda")
@Getter @Setter @NoArgsConstructor
public class PagamentoVenda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "venda_id") private Venda venda;
    @Enumerated(EnumType.STRING) @Column(name = "forma_pagamento", nullable = false, length = 25) private FormaPagamento formaPagamento;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal valor;

    public enum FormaPagamento { DINHEIRO, PIX, CARTAO_DEBITO, CARTAO_CREDITO }
}
