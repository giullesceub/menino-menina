# Menino Menina API

API do protótipo do módulo de produtos, feita com Java 21, Quarkus, Maven e H2 em memória.

## Executar localmente

Requisitos: Java 21.

```bash
./mvnw quarkus:dev
```

No Windows PowerShell, use `mvnw.cmd quarkus:dev`.

A API ficará em `http://localhost:8080`. O banco H2 é recriado ao iniciar e recebe os dados de exemplo de `src/main/resources/import.sql`; alterações feitas durante a demonstração não persistem após reiniciar.

## Contrato HTTP para o frontend

A API recebe e retorna JSON. Em desenvolvimento, configure a URL base do React como `http://localhost:8080` (por exemplo, em `VITE_API_URL`).

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

## Publicação gratuita

O `Dockerfile` da raiz compila e executa a API em JVM. Para publicar no Render, conecte o repositório e crie um **Web Service** usando Docker; se o repositório contiver uma pasta `menino-menina`, configure-a como **Root Directory**. O serviço usa a porta informada pelo host (`PORT`).

Configure a variável de ambiente `CORS_ORIGINS` com a origem exata do site React publicado, sem barra final. Exemplo: `https://meu-prototipo.onrender.com`. Para desenvolvimento local, o padrão já permite `http://localhost:5173`.

Serviços gratuitos podem suspender a API quando ficam ociosos. A primeira chamada depois da suspensão pode demorar aproximadamente um minuto; a interface deve exibir um estado de carregamento. Como o banco é H2 em memória, reinícios restauram os dados de exemplo.
