package br.com.meninosmeninas;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BusinessFlowResourceTest {

    @Test
    void rejeitaOperacaoAdministrativaComCredencialDeVendedor() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "valor": 10.00,
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(403);
    }

    @Test
    void executaCompraVendaCaixaEConsultaHistorico() {
        int produtoId = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto fluxo completo",
                          "categoriaId": 1,
                          "quantidade": 0,
                          "preco": 25.00,
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {
                          "fornecedorId": 1,
                          "itens": [
                            {"produtoId": %d, "quantidade": 3, "custoUnitario": 10.00}
                          ],
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """.formatted(produtoId))
                .when().post("/api/compras")
                .then()
                .statusCode(201)
                .body("valorTotal", equalTo(30.0f));

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 20.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": null,
                          "itens": [{"produtoId": %d, "quantidade": 2}],
                          "pagamentos": [{"formaPagamento": "DINHEIRO", "valor": 50.00}],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """.formatted(produtoId))
                .when().post("/api/vendas")
                .then()
                .statusCode(201)
                .body("valorTotal", equalTo(50.0f));

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 70.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/fechar")
                .then()
                .statusCode(200)
                .body("diferenca", equalTo(0.0f))
                .body("situacao", equalTo("VALOR CONFERIDO"));

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/historico/produtos/{id}", produtoId)
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(2)))
                .body("[0].tipoAtualizacao", equalTo("I"))
                .body("[1].tipoAtualizacao", equalTo("A"));
    }

    @Test
    void rejeitaPagamentoComSomaDiferenteDoTotal() {
        int produtoId = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto pagamento inválido",
                          "categoriaId": 1,
                          "quantidade": 1,
                          "preco": 20.00,
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": null,
                          "itens": [{"produtoId": %d, "quantidade": 1}],
                          "pagamentos": [{"formaPagamento": "PIX", "valor": 19.99}],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """.formatted(produtoId))
                .when().post("/api/vendas")
                .then()
                .statusCode(400);
    }

    @Test
    void rejeitaCredencialInvalidaEOperacoesDeCaixaInconsistentes() {
        given()
                .contentType("application/json")
                .body("""
                        {"valor": 10.00, "login": "sandra", "senha": "errada"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(401);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 10.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 1.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 11.00, "descricao": "Retirada inválida", "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/retirada")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 1.00, "descricao": "Troco", "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/entrada")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 11.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/fechar")
                .then()
                .statusCode(200)
                .body("diferenca", equalTo(0.0f))
                .body("situacao", equalTo("VALOR CONFERIDO"));
    }

    @Test
    void bloqueiaCompraPorVendedorEProdutoInativo() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "fornecedorId": 1,
                          "itens": [{"produtoId": 1, "quantidade": 1, "custoUnitario": 10.00}],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/compras")
                .then()
                .statusCode(403);

        int produtoId = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto inativo para teste",
                          "categoriaId": 1,
                          "quantidade": 0,
                          "preco": 15.00,
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/produtos/{id}/desativar", produtoId)
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": null,
                          "itens": [{"produtoId": %d, "quantidade": 1}],
                          "pagamentos": [{"formaPagamento": "PIX", "valor": 15.00}],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """.formatted(produtoId))
                .when().post("/api/vendas")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/produtos/{id}/ativar", produtoId)
                .then()
                .statusCode(200);
    }

    @Test
    void protegeContaDaProprietariaEConsultaDeHistorico() {
        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/funcionarios/1/desativar")
                .then()
                .statusCode(400);

        int produtoId = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto para consulta histórica",
                          "categoriaId": 1,
                          "quantidade": 0,
                          "preco": 18.00,
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {"login": "fernanda", "senha": "123456"}
                        """)
                .when().post("/api/historico/produtos/{id}", produtoId)
                .then()
                .statusCode(403);

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/historico/produtos/{id}", produtoId)
                .then()
                .statusCode(200)
                .body("", not(empty()));
    }

    @Test
    void aceitaMultiplosPagamentosERejeitaEstoqueInsuficiente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": null,
                          "itens": [{"produtoId": 2, "quantidade": 1}],
                          "pagamentos": [
                            {"formaPagamento": "PIX", "valor": 40.00},
                            {"formaPagamento": "CARTAO_CREDITO", "valor": 49.90}
                          ],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/vendas")
                .then()
                .statusCode(201)
                .body("valorTotal", equalTo(89.90f));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": null,
                          "itens": [{"produtoId": 2, "quantidade": 1000}],
                          "pagamentos": [{"formaPagamento": "PIX", "valor": 89900.00}],
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/vendas")
                .then()
                .statusCode(400);
    }

    @Test
    void bloqueiaCompraDeFornecedorInativoEPermiteReativacao() {
        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/fornecedores/1/desativar")
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body("""
                        {
                          "fornecedorId": 1,
                          "itens": [{"produtoId": 1, "quantidade": 1, "custoUnitario": 10.00}],
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().post("/api/compras")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/fornecedores/1/ativar")
                .then()
                .statusCode(200);
    }

    @Test
    void informaSobraEFaltaNoFechamentoDoCaixa() {
        given()
                .contentType("application/json")
                .body("""
                        {"valor": 5.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 7.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/fechar")
                .then()
                .statusCode(200)
                .body("diferenca", equalTo(2.0f))
                .body("situacao", equalTo("SOBRA DE CAIXA"));

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 5.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 3.00, "login": "sandra", "senha": "123456"}
                        """)
                .when().post("/api/caixa/fechar")
                .then()
                .statusCode(200)
                .body("diferenca", equalTo(-2.0f))
                .body("situacao", equalTo("FALTA DE CAIXA"));
    }

    @Test
    void funcionarioInativoNaoPodeAutenticar() {
        int funcionarioId = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Vendedor temporário",
                          "cpf": null,
                          "telefone": null,
                          "login": "vendedor_temporario",
                          "senha": "123456",
                          "perfil": "VENDEDOR",
                          "operadorLogin": "sandra",
                          "operadorSenha": "123456"
                        }
                        """)
                .when().post("/api/funcionarios")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/funcionarios/{id}/desativar", funcionarioId)
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body("""
                        {"valor": 10.00, "login": "vendedor_temporario", "senha": "123456"}
                        """)
                .when().post("/api/caixa/abrir")
                .then()
                .statusCode(401);

        given()
                .contentType("application/json")
                .body("""
                        {"login": "sandra", "senha": "123456"}
                        """)
                .when().patch("/api/funcionarios/{id}/ativar", funcionarioId)
                .then()
                .statusCode(200);
    }

    @Test
    void permiteFuncionarioAlterarProprioCadastroSemAlterarDadosImutaveis() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": " Fernanda Atualizada ",
                          "telefone": " 61999990030 ",
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().put("/api/funcionarios/3")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Fernanda Atualizada"))
                .body("telefone", equalTo("61999990030"))
                .body("login", equalTo("fernanda"))
                .body("cpf", equalTo("12345678909"))
                .body("perfil", equalTo("VENDEDOR"));
    }

    @Test
    void proprietariaPodeResetarSenhaDeOutroFuncionario() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Carla Mendes",
                          "telefone": "61999990004",
                          "novaSenha": "resetada123",
                          "login": "sandra",
                          "senha": "123456"
                        }
                        """)
                .when().put("/api/funcionarios/4")
                .then()
                .statusCode(200)
                .body("login", equalTo("carla"))
                .body("cpf", equalTo("93541134780"));
    }

    @Test
    void vendedorNaoPodeAlterarCadastroDeOutroFuncionario() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Carla Atualizada",
                          "telefone": "61999990004",
                          "login": "fernanda",
                          "senha": "123456"
                        }
                        """)
                .when().put("/api/funcionarios/4")
                .then()
                .statusCode(403);
    }

    @Test
    void rejeitaCorposAusentesSemErroInterno() {
        given().contentType("application/json")
                .when().post("/api/caixa/abrir")
                .then().statusCode(400);

        given().contentType("application/json")
                .when().post("/api/compras")
                .then().statusCode(400);

        given().contentType("application/json")
                .when().post("/api/vendas")
                .then().statusCode(400);

        given().contentType("application/json")
                .when().post("/api/historico/produtos/1")
                .then().statusCode(400);

        given().contentType("application/json")
                .when().patch("/api/fornecedores/1/desativar")
                .then().statusCode(400);
    }
}
