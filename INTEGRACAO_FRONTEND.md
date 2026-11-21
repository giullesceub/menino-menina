# Integração do frontend com a API Menino Menina

Este documento serve como instrução para implementação do frontend e pode ser utilizado por uma inteligência artificial. Ele descreve a API atualmente disponível, os formatos JSON, autenticação, regras de negócio e comportamento esperado.

## 1. Ambiente e configuração

### URLs

- API publicada no Render: `https://menino-menina-api.onrender.com`
- Desenvolvimento local: `http://localhost:8080`
- Endpoint de disponibilidade: `GET /api/health`

Configure uma variável de ambiente no frontend:

```text
VITE_API_URL=https://menino-menina-api.onrender.com
```

Depois utilize como base:

```text
${VITE_API_URL}/api/produtos
```

Não armazene senha do banco Supabase no frontend. O frontend envia apenas login e senha do funcionário, quando solicitados pela operação.

### CORS

O backend já aceita JSON e os métodos `GET`, `POST`, `PATCH`, `PUT` e `OPTIONS`. Em produção, o Render deve possuir:

```text
CORS_ORIGINS=https://URL-REAL-DO-FRONTEND.onrender.com
```

Para desenvolvimento local:

```text
CORS_ORIGINS=http://localhost:5173
```

O CORS não é autenticação. Ele apenas permite que o navegador acesse a API a partir da origem configurada.

### Serviço gratuito suspenso

O Render gratuito pode suspender a API após período sem uso. Na primeira requisição seguinte, aguarde até aproximadamente um minuto e exiba estado de carregamento. Implemente timeout/retry somente para a primeira falha de rede temporária; não repita automaticamente operações de escrita.

## 2. Formato das requisições

Todas as requisições de JSON devem enviar:

```http
Content-Type: application/json
```

Não envie o objeto `Credenciais` em um cabeçalho de autorização. A API atual não usa JWT e não mantém sessão.

Para cada operação de escrita, envie os campos `login` e `senha` no corpo JSON quando a API exigir credenciais.

Exemplo:

```json
{
  "login": "sandra",
  "senha": "123456",
  "valor": 100
}
```

Não represente credenciais por valores vazios, `null` ou senha em localStorage sem necessidade. Para homologação acadêmica, mantenha o operador autenticado na memória do frontend. Não imprima ou registre senhas em logs.

## 3. Respostas e erros

### Respostas de sucesso

- `200 OK`: consulta ou alteração concluída.
- `201 Created`: cadastro ou operação registrada.
- `404 Not Found`: recurso inexistente em consultas ou operações específicas.

### Erro padrão de negócio

O backend retorna erros de regra de negócio como:

```json
{
  "mensagem": "Texto explicando o problema."
}
```

Status possíveis:

- `400 Bad Request`: JSON ausente, dados inválidos ou violação de regra de negócio.
- `401 Unauthorized`: login/senha ausentes ou inválidos. A mensagem começa com `Credenciais`.
- `403 Forbidden`: usuário autenticado, mas sem perfil permitido.
- `404 Not Found`: recurso inexistente em consultas específicas.
- `500 Internal Server Error`: falha inesperada; não deve ser tratada como validação normal.

O frontend deve mostrar `mensagem` ao usuário e não assumir que o erro é sempre HTTP 400.

Exemplo de tratamento:

