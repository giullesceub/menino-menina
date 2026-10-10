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

## Publicação gratuita da API no Render

O arquivo `render.yaml` define um serviço Docker gratuito e usa `GET /api/health` para verificar se a API iniciou.

1. Envie a branch `main` deste repositório ao GitHub.
2. No Render, escolha **New > Blueprint** e conecte este repositório.
3. Revise o serviço `menino-menina-api` detectado no `render.yaml` e aplique a configuração.
4. Quando o deploy terminar, copie a URL `https://...onrender.com` exibida no serviço e teste `https://...onrender.com/api/health`; a resposta esperada é `{"status":"ok"}`.

Para a integração do colega durante o desenvolvimento, a origem `http://localhost:5173` já é permitida por padrão, mesmo quando a API está publicada. Depois que o frontend também estiver publicado, configure no serviço Render a variável `CORS_ORIGINS` com a origem exata do site React, sem barra final, por exemplo `https://meu-prototipo.onrender.com`. Salve e aguarde o novo deploy.

Serviços gratuitos podem suspender a API quando ficam ociosos. A primeira chamada depois da suspensão pode demorar aproximadamente um minuto; a interface deve exibir um estado de carregamento. Como o banco é H2 em memória, reinícios restauram os dados de exemplo.
