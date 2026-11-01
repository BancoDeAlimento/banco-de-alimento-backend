# Banco de Alimento - Frontend

Interface web do projeto **Banco de Alimento**, feita com Angular. Projeto final da Residência Porto Digital - SENAC com a Capgemini (3º período).

A API (Java + Spring Boot) fica em outro repositório: [banco-de-alimento-backend](https://github.com/BancoDeAlimento/banco-de-alimento-backend)

## Sobre o projeto

O Banco de Alimento conecta quem tem alimentos para doar com quem precisa receber. A ideia é facilitar o cadastro das doações e o controle do que está disponível, evitando desperdício.

> **A definir pelo time:** nome do projeto 

### Funcionalidades

- [ ] A definir (exemplo: tela de listagem de alimentos disponíveis)
- [ ] A definir (exemplo: formulário de cadastro de doação)
- [ ] A definir (exemplo: tela de retirada)

## Integrantes

| Nome completo | Área |
|---|---|
| Allany Dias de Oliveira | Frontend |
| [nome completo] (Maya) | Frontend |
| [nome completo] (Vinícius) | Frontend |
| Victoria Soares | Backend |
| [nome completo] (biel) | Backend |
| [nome completo] (Felipe) | Backend |

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Angular | 22 |
| Node.js | 24 (LTS) |
| npm | vem junto com o Node.js |
| TypeScript | instalado com o Angular |

## Pré-requisitos

- Node.js 24 (LTS). Para conferir, rode `node -v`
- Angular CLI:

  ```bash
  npm install -g @angular/cli
  ```

- Git
- O backend rodando em `http://localhost:8080` (veja as instruções no [repositório do backend](https://github.com/BancoDeAlimento/banco-de-alimento-backend))

## Como rodar

1. Clone o repositório:

   ```bash
   git clone https://github.com/BancoDeAlimento/banco-de-alimento-frontend.git
   cd banco-de-alimento-frontend
   ```

2. Instale as dependências:

   ```bash
   npm install
   ```

3. Rode a aplicação:

   ```bash
   ng serve
   ```

4. Abra `http://localhost:4200` no navegador.

## Como o frontend se comunica com o backend

O frontend chama a API em `http://localhost:8080` usando o `HttpClient` do Angular. Os dados mostrados nas telas vêm das respostas JSON do backend.

> **A definir pelo time:** listar aqui os endpoints que o frontend usa, quando existirem.

| Tela | Endpoint usado |
|---|---|
| A definir | `GET /api/...` |

## Como contribuir

- A branch principal é a `main` e ela é protegida: não é possível dar push direto.
- Cada tarefa é feita em uma branch própria, com a chave do card do Jira no nome. Exemplo: `feature/BDA-8-tela-doacoes`
- As mudanças entram na `main` por **Pull Request**, que precisa de aprovação antes do merge.
- As mensagens de commit devem dizer o que foi feito. Exemplo: `BDA-8 cria tela de listagem de doações`
