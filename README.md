# 🚀 API Desafio Votação 2.0

## 🌟 Visão Geral

Este repositório apresenta a solução completa para o desafio "Votação 2.0". A aplicação é uma solução web robusta para gerenciar e participar de sessões de votação, executada na nuvem e com foco na performance e na usabilidade.

### Descrição da API

A API RESTful `API Voting Challenge 2.0` na versão `1.0.0` foi desenvolvida para gerenciar e facilitar o processo de votação em pautas de forma eficiente e transparente. A solução permite o cadastro de novas pautas, a abertura de sessões de votação com tempo pré-determinado, o recebimento de votos 'Sim' ou 'Não' de usuários únicos e a contabilização final dos resultados.

## ⚙️ Tecnologias Utilizadas

| Componente | Tecnologia                                            | Observação | 
| :--- |:------------------------------------------------------| :--- | 
| **Backend** | [Spring Boot](https://spring.io/projects/spring-boot) | Frameworks e bibliotecas de livre escolha. | 
| **Persistência** | MySQL                                                 | `[Ex: PostgreSQL, MongoDB, MySQL]` | 

## ✅ Funcionalidades Principais da API

A API é dividida em dois grupos principais de operações, refletindo a estrutura do sistema: `Voter Operations` e `Admin Operations`.

### `Admin Operations`

Endpoints para gerenciar usuários administradores, pautas e sessões de votação.

* `POST /api/v1/admin`: Cria um novo usuário administrador.

* `GET /api/v1/admin/{id}`: Busca um usuário administrador por ID.

* `PUT /api/v1/admin/{id}`: Atualiza um usuário administrador.

* `DELETE /api/v1/admin/{id}`: Exclui um usuário administrador.

* `GET /api/v1/admin/email`: Busca um usuário administrador por e-mail.

* `GET /api/v1/admin/cpf`: Busca um usuário administrador por CPF.

* `POST /api/v1/admin/{id}/agenda`: Cria uma nova pauta para votação.

* `POST /api/v1/admin/voters`: Cria um novo usuário votante.

* `POST /api/v1/admin/agenda/{id}/voting-session`: Abre uma nova sessão de votação para uma pauta.

### `Voter Operations`

Endpoints para usuários votantes interagirem com as sessões de votação.

* `GET /api/v1/voter/open-sessions`: Lista todas as sessões de votação abertas.

* `POST /api/v1/voter/voting-session/{sessionId}/vote`: Registra um voto em uma sessão específica.

* `GET /api/v1/voter/voting-session/{sessionId}/results`: Busca o resultado da votação de uma sessão.

## 🚀 Como Executar o Projeto

### Pré-requisitos

* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) (versão 17 ou superior, por exemplo)

* [Maven](https://maven.apache.org/) (versão 3.x ou superior)


### Configuração e Execução

1. **Clone o repositório:**

   ```bash
   git clone [GitHub](https://github.com/josiasbarreto-dev/api-voting-challenge.git)
   cd desafio-votacao