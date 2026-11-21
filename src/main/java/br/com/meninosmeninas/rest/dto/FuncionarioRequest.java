package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.persistence.models.Funcionario;

public record FuncionarioRequest(String nome, String cpf, String telefone, String login,
                                 String senha, Funcionario.Perfil perfil,
                                 String operadorLogin, String operadorSenha) {
}
