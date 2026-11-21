package br.com.meninosmeninas.persistence.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historico_fornecedor")
@IdClass(HistoricoFornecedor.Chave.class)
@Getter @Setter @NoArgsConstructor
public class HistoricoFornecedor {
    @Id @Column(name = "fornecedor_id") private Long fornecedorId;
    @Id @Column(name = "timestamp_alteracao") private LocalDateTime timestampAlteracao;
    @Column(name = "razao_social", nullable = false, length = 150) private String razaoSocial;
    @Column(name = "nome_fantasia", length = 150) private String nomeFantasia;
    @Column(length = 18) private String cnpj;
    @Column(length = 20) private String telefone;
    @Column(length = 120) private String email;
    @Column(nullable = false) private boolean ativo;
    @Column(name = "tipo_atualizacao", nullable = false, length = 1) private String tipoAtualizacao;
    @Column(name = "usuario_id", nullable = false) private Long usuarioId;

    @Getter @Setter @NoArgsConstructor @EqualsAndHashCode
    public static class Chave {
        private Long fornecedorId;
        private LocalDateTime timestampAlteracao;
    }
}