```javascript
async function apiRequest(path, options = {}) {
  const response = await fetch(`${import.meta.env.VITE_API_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  let body = null;
  if (response.status !== 204) {
    body = await response.json().catch(() => null);
  }

  if (!response.ok) {
    throw new Error(body?.mensagem || `Erro HTTP ${response.status}`);
  }

  return body;
}
```

## 4. Perfis e autenticação

### Perfis disponíveis

- `PROPRIETARIA`
- `VENDEDOR`

### Dados de homologação

O seed fornece estas contas iniciais. Use somente no ambiente de homologação:

| Login | Senha | Perfil |
|---|---|---|
| sandra | 123456 | PROPRIETARIA |
| luiz | 123456 | PROPRIETARIA |
| fernanda | 123456 | VENDEDOR |
| carla | 123456 | VENDEDOR |

Não existe endpoint de login que devolva token. Não existe cookie de sessão nem JWT no backend. Para validar credenciais, envie `login` e `senha` junto de cada operação protegida.

O frontend pode armazenar temporariamente o login do operador em memória enquanto a página estiver aberta. A senha não deve ser armazenada em `localStorage`, `sessionStorage` ou cookies. Cada cadastro ou alteração pode abrir novamente o modal de credenciais. Essa opção evita armazenar senha, mantém o fluxo simples e garante que cada operação seja validada pelo backend.

Caso o frontend escolha guardar temporariamente a senha em estado da aplicação ou em memória durante o uso, ela poderá ser reaproveitada no modal, mas não deve permanecer após recarregar ou fechar a página. Apesar de possível, essa decisão é menos segura e não cria uma sessão no backend.

### Fluxo recomendado para escrituras

1. A tela consulta normalmente os dados existentes por um endpoint `GET`.
2. O usuário abre o formulário de cadastro ou alteração.
3. Ao tentar enviar, o frontend abre um modal/pop-up pedindo login e senha.
4. O modal não autoriza a operação por si só: ele apenas coleta credenciais.
5. O frontend envia no mesmo JSON os dados do formulário e `login`/`senha`.
6. O backend autentica o funcionário e verifica perfil/permissão.
7. Se autorizado, a operação é concluída e o frontend recarrega a lista ou o recurso alterado.
8. Se houver `401`, as credenciais estão ausentes ou incorretas. Mantenha o formulário principal e o modal abertos, limpe somente a senha do modal e permita nova tentativa.
9. Se houver `403`, o usuário existe e está autenticado, mas não possui perfil necessário. Mantenha o formulário e informe que a operação exige outro nível de acesso. Não repita automaticamente a operação.
10. Se houver `400`, corrija ou informe a mensagem de regra de negócio. Não limpe automaticamente todos os dados já preenchidos.
11. Nunca limpe o formulário de cadastro ou feche o modal por um simples erro de credencial.
12. Não reenvie automaticamente um POST ou PATCH após erro. O usuário deve confirmar novamente no modal.

O frontend pode indicar visualmente que uma ação exige proprietário ou vendedor, mas essa indicação é apenas informativa. O backend continua sendo a autoridade sobre autorização. Não implemente proteção apenas escondendo botões: chamadas diretas à API ainda serão validadas pelo backend.

### Permissões esperadas

- Consultas `GET`: abertas, exceto histórico, que exige proprietária.
- Consultas públicas: categorias, produtos, clientes, fornecedores, funcionários e compras.
- Cliente: cadastro e atualização permitem qualquer funcionário autenticado. Desativação/ativação exige `PROPRIETARIA`.
- Fornecedor: cadastro, atualização e ativação/desativação exigem `PROPRIETARIA`.
- Produto: cadastro e atualização de quantidade exigem `PROPRIETARIA`.
- Funcionário: cadastro e ativação/desativação exigem `PROPRIETARIA`. Alteração de próprio cadastro é permitida para proprietário ou vendedor. Alteração de outro funcionário exige `PROPRIETARIA`.
- Compra: exige `PROPRIETARIA`.
- Venda: exige funcionário autenticado, proprietário ou vendedor.
- Caixa: abertura, entrada manual, retirada e fechamento exigem `PROPRIETARIA`. Consulta aberta exige caixa aberto.
- Histórico: exige `PROPRIETARIA`.

## 5. Endpoints públicos de consulta

Os endpoints de consulta desta seção não exigem `login` nem `senha`. Isso vale para clientes, fornecedores, funcionários e compras: suas operações `GET` são públicas.

### Health

```http
GET /api/health
```

Resposta:

```json
{
  "status": "ok"
}
```

Use apenas para verificar disponibilidade. Não use como indicação completa de integridade do banco.

### Categorias

```http
GET /api/categorias
```

Resposta:

```json
[
  { "id": 1, "nome": "Roupas" },
  { "id": 2, "nome": "Calçados" }
]
```

### Produtos

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
    "nome": "Body Manga Curta Algodão",
    "categoriaId": 1,
    "categoriaNome": "Roupas",
    "quantidade": 15,
    "preco": 39.9
  }
]
```

