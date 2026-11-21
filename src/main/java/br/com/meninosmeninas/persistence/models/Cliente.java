package br.com.meninosmeninas.persistence.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente")
@Getter @Setter @NoArgsConstructor
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String nome;
    @Column(length = 14, unique = true) private String cpf;
    @Column(length = 20) private String telefone;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") private boolean ativo = true;
}
