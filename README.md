# Banco de Alimento - Backend

API REST do projeto **Banco de Alimento**, feita com Java e Spring Boot. Projeto final da Residência Porto Digital - SENAC com a Capgemini (3º período).

O frontend (Angular) fica em outro repositório: [banco-de-alimento-frontend](https://github.com/BancoDeAlimento/banco-de-alimento-frontend)

## Sobre o projeto

O Banco de Alimento conecta quem tem alimentos para doar com quem precisa receber. A ideia é facilitar o cadastro das doações e o controle do que está disponível, evitando desperdício.

> **A definir pelo time:** nome do projeto

### Funcionalidades

- [ ] A definir (exemplo: cadastrar doação)
- [ ] A definir (exemplo: listar alimentos disponíveis)
- [ ] A definir (exemplo: registrar retirada)

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
| Java (JDK) | 25 |
| Spring Boot | 4.1.x (conferir a versão exata no `pom.xml`) |
| Maven | via Maven Wrapper (`mvnw`) |
| Spring Web | incluído no Spring Boot |
| Spring Data JPA | incluído no Spring Boot |
| Bean Validation | incluído no Spring Boot |
| H2 Database | incluído no Spring Boot |

## Pré-requisitos

- Java 25 instalado. Para conferir, rode `java -version`
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

Usamos o **H2**, um banco em memória, para desenvolvimento e demonstração. Os dados são apagados quando a aplicação é encerrada.

## Endpoints

> **A definir pelo time:** preencher a tabela conforme os endpoints forem criados.

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/...` | A definir |
| POST | `/api/...` | A definir |

## Como o frontend se comunica com o backend

O frontend em Angular roda em `http://localhost:4200` e chama a API em `http://localhost:8080` usando requisições HTTP (`HttpClient`). Os dados mostrados na tela vêm das respostas JSON dos endpoints acima.

Como as duas aplicações rodam em portas diferentes, o backend precisa liberar o **CORS** para a origem `http://localhost:4200`.

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
