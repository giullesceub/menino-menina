package br.com.meninosmeninas.rest.dto;

public record AtualizarFuncionarioRequest(String nome, String telefone, String novaSenha,
                                           String login, String senha) {
}
