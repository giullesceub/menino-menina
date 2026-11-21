package br.com.meninosmeninas.rest.dto;

import br.com.meninosmeninas.service.Credenciais;

public record CredenciaisRequest(String login, String senha) {
    public Credenciais toCredentials() {
        return new Credenciais(login, senha);
    }
}
