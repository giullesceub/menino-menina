package br.com.meninosmeninas.rest.dto;

public record FornecedorRequest(String razaoSocial, String nomeFantasia, String cnpj,
                                String telefone, String email, String login, String senha) {
}
