package br.com.meninosmeninas.persistence.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TipoCategoriaProduto {
    private int id;
    private String nome;

    public TipoCategoriaProduto(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static TipoCategoriaProduto porId(Integer id) {
        if (id == null) {
            return null;
        }
        return switch (id) {
            case 1 -> new TipoCategoriaProduto(1, "Roupas");
            case 2 -> new TipoCategoriaProduto(2, "Calçados");
            default -> null;
        };
    }

    public static TipoCategoriaProduto porNome(String nome) {
        if (nome == null) {
            return null;
        }
        return switch (nome) {
            case "Roupas" -> new TipoCategoriaProduto(1, "Roupas");
            case "Calçados" -> new TipoCategoriaProduto(2, "Calçados");
            default -> null;
        };
    }
}
