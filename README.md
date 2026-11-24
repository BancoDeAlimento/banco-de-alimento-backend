# Banco de Alimento - Backend

API REST do projeto **Banco de Alimento**, feita com Java e Spring Boot. Projeto final da Residência Porto Digital - SENAC com a Capgemini (3º período).

O frontend (Angular) fica em outro repositório: [banco-de-alimento-frontend](https://github.com/BancoDeAlimento/banco-de-alimento-frontend)

## Sobre o projeto

O Banco de Alimento conecta quem tem alimentos para doar com quem precisa receber. A ideia é facilitar o cadastro das doações e o controle do que está disponível, evitando desperdício.

> **A definir pelo time:** nome do projeto

### Funcionalidades

- [x] Cadastro e consulta de solicitações de alimentos por código
- [x] Cadastro público de doações
- [x] Pré-cadastro de entidades
- [x] Indicador de impacto configurável para demonstração
- [x] Rotas internas de triagem e administração (protegidas)
- [ ] Controle de estoque e distribuição de alimentos (etapa futura)

## Integrantes

| Nome | Área |
|---|---|
| Allany Dias | Frontend |
| Vinícius Santiago | Frontend |
| Mayara Marina | Frontend |
| Victoria Soares | Backend |
| Luis Gabriel | Backend |
| Filipe Gabriel | Backend |


## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 4.1.0 |
| Maven | via Maven Wrapper (`mvnw`) |
| Spring Web | incluído no Spring Boot |
| Spring Data JPA | incluído no Spring Boot |
| Bean Validation | incluído no Spring Boot |
| H2 Database | incluído no Spring Boot |

## Pré-requisitos

- Java 21 instalado. Para conferir, rode `java -version`
- Git

Não precisa instalar Maven nem banco de dados. O Maven Wrapper vem no projeto e o H2 roda dentro da aplicação.

## Como rodar

1. Clone o repositório:

   ```bash
   git clone https://github.com/BancoDeAlimento/banco-de-alimento-backend.git
   cd banco-de-alimento-backend
   ```

2. Rode a aplicação:

   ```bash
   # Linux e Mac
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

3. A API sobe em `http://localhost:8080`.

## Banco de dados

Utilizamos o **H2** no ambiente de desenvolvimento. O perfil `dev` está configurado em `src/main/resources/application-dev.properties` para persistir dados no diretório local `./data/`, que não deve ser versionado. Os dados **não são apagados automaticamente** ao encerrar a aplicação. Para os testes, aplica-se a configuração de banco específica do ambiente de teste.

## Endpoints

URL base local: `http://localhost:8080`.

### Públicos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/pedidos` | Cria solicitação de alimentos, retornando código de acompanhamento |
| GET | `/pedidos/{codigo}` | Consulta o andamento de uma solicitação pelo código |
| POST | `/doacoes` | Registra uma doação |
| POST | `/entidades` | Realiza pré-cadastro de entidade |
| GET | `/api/impacto` | Retorna o indicador de impacto configurado para o MVP |

### Internos, exigem autenticação e autorização

| Método | Rota | Descrição |
|---|---|---|
| GET | `/equipe/pedidos` | Lista e filtra solicitações |
| GET | `/equipe/pedidos/{id}` | Detalha solicitação |
| PATCH | `/equipe/pedidos/{id}` | Atualiza atendimento |
| POST | `/equipe/pedidos/{id}/cadunico/verificar` | Registra verificação administrativa |
| POST | `/equipe/usuarios` | Cadastra usuário da equipe |
| GET | `/equipe/usuarios` | Lista usuários da equipe |
| PATCH | `/equipe/usuarios/{id}/ativo` | Ativa ou desativa usuário |
| GET | `/equipe/auditoria` | Consulta auditoria |

### Tratamento de erros

A API retorna erros no formato `application/problem+json`. Erros de validação (`Bean Validation`) possuem um mapa `erros` com o nome de cada campo inválido e sua mensagem. Exemplo ilustrativo de resposta HTTP 400:

```json
{
  "status": 400,
  "detail": "Há campos inválidos na requisição.",
  "erros": {
    "nomeOuApelido": "Informe seu nome ou apelido."
  }
}
```

Os dados de CPF e NIS não são obrigatórios na criação de pedidos. A API não deve incluí-los nas respostas públicas.

### Indicador de impacto

`GET /api/impacto` usa `app.impacto.total-kg`, configurado para demonstração. O valor **não deve ser apresentado como estatística oficial** sem validação da equipe responsável.

## Como o frontend se comunica com o backend

O frontend em Angular roda em `http://localhost:4200` e chama a API em `http://localhost:8080` usando requisições HTTP (`HttpClient`). Os dados mostrados na tela vêm das respostas JSON dos endpoints acima.

O backend permite CORS para os endpoints públicos de pedidos, doações, entidades e impacto somente a partir da origem `http://localhost:4200` por padrão. É possível alterá-la com a variável `APP_CORS_ALLOWED_ORIGIN`. Os endpoints administrativos não estão incluídos nessa liberação. CORS não substitui autenticação nem autorização.

Para testar a integração real, execute o Angular em `http://localhost:4200` e verifique as requisições na aba Network do navegador. Para testar somente o preflight de `POST /doacoes`, use:

```bash
curl -i -X OPTIONS http://localhost:8080/doacoes -H "Origin: http://localhost:4200" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: content-type"
```

Execute `./mvnw clean verify` no Linux/macOS ou `mvnw.cmd clean verify` no Windows para executar a suíte de testes antes do Pull Request.

## Como contribuir

### Branches

- A `main` é protegida: ninguém dá push direto nela.
- Nome da branch: `bda-<número do card>-<função>`, tudo minúsculo e separado por hífen.
  - Exemplo: `bda-5-cadastro-doacao`
- Uma branch por card do Jira. Depois do merge, a branch pode ser apagada. Não criar branch à toa.

### Commits

Sempre em português, no formato `tipo: o que foi feito`:

| Tipo | Quando usar | Exemplo |
|---|---|---|
| `feat:` | funcionalidade nova | `feat: cria endpoint de cadastro de doação` |
| `fix:` | correção de erro | `fix: corrige validação da data de validade` |
| `docs:` | documentação | `docs: atualiza tabela de endpoints` |

Depois dos dois pontos, tudo minúsculo, com o verbo no presente ("cria", "corrige", "atualiza") e a mensagem curta.

### Atualizar o local com o remoto

Sempre puxar as atualizações do remoto **antes de começar a trabalhar** e **antes de abrir o PR**:

```bash
git fetch --all        # baixa as atualizações de todas as branches
git checkout main
git pull               # atualiza a main local
git checkout bda-5-cadastro-doacao
git merge main         # traz as novidades da main pra sua branch
```

### Pull Request

- O título segue o mesmo padrão dos commits. Exemplo: `feat: cria endpoint de cadastro de doação`
- A descrição segue este modelo:

```
## O que foi feito
(explique em poucas linhas)

## Card do Jira
BDA-

## Como testar
(passo a passo curto pra conferir)

## Checklist
- [ ] Atualizei minha branch com a `main` (`git fetch --all` e `git merge main`)
- [ ] O projeto roda sem erro
- [ ] Os commits seguem o padrão (`feat:`, `fix:`, `docs:`) em português
- [ ] Atualizei o README, se mudei endpoint ou tela
```

- O PR precisa de aprovação antes do merge.