Observações:

- `quantidade` é o estoque atual.
- `preco` é número decimal.
- Categoria inexistente retorna lista vazia.
- Um produto inativo pode permanecer na listagem dependendo da implementação atual; a interface não deve permitir vender ou comprar produto inativo.

### Clientes

```http
GET /api/clientes
```

Resposta real depende dos dados persistidos. Campos esperados: `id`, `nome`, `cpf`, `telefone`, `ativo`.

```http
GET /api/clientes/{id}
```

Retorna `404` quando inexistente.

### Fornecedores

```http
GET /api/fornecedores
```

Campos esperados: `id`, `razaoSocial`, `nomeFantasia`, `cnpj`, `telefone`, `email`, `ativo`.

```http
GET /api/fornecedores/{id}
```

### Funcionários

```http
GET /api/funcionarios
```

Resposta:

```json
[
  {
    "id": 1,
    "nome": "Sandra Uchikado",
    "cpf": "11144477735",
    "telefone": "61999990001",
    "login": "sandra",
    "perfil": "PROPRIETARIA",
    "ativo": true
  }
]
```

A senha nunca é retornada.

## 6. Cadastros e alterações

### Cadastrar produto

```http
POST /api/produtos
```

JSON:

```json
{
  "nome": "Meia infantil",
  "categoriaId": 1,
  "quantidade": 0,
  "preco": 12.50,
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- nome obrigatório;
- categoria existente;
- quantidade maior ou igual a zero;
- preço maior ou igual a zero;
- fornecedor não é enviado neste endpoint;
- operação exige proprietário;
- quantidade zero é válida.

Resposta: `201 Created` com `ProdutoResponse`.

Não crie produto no frontend sem passar pelas validações locais acima.

### Atualizar quantidade do produto

```http
PATCH /api/produtos/{id}/quantidade
```

JSON:

```json
{
  "quantidade": 10,
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- id positivo;
- quantidade maior ou igual a zero;
- produto existente;
- proprietário autenticado.

### Desativar produto

```http
PATCH /api/produtos/{id}/desativar
```

JSON:

```json
{
  "login": "sandra",
  "senha": "123456"
}
```

Só pode desativar produto com estoque zero. Produto inexistente retorna erro de negócio.

### Reativar produto

```http
PATCH /api/produtos/{id}/ativar
```

Mesmo JSON de credenciais. Não há histórico de senha.

### Cadastrar cliente

```http
POST /api/clientes
```

JSON:

```json
{
  "nome": "Joana Silva",
  "cpf": "39053344705",
  "telefone": "61988889999",
  "login": "luiz",
  "senha": "123456"
}
```

Validações:

- nome obrigatório;
- CPF opcional, mas válido quando informado;
- telefone opcional, mas válido quando informado;
- login/senha válidos;
- proprietário ou vendedor pode cadastrar.

Na interface:

- aceitar apenas números para CPF e telefone antes de enviar;
- exibir máscaras visualmente, mas enviar somente os dígitos ou o formato normalizado do backend;
- não bloquear o cadastro por CPF ausente.

### Atualizar cliente

```http
PUT /api/clientes/{id}
```

JSON:

```json
{
  "nome": "Joana Silva",
  "cpf": "39053344705",
  "telefone": "61988889999",
  "login": "luiz",
  "senha": "123456"
}
```

### Desativar ou reativar cliente

```http
PATCH /api/clientes/{id}/desativar
PATCH /api/clientes/{id}/ativar
```

Corpo:

```json
{
  "login": "sandra",
  "senha": "123456"
}
```

Exige proprietário.

### Cadastrar fornecedor

```http
POST /api/fornecedores
```

JSON:

```json
{
  "razaoSocial": "Fornecedor Exemplo Ltda",
  "nomeFantasia": "Fornecedor Exemplo",
  "cnpj": "11222333000181",
  "telefone": "1133334444",
  "email": "contato@exemplo.com.br",
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- razão social obrigatória;
- CNPJ opcional, mas válido quando informado;
- telefone opcional, mas válido quando informado;
- email opcional, mas válido quando informado;
- exige proprietário.

### Atualizar fornecedor

```http
PUT /api/fornecedores/{id}
```

Use os mesmos campos do cadastro, com credenciais da proprietária.

### Desativar ou reativar fornecedor

```http
PATCH /api/fornecedores/{id}/desativar
PATCH /api/fornecedores/{id}/ativar
```

Corpo com `login` e `senha` da proprietária.

### Cadastrar funcionário

```http
POST /api/funcionarios
```

JSON:

```json
{
  "nome": "Paula Santos",
  "cpf": "52998224725",
  "telefone": "61999998888",
  "login": "paula",
  "senha": "senhaNova",
  "perfil": "VENDEDOR",
  "operadorLogin": "sandra",
  "operadorSenha": "123456"
}
```

Valores válidos de `perfil`:

- `PROPRIETARIA`
- `VENDEDOR`

Validações:

- nome obrigatório;
- perfil obrigatório;
- login obrigatório;
- senha do novo funcionário obrigatória;
- CPF opcional válido;
- telefone opcional válido;
- operação exige proprietário;
- login deve ser único.

A senha de um funcionário é armazenada como hash BCrypt. Não retorna senha nem hash.

### Alterar cadastro de funcionário

```http
PUT /api/funcionarios/{id}
```

JSON:

```json
{
  "nome": "Sandra Uchikado",
  "telefone": "61999990001",
  "novaSenha": "novaSenhaOpcional",
  "login": "fernanda",
  "senha": "123456"
}
```

Regras:

- `nome` e `telefone` podem ser alterados;
- `novaSenha` é opcional. Quando vazia ou ausente, a senha atual é preservada;
- o próprio funcionário autenticado pode alterar seu próprio cadastro;
- apenas proprietário pode alterar o cadastro de outro funcionário;
- login, CPF, perfil e situação ativo são preservados neste endpoint e não devem ser enviados;
- senha nova pode ser definida pelo próprio funcionário ou por uma proprietária como reset de senha;
- vendedor não pode alterar cadastro de outro vendedor ou de um proprietário;
- telefone vazio é convertido em `null` pelo backend;
- nome vazio é rejeitado;
- todos os textos são removidos de espaços nas extremidades pelo backend.

### Desativar ou reativar funcionário

```http
PATCH /api/funcionarios/{id}/desativar
PATCH /api/funcionarios/{id}/ativar
```

JSON:

```json
{
  "login": "sandra",
  "senha": "123456"
}
```

Regras:

- usuário precisa ser proprietário;
- proprietário não pode desativar a própria conta;
- não pode desativar a última proprietária ativa.

## 7. Caixa

### Consultar caixa aberto

```http
GET /api/caixa/aberto
```

Retorna `404` se não houver caixa aberto.

### Abrir caixa

```http
POST /api/caixa/abrir
```

JSON:

```json
{
  "valor": 100.00,
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- valor não negativo;
- exige proprietário;
- não pode existir outro caixa aberto.

### Entrada manual

```http
POST /api/caixa/entrada
```

JSON:

```json
{
  "valor": 50.00,
  "descricao": "Troco adicional",
  "login": "sandra",
  "senha": "123456"
}
```

Regras:

- valor maior que zero;
- descrição obrigatória;
- exige caixa aberto;
- exige proprietário.

### Retirada

```http
POST /api/caixa/retirada
```

JSON:

```json
{
  "valor": 30.00,
  "descricao": "Pagamento de fornecedor",
  "login": "sandra",
  "senha": "123456"
}
```

Regras:

- valor maior que zero;
- descrição obrigatória;
- não pode superar o saldo disponível;
- exige caixa aberto;
- exige proprietário.

### Fechar caixa

```http
POST /api/caixa/fechar
```

JSON:

```json
{
  "valor": 145.50,
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- valor não negativo;
- exige proprietário;
- exige caixa aberto;

A resposta contém `caixa`, `diferenca` e `situacao`:

```json
{
  "caixa": {},
  "diferenca": 5.50,
  "situacao": "SOBRA DE CAIXA"
}
```

Valores possíveis de `situacao`:

- `VALOR CONFERIDO`;
- `SOBRA DE CAIXA`;
- `FALTA DE CAIXA`.

### Movimentações do caixa

```http
GET /api/caixa/{id}/movimentacoes
```

Retorna as movimentações persistidas do caixa informado.

## 8. Compras

### Consultar compras — público

```http
GET /api/compras
GET /api/compras/{id}
```

Não envie credenciais nas consultas. A listagem inclui fornecedor e itens da compra:

```json
[
  {
    "id": 1,
    "fornecedorId": 1,
    "fornecedorNome": "Confecções Pequenos Passos Ltda",
    "dataCompra": "2026-11-21T15:00:00",
    "valorTotal": 100.00,
    "itens": [
      {
        "id": 1,
        "produtoId": 1,
        "produtoNome": "Body Manga Curta Algodão",
        "quantidade": 5,
        "custoUnitario": 20.00,
        "subtotal": 100.00
      }
    ]
  }
]
```

`GET /api/compras/{id}` retorna `404` se a compra não existir.

### Registrar compra — proprietária

```http
POST /api/compras
```

JSON:

```json
{
  "fornecedorId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 5,
      "custoUnitario": 20.00
    }
  ],
  "login": "sandra",
  "senha": "123456"
}
```

Validações:

- exige proprietário;
- fornecedor ativo;
- pelo menos um item;
- quantidade positiva;
- custo unitário não negativo;
- produto existente e ativo;
- itens podem ser consolidados;
- compra não gera movimentação de caixa;
- estoque atual aumenta pela quantidade comprada;
- custo unitário não altera o preço de venda;
- o backend registra histórico do produto.

## 9. Vendas

```http
POST /api/vendas
```

JSON:

```json
{
  "clienteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ],
  "pagamentos": [
    {
      "formaPagamento": "PIX",
      "valor": 79.80
    }
  ],
  "login": "luiz",
  "senha": "123456"
}
```

### Formas de pagamento

Valores exatos aceitos:

- `DINHEIRO`
- `PIX`
- `CARTAO_DEBITO`
- `CARTAO_CREDITO`

### Validações

- login/senha válidos;
- vendedor ou proprietário;
- pelo menos um item;
- pelo menos um pagamento;
- cliente opcional, mas deve estar ativo quando informado;
- produto ativo;
- quantidade positiva;
- estoque suficiente;
- itens repetidos são consolidados pelo backend;
- soma dos pagamentos deve ser igual ao valor total da venda;
- o preço unitário usado é congelado da venda;
- estoque diminui depois da venda;
- backend registra histórico do produto;
- pagamentos em dinheiro geram movimentação de caixa;
- pagamento que não seja dinheiro não altera o caixa.

### Pagamento misto

Exemplo:

```json
{
  "clienteId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 1 }
  ],
  "pagamentos": [
    { "formaPagamento": "DINHEIRO", "valor": 20.00 },
    { "formaPagamento": "PIX", "valor": 19.90 }
  ],
  "login": "luiz",
  "senha": "123456"
}
```

A soma deve ser exatamente o valor total da venda.

Pagamento em dinheiro exige caixa aberto. Não permita concluir a venda no frontend sem explicar esse requisito. Uma venda totalmente PIX ou cartão não exige caixa aberto.

## 10. Histórico

Os endpoints abaixo exigem proprietário. O JSON usa apenas credenciais:

```http
POST /api/historico/clientes/{id}
POST /api/historico/fornecedores/{id}
POST /api/historico/funcionarios/{id}
POST /api/historico/produtos/{id}
```

Corpo:

```json
{
  "login": "sandra",
  "senha": "123456"
}
```

Cada resposta contém snapshots anteriores. Campos importantes:

- `tipoAtualizacao`: `I` para inclusão ou `A` para alteração;
- `timestampAlteracao` ou nome equivalente no objeto retornado;
- dados cadastrais snapshotados;
- `usuarioId`: funcionário responsável;
- senha nunca aparece no histórico.

## 11. Dados cadastrais normalizados

### CPF

- opcional em cliente, fornecedor não possui CPF e funcionário/produto não se aplica;
- aceito como 11 dígitos;
- o backend valida dígitos verificadores;
- a interface deve validar formato antes de enviar e mostrar erro claro;
- não usar CPF fictício/código inválido em demonstrações reais.

### CNPJ

- opcional;
- aceito como 14 dígitos;
- o backend valida dígitos verificadores;
- fornecedor pode ser cadastrado sem CNPJ.

### Telefone

- opcional;
- o backend exige DDD e número;
- usar máscara visual com pelo menos 10 ou 11 dígitos;
- não aceitar texto puro ou letras.

### E-mail

- opcional;
- validar formato básico no frontend:
  - presença de `@`;
  - domínio;
  - sem espaços.

### Código de barras

- opcional;
- somente dígitos;
- não enviar letras ou pontuação.

## 12. Regras importantes para telas

- Não permitir exclusão física de cliente, fornecedor, funcionário ou produto.
- Usar desativação/ativação.
- Para desativar produto, exigir estoque zero na interface e repetir a validação no backend.
- Não permitir venda de produto inativo.
- Não permitir compra de produto inativo.
- Não permitir vender ou comprar fornecedor/produto inativo.
- Não permitir caixa fechado para entrada manual, retirada ou recebimento de dinheiro.
- Pagamento misto deve ter soma igual ao total calculado pelo frontend e pelo backend.
- Produto não pode ter estoque ou preço negativo.
- Compra não altera caixa.
- Venda com dinheiro altera caixa quando houver caixa aberto.
- Venda sem dinheiro não exige caixa aberto.
- Última proprietária ativa não pode ser desativada.
- Proprietária não pode desativar a própria conta.
- Senhas não são retornadas pela API.

## 13. Tratamento de loading e cooldown

Nas telas:

- desabilite o botão de ação enquanto o request estiver pendente;
- use loading global ou por ação;
- não permita dois cadastros ou vendas iguais por duplo clique;
- desabilite o botão confirmar do modal enquanto a requisição estiver pendente;
- mantenha o formulário preenchido e o modal disponível após `401` ou erro de validação;
- limpe somente a senha após credencial rejeitada;
- não implemente autorização verdadeira apenas escondendo botões no frontend;
- em caso de timeout do Render, aguarde e permita nova tentativa manual;
- após venda, compra ou alteração de estoque, recarregue `GET /api/produtos`;
- após abrir, movimentar ou fechar caixa, recarregue o caixa aberto e movimentações;
- após cadastrar ou alterar cliente/fornecedor/funcionário, recarregue a lista correspondente;
- não mantenha dados críticos apenas em estado local: o backend é a fonte da verdade.

## 14. Modelo sugerido de consumo no frontend

```javascript
const API_URL = import.meta.env.VITE_API_URL;

