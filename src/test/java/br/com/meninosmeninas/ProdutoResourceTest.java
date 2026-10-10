package br.com.meninosmeninas;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class ProdutoResourceTest {

    @Test
    void listaCategoriasEProdutosComFiltroOpcional() {
        given()
                .when().get("/api/categorias")
                .then()
                .statusCode(200)
                .body("", hasSize(2));

        given()
                .when().get("/api/produtos")
                .then()
                .statusCode(200)
                .body("", hasSize(3));

        given()
                .queryParam("categoriaId", 2)
                .when().get("/api/produtos")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].nome", equalTo("Boneca de Pano"));
    }

    @Test
    void cadastraProdutoEAtualizaQuantidadeInclusiveParaZero() {
        int idProduto = given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto de teste REST",
                          "categoriaId": 1,
                          "quantidade": 0,
                          "preco": 12.50
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(201)
                .body("quantidade", equalTo(0))
                .extract()
                .path("id");

        given()
                .contentType("application/json")
                .body("""
                        { "quantidade": 0 }
                        """)
                .when().patch("/api/produtos/{id}/quantidade", idProduto)
                .then()
                .statusCode(200)
                .body("id", equalTo(idProduto))
                .body("quantidade", equalTo(0));
    }

    @Test
    void rejeitaDadosInvalidosECategoriaInexistente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Produto inválido",
                          "categoriaId": 999,
                          "quantidade": 1,
                          "preco": 10.00
                        }
                        """)
                .when().post("/api/produtos")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body("{ \"quantidade\": -1 }")
                .when().patch("/api/produtos/1/quantidade")
                .then()
                .statusCode(400);
    }

    @Test
    void retorna404AoAtualizarProdutoInexistente() {
        given()
                .contentType("application/json")
                .body("{ \"quantidade\": 4 }")
                .when().patch("/api/produtos/99999/quantidade")
                .then()
                .statusCode(404);
    }

    @Test
    void permiteOrigemLocalDoFrontendNoCors() {
        given()
                .header("Origin", "http://localhost:5173")
                .when().get("/api/produtos")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", equalTo("http://localhost:5173"));
    }

        @Test
        void permitePreflightParaAtualizarQuantidade() {
                given()
                                .header("Origin", "http://localhost:5173")
                                .header("Access-Control-Request-Method", "PATCH")
                                .header("Access-Control-Request-Headers", "content-type")
                                .when().options("/api/produtos/1/quantidade")
                                .then()
                                .statusCode(200)
                                .header("Access-Control-Allow-Origin", equalTo("http://localhost:5173"))
                                .header("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH"));
        }
}
