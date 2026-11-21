package br.com.meninosmeninas.service;

import java.text.Normalizer;
import java.util.Locale;

public final class LoginNormalizer {
    private LoginNormalizer() {
    }

    public static String normalizar(String login) {
        if (login == null) {
            return "";
        }

        String semAcentos = Normalizer.normalize(login.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);

        return semAcentos.replaceAll("[^a-z0-9]", "");
    }
}
