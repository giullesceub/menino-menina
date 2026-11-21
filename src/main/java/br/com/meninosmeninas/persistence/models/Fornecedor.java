package br.com.meninosmeninas.persistence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fornecedor")
@Getter @Setter @NoArgsConstructor
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "razao_social", nullable = false, length = 150) private String razaoSocial;
    @Column(name = "nome_fantasia", length = 150) private String nomeFantasia;
    @Column(length = 18, unique = true) private String cnpj;
    @Column(length = 20) private String telefone;
    @Column(length = 120) private String email;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") private boolean ativo = true;
}