export async function listarProdutos() {
  return apiRequest("/api/produtos");
}

export async function abrirCaixa(login, senha, valor) {
  return apiRequest("/api/caixa/abrir", {
    method: "POST",
    body: JSON.stringify({ login, senha, valor })
  });
}

export async function registrarVenda(dados, login, senha) {
  return apiRequest("/api/vendas", {
    method: "POST",
    body: JSON.stringify({ ...dados, login, senha })
  });
}
```

## 15. Checklist final para implementação

A implementação do frontend estará adequada quando:

- todas as URLs forem geradas a partir de `VITE_API_URL`;
- todas as operações protegidas enviarem `login` e `senha` no JSON;
- nenhum endpoint protegido dependa de bearer token ou cookie;
- erros exibirem `mensagem` da API;
- status `401` e `403` forem tratados separadamente;
- pagamentos forem enviados com os enums exatos;
- soma dos pagamentos usar decimal/inteiro em centavos para evitar inconsistência visual;
- listas forem recarregadas após operações de escrita;
- caixa aberto seja consultado antes de operação que envolva dinheiro;
- os perfis de proprietário e vendedor forem exibidos corretamente;
- CPF, CNPJ, telefone e código de barras sejam validados;
- dados persistidos não sejam perdidos por simples atualização do frontend;
- o frontend não permita exclusão física.
