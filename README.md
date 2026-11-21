# Menino Menina API

API do protótipo do módulo de produtos, feita com Java 21, Quarkus, Maven, H2 em memória e PostgreSQL.

## Executar localmente

Requisitos: Java 21.

```bash
./mvnw quarkus:dev
```

No Windows PowerShell, use `mvnw.cmd quarkus:dev`.

A API local ficará em `http://localhost:8080`. No perfil `dev`, ela usa H2 em memória, sem exigir instalação de banco. O H2 é recriado ao iniciar e recebe os dados de exemplo de `src/main/resources/import.sql`; alterações locais não persistem após reiniciar.

## Ambientes e banco de dados

Os testes automatizados usam H2 em memória:

```powershell
.\mvnw.cmd clean test
```

O perfil `prod`, usado no Render, conecta ao PostgreSQL persistente do Supabase. Nesse perfil, a aplicação não recria o banco e não executa `import.sql`; o conteúdo de `src/main/resources/schema.sql` deve ser aplicado uma vez no SQL Editor do Supabase.

O datasource padrão do arquivo de configuração é PostgreSQL para que o Quarkus consiga montar o artefato de produção durante o build do Render. A URL, o usuário e a senha padrão são apenas fallbacks sem segredo; no Render, as variáveis `QUARKUS_DATASOURCE_*` substituem esses valores. Os perfis `dev` e `test` substituem o datasource por H2.

Para inserir dados iniciais no ambiente de homologação, execute uma única vez o conteúdo de `src/main/resources/supabase-seed.sql`, depois de `schema.sql`. Esse script não deve ser usado nos testes H2.

No Render, configure estas variáveis de ambiente:

```text
QUARKUS_PROFILE=prod
QUARKUS_DATASOURCE_DB_KIND=postgresql
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://HOST:PORT/postgres?sslmode=require&prepareThreshold=0
QUARKUS_DATASOURCE_USERNAME=usuario_do_banco
QUARKUS_DATASOURCE_PASSWORD=senha_do_banco
QUARKUS_DATASOURCE_JDBC_MAX_SIZE=5
CORS_ORIGINS=https://url-do-frontend
```

Não publique esses valores no GitHub. A senha do banco deve ser cadastrada apenas como variável secreta no Render.

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

O contrato completo para integração do frontend está em [`INTEGRACAO_FRONTEND.md`](./INTEGRACAO_FRONTEND.md). Para criar registros históricos `I` dos dados já existentes no Supabase, execute uma vez [`supabase-historico-carga.sql`](./supabase-historico-carga.sql).

Serviços gratuitos podem suspender a API quando ficam ociosos. A primeira chamada depois da suspensão pode demorar aproximadamente um minuto; a interface deve exibir um estado de carregamento. O ambiente publicado usa PostgreSQL no Supabase e mantém os dados entre reinícios da API.
