# 🚀 API Desafio Votação 2.0

## 🌟 Visão Geral

Este repositório apresenta a solução completa para o desafio "Votação 2.0". A aplicação é uma solução web robusta para gerenciar e participar de sessões de votação, executada na nuvem e com foco na performance e na usabilidade.

## 📄 Descrição da API

A API RESTful `API Voting Challenge 2.0` na versão `1.0.0` foi desenvolvida para gerenciar e facilitar o processo de votação em pautas de forma eficiente e transparente. A solução permite o cadastro de novas pautas, a abertura de sessões de votação com tempo pré-determinado, o recebimento de votos 'Sim' ou 'Não' de usuários únicos e a contabilização final dos resultados.

## ⚙️ Tecnologias Utilizadas
| Ícone | Componente | Tecnologia |
| :---: | :--- | :--- |
| ☕ | **Linguagem** | Java 17 (LTS) |
| 🍃 | **Framework** | Spring Boot 3.x |
| 🗄️ | **Persistência** | MySQL |
| 🪶 | **Versionamento de DB** | Flyway |
| 📚 | **Documentação da API** | Swagger (OpenAPI) |
| 🐳 | **Containerização** | Docker |
| 📦 | **Gerenciador de Dependências** | Maven |
| 🗺️ | **Mapeamento de Objetos** | MapStruct |
| 🌐 | **API Web** | RESTful |
| ✅ | **Testes** | JUnit 5 e Mockito |
| 🔗 | **Repositório** | [GitHub](https://github.com/josiasbarreto-dev/api-voting-challenge.git) |

## ✅ Funcionalidades Principais da API

A API é dividida em quatro grupos principais de operações, refletindo a estrutura do sistema: `Agenda Operations`, `User Operations`, `Vote Operations` e `Voting Session Operations`.

### 🔑 User Operations
Endpoints para gerenciar os usuários de votação.

| Método HTTP | Endpoint                    | Descrição                             |
|:------------|:----------------------------|:--------------------------------------|
| `POST`      | `/api/v1/users`             | Cria um novo usuário.                 |
| `PUT`       | `/api/v1/users/{id}`        | Atualiza um usuário.                  |
| `GET`       | `/api/v1/users/{id}`        | Busca um usuário por ID.              |
| `GET`       | `/api/v1/users/cpf?cpf=...` | Busca um usuário por CPF.             |
| `GET`       | `/api/v1/users`             | Busca uma lista paginada de usuários. |
| `DELETE`    | `/api/v1/users/{id}`        | Exclui um usuário.                    |

### 📝 Agenda Operations
Endpoints para gerenciar as pautas de votação.

| Método HTTP | Endpoint               | Descrição                           |
|:------------|:-----------------------|:------------------------------------|
| `POST`      | `/api/v1/agendas`      | Cria uma nova pauta.                |
| `PUT`       | `/api/v1/agendas/{id}` | Atualiza uma pauta.                 |
| `GET`       | `/api/v1/agendas/{id}` | Busca uma pauta por ID.             |
| `GET`       | `/api/v1/agendas`      | Busca uma lista paginada de pautas. |
| `DELETE`    | `/api/v1/agendas/{id}` | Exclui uma pauta.                   |

### 🕒 Voting Session Operations
Endpoints para usuários interagirem nas sessões de votações e pegar resultado de votação.

| Método HTTP | Endpoint                                      | Descrição                                                                                              |
|:------------|:----------------------------------------------|:-------------------------------------------------------------------------------------------------------|
| `POST`      | `/api/v1/voting-sessions`                     | Permite ao usuário criar uma sessão de votação.                                                        |
| `GET`       | `/api/v1/voting-sessions`                     | Lista todas as sessões de votação abertas.                                                             |
| `GET`       | `/api/v1/voting-sessions/{sessionId}/results` | Permite ao usuário buscar o resultado de uma sessão de votação após o tempo de votação ser finalizado. |

### 🗳️ Vote Operations

Endpoints para usuários votarem em uma sessão de votação aberta.

| Método HTTP | Endpoint                                   | Descrição                                  |
|:------------|:-------------------------------------------|:-------------------------------------------|
| `POST`      | `/api/v1/voting-sessions/{sessionId}/vote` | Registra um voto em uma sessão específica. |

## 🚀 Como Executar o Projeto

### Pré-requisitos:

Certifique-se de que os seguintes softwares estão instalados em sua máquina:

* **[Git](https://git-scm.com/downloads)** (para clonar o repositório)
* **[Docker](https://www.docker.com/get-started/)**
* **[Docker Compose](https://docs.docker.com/compose/install/)**

### Passo a Passo:

1.  **Clone o Repositório:**
    Abra o terminal e execute o comando abaixo para baixar o código-fonte do projeto.
    ```bash
    git clone [https://github.com/josiasbarreto-dev/api-voting-challenge.git](https://github.com/josiasbarreto-dev/api-voting-challenge.git)
    ```

2.  **Acesse o Diretório do Projeto:**
    Navegue para a pasta do projeto que foi clonada.
    ```bash
    cd api-voting-challenge
    ```

3.  **Execute a Aplicação com Docker:**
    Com o Docker e o Docker Compose instalados, execute o seguinte comando na raiz do projeto para construir as imagens e iniciar os containers.
    ```bash
    docker-compose up --build
    ```

Sua aplicação estará disponível em `http://localhost:8080` (ou na porta configurada no seu `docker-compose.yml`).

## ✍️ Autor

Este projeto foi desenvolvido por:

* **Josias Barreto** - [GitHub](https://github.com/josiasbarreto-dev)
* **LinkedIn** - [Josias Barreto](https://www.linkedin.com/in/josiasbarreto-dev/)