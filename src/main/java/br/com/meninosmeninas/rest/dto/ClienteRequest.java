package br.com.meninosmeninas.rest.dto;

public record ClienteRequest(String nome, String cpf, String telefone, String login, String senha) {
}
