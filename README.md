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

## 🔌API Versioning
O versionamento é uma comunicação eficaz em torno de alterações em sua API, para que quem as consome saiba o que esperar dela. Você está entregando dados para o público de algum modo e precisa comunicar quando muda a maneira como os dados são entregues. 
Esta API `Voting-Challenge` utiliza versionamento através da URI (Uniform Resource Identifier), permitindo futuras atualizações sem impactar clientes existentes. A versão atual é `v1`, indicada no caminho dos endpoints (ex.: `/api/v1/users`).

### Justificativa do Tipo de Versionamento:
- **Simplicidade e Descoberta (Padrão REST):** A URI é o identificador exclusivo de um recurso. Ao incluir a versão (/v1, /v2) no caminho, você está definindo que `v1/users` é um recurso completamente diferente de v2/users. Isso é o que a arquitetura REST espera.
- **Fácil para o Desenvolvedor:** É intuitivo. O cliente (quem consome a API) vê a versão diretamente na URL e sabe exatamente o que está chamando. É o método mais fácil de documentar e entender.
- **Caching Eficaz:** Como o URL completo `(/v1/recurso)` muda, os mecanismos de cache padrão da web (proxies, navegadores, CDNs) tratam cada versão como um recurso distinto. Isso garante que os clientes não recebam dados desatualizados da `v1` ao tentar acessar a `v2`.
- **Roteamento Direto:** No lado do servidor, é muito fácil configurar o roteamento (mapping) para diferentes versões. Você pode ter um `ControllerV1` e um `ControllerV2` que respondem a caminhos distintos, simplificando o código e o deploy.
- **Suporte a Múltiplas Versões:** Você pode manter a `v1` e a `v2` rodando simultaneamente por um longo período, permitindo que os clientes migrem gradualmente sem quebrar a aplicação deles.

### Alternativas Consideradas:
- **Versionamento via Header:** Embora seja uma abordagem limpa, pode ser menos intuitiva para desenvolvedores que consomem a API, pois eles precisam configurar headers adicionais em suas requisições. Ex: `X-API-Version: 2`.
- **Versionamento via Query Parameter:** Embora seja fácil de implementar, pode levar a confusão, pois o parâmetro de versão pode ser facilmente esquecido ou mal interpretado. Ex: `/users?version=2`.
- **Versionamento via Content Negotiation:** Embora seja uma abordagem poderosa, pode ser complexa de implementar e entender, especialmente para desenvolvedores menos experientes. Ex: `Accept: application/vnd.api.v2+json`.

### Estratégia de Atualização:
  - **Breaking Change:** Qualquer alteração que exige que o cliente modifique seu código para continuar funcionando. Ex: mudar o formato de resposta, remover um campo, alterar a lógica de negócios.<br>
  - **Non-Breaking Change:** Qualquer alteração que não exige modificação no código do cliente e mantém a compatibilidade reversa. Ex: adicionar novos campos opcionais, melhorar a performance, corrigir bugs.

#### Lidando com Breaking Changes:
Uma vez que você identificou uma Breaking Change, você não deve implantá-la na `/v1`. Você deve criar e implantar uma nova versão, a `/v2`.
1. **Criação do /v2:** Copie a estrutura do seu código da `/v1` para uma nova estrutura de versão (ex: crie uma nova classe UsersControllerV2 que mapeia para `/v2/users`) >> Aplique a mudança que quebra na nova estrutura `/v2`.
2. **Política de Depreciação (Sunset Policy):** Comunique aos usuários da API sobre a descontinuação da `/v1` com antecedência suficiente (ex: 6 meses). Forneça documentação clara sobre as mudanças na `/v2` e guias de migração. Use e-mail, blogs, e, o mais importante, inclua um cabeçalho HTTP informativo na resposta da `/v1` (Ex: Warning: This API version is deprecated and will be removed on 2026-03-01. Please migrate to `/v2`).
3. **Suporte Contínuo:** Durante o período de transição, mantenha ambas as versões ativas e monitoradas para garantir que os usuários possam migrar sem problemas. Após o prazo final, o /v1 deve ser removido ou, pelo menos, retornar um código 410 Gone (Indica que o recurso foi removido permanentemente) ou 404 Not Found com uma mensagem clara sobre a migração para /v2.

####  Lidando com Non-Breaking Changes:
As alterações que não quebram o contrato da API devem ser lançadas na versão atual `/v1`.
1. Mantenha a Estabilidade: Adicione novos campos no final dos objetos JSON/XML, mas nunca remova ou renomeie campos existentes. Clientes robustos devem ignorar campos que não esperam, mas você deve testar isso.
2. Use Novos Endpoints: Se a mudança for adicionar uma nova funcionalidade completa, crie um novo endpoint dentro do /v1 (Ex: GET /v1/users/permissions) em vez de alterar o endpoint existente (GET /v1/users).
3. Documentação Imediata: A atualização da documentação (Swagger/OpenAPI) deve ser síncrona com o deploy da mudança, garantindo que novos clientes já saibam da existência dos novos campos/recursos.

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