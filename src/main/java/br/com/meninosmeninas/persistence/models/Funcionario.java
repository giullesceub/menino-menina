package br.com.meninosmeninas.persistence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "funcionario")
@Getter @Setter @NoArgsConstructor
public class Funcionario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120) private String nome;
    @Column(length = 14, unique = true) private String cpf;
    @Column(length = 20) private String telefone;
    @Column(nullable = false, length = 50, unique = true) private String login;
    @Column(name = "senha_hash", nullable = false, length = 255) private String senhaHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Perfil perfil;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") private boolean ativo = true;

    public enum Perfil { PROPRIETARIA, VENDEDOR }
}
