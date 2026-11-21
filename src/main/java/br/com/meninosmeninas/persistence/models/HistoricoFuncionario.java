package br.com.meninosmeninas.persistence.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historico_funcionario")
@IdClass(HistoricoFuncionario.Chave.class)
@Getter @Setter @NoArgsConstructor
public class HistoricoFuncionario {
    @Id @Column(name = "funcionario_id") private Long funcionarioId;
    @Id @Column(name = "timestamp_alteracao") private LocalDateTime timestampAlteracao;
    @Column(nullable = false, length = 120) private String nome;
    @Column(length = 14) private String cpf;
    @Column(length = 20) private String telefone;
    @Column(nullable = false, length = 50) private String login;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Funcionario.Perfil perfil;
    @Column(nullable = false) private boolean ativo;
    @Column(name = "tipo_atualizacao", nullable = false, length = 1) private String tipoAtualizacao;
    @Column(name = "usuario_id", nullable = false) private Long usuarioId;

    @Getter @Setter @NoArgsConstructor @EqualsAndHashCode
    public static class Chave {
        private Long funcionarioId;
        private LocalDateTime timestampAlteracao;
    }
}
