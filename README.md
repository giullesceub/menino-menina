# Menino Menina API

API do protótipo do módulo de produtos, feita com Java 21, Quarkus, Maven e H2 em memória.

## Executar localmente

Requisitos: Java 21.

```bash
./mvnw quarkus:dev
```

No Windows PowerShell, use `mvnw.cmd quarkus:dev`.

A API local ficará em `http://localhost:8080`. A versão pública está em `https://menino-menina-api.onrender.com`. O banco H2 é recriado ao iniciar e recebe os dados de exemplo de `src/main/resources/import.sql`; alterações feitas durante a demonstração não persistem após reiniciar.

## Contrato HTTP para o frontend

A API recebe e retorna JSON. Para desenvolvimento local com o backend na mesma máquina, use `http://localhost:8080` como URL base. Para consumir a API publicada, use `https://menino-menina-api.onrender.com` (por exemplo, em `VITE_API_URL`, se o frontend usar Vite).

### Listar categorias

```http
GET /api/categorias
```

Resposta:

```json
[
  { "id": 1, "nome": "Vestuário" },
  { "id": 2, "nome": "Brinquedos" }
]
```

### Listar produtos

Sem filtro:

```http
GET /api/produtos
```

Filtrar por categoria:

```http
GET /api/produtos?categoriaId=1
```

Resposta:

```json
[
  {
    "id": 1,
    "nome": "Camiseta Básica Branca",
    "categoriaId": 1,
    "categoriaNome": "Vestuário",
    "quantidade": 50,
    "preco": 49.9
  }
]
```

### Cadastrar produto

```http
POST /api/produtos
Content-Type: application/json
```

Corpo:

```json
{
  "nome": "Meia infantil",
  "categoriaId": 1,
  "quantidade": 0,
  "preco": 12.5
}
```

Retorna `201 Created` com o produto criado. Nome não pode ser vazio, categoria precisa existir, quantidade e preço não podem ser negativos. Quantidade zero é válida.

### Atualizar somente a quantidade

```http
PATCH /api/produtos/1/quantidade
Content-Type: application/json
```

Corpo:

```json
{ "quantidade": 0 }
```

Retorna `200 OK` com o produto atualizado. Retorna `404 Not Found` se o produto não existir e `400 Bad Request` se o id ou a quantidade forem inválidos.

## Testes

```bash
./mvnw test
```

## API publicada no Render

A API está disponível em `https://menino-menina-api.onrender.com`; por exemplo, `GET https://menino-menina-api.onrender.com/api/produtos` lista os produtos. O health check configurado para o Render fica em `https://menino-menina-api.onrender.com/api/health` e responde `{"status":"ok"}`.


Serviços gratuitos podem suspender a API quando ficam ociosos. A primeira chamada depois da suspensão pode demorar aproximadamente um minuto; a interface deve exibir um estado de carregamento. Como o banco é H2 em memória, reinícios restauram os dados de exemplo.
