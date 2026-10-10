package br.com.meninosmeninas.persistence.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Produto")
@NamedQueries({
    @NamedQuery(
        name = "Produto.listarTodos",
        query = "SELECT p FROM Produto p JOIN FETCH p.categoria ORDER BY p.id"
    ),
    @NamedQuery(
        name = "Produto.listarPorCategoria",
        query = "SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.categoria.id = :idCategoria ORDER BY p.id"
    )
})
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idCategoria", nullable = false)
    private TipoCategoriaProduto categoria;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "preco", nullable = false)
    private Double preco;
}