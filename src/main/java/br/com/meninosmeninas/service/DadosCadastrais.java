package br.com.meninosmeninas.service;

import java.util.regex.Pattern;

public final class DadosCadastrais {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private DadosCadastrais() {
    }

    public static String cpf(String valor) {
        String normalizado = digitos(valor, "CPF");
        if (normalizado.length() != 11 || repetido(normalizado) || !validaCpf(normalizado)) {
            throw new RegraNegocioException("CPF inválido.");
        }
        return normalizado;
    }

    public static String cpfOpcional(String valor) {
        return vazio(valor) ? null : cpf(valor);
    }

    public static String cnpj(String valor) {
        String normalizado = digitos(valor, "CNPJ");
        if (normalizado.length() != 14 || repetido(normalizado) || !validaCnpj(normalizado)) {
            throw new RegraNegocioException("CNPJ inválido.");
        }
        return normalizado;
    }

    public static String cnpjOpcional(String valor) {
        return vazio(valor) ? null : cnpj(valor);
    }

    public static String telefone(String valor) {
        String normalizado = digitos(valor, "Telefone");
        if (normalizado.length() != 10 && normalizado.length() != 11) {
            throw new RegraNegocioException("Telefone deve conter DDD e número.");
        }
        return normalizado;
    }

    public static String telefoneOpcional(String valor) {
        return vazio(valor) ? null : telefone(valor);
    }

    public static String emailOpcional(String valor) {
        if (vazio(valor)) return null;
        String normalizado = valor.trim().toLowerCase();
        if (!EMAIL.matcher(normalizado).matches()) {
            throw new RegraNegocioException("E-mail inválido.");
        }
        return normalizado;
    }

    public static String codigoBarrasOpcional(String valor) {
        if (vazio(valor)) return null;
        String normalizado = valor.trim();
        if (!normalizado.matches("\\d{1,50}")) {
            throw new RegraNegocioException("Código de barras deve conter somente dígitos.");
        }
        return normalizado;
    }

    private static String digitos(String valor, String campo) {
        if (vazio(valor)) throw new RegraNegocioException(campo + " inválido.");
        String normalizado = valor.replaceAll("\\D", "");
        if (!normalizado.equals(valor.replaceAll("[.\\-/()\\s]", ""))) {
            throw new RegraNegocioException(campo + " inválido.");
        }
        return normalizado;
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static boolean repetido(String valor) {
        return valor.chars().allMatch(c -> c == valor.charAt(0));
    }

    private static boolean validaCpf(String cpf) {
        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
        int primeiro = (soma * 10) % 11;
        if (primeiro == 10) primeiro = 0;
        if (primeiro != cpf.charAt(9) - '0') return false;
        soma = 0;
        for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
        int segundo = (soma * 10) % 11;
        if (segundo == 10) segundo = 0;
        return segundo == cpf.charAt(10) - '0';
    }

    private static boolean validaCnpj(String cnpj) {
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digito(cnpj, pesos1, 12) == cnpj.charAt(12) - '0'
                && digito(cnpj, pesos2, 13) == cnpj.charAt(13) - '0';
    }

    private static int digito(String valor, int[] pesos, int limite) {
        int soma = 0;
        for (int i = 0; i < limite; i++) soma += (valor.charAt(i) - '0') * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
