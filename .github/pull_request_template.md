# Proposta de Pull Request: [Tipo]: [Breve Descrição da Mudança]

## Título do Commit (opcional, se diferente do título do PR):
Ex: `feat: implement user authentication`

## Descrição da Mudança:
Descreva detalhadamente o que foi alterado e por que.
- O que este PR resolve/adiciona?
- Qual o impacto no sistema?
- Quais foram os principais desafios ou decisões de design?

## Tipo de Mudança (Marque com `x` entre os colchetes `[]`):
- [ ] `feat`: Nova funcionalidade
- [ ] `fix`: Correção de bug
- [ ] `docs`: Alterações na documentação
- [ ] `style`: Mudanças que não afetam o significado do código (espaços em branco, formatação, ponto e vírgula ausente, etc.)
- [ ] `refactor`: Uma mudança de código que não adiciona um recurso nem corrige um bug
- [ ] `perf`: Uma mudança de código que melhora o desempenho
- [ ] `test`: Adição ou correção de testes
- [ ] `chore`: Outras alterações que não modificam o código-fonte principal ou arquivos de teste (ex: atualização de dependências, ajustes de build, etc.)
- [ ] `build`: Alterações que afetam o sistema de build ou dependências externas (ex: Maven, Gradle, npm)
- [ ] `ci`: Alterações nos arquivos e scripts de CI (ex: GitHub Actions, GitLab CI)
- [ ] `revert`: Reverte um commit anterior
- [ ] `WIP`: Trabalho em progresso (Não mesclar!)

## Requisitos/Critérios de Aceite Atendidos:
- Liste quaisquer requisitos ou critérios de aceite específicos que esta mudança satisfaz.
- Ex:
    - O usuário consegue se cadastrar com email e senha.
    - O sistema valida a unicidade do email no cadastro.

## Como Testar (Passos para Reproduzir/Verificar):
Descreva os passos que o revisor pode seguir para testar sua mudança.
- **Ambiente:** (Ex: Local, Docker, Staging)
- **Pré-requisitos:** (Ex: Banco de dados configurado, usuário de teste específico)
- **Passos:**
    1. Executar `mvn spring-boot:run`
    2. Acessar `http://localhost:8080/api/users`
    3. Enviar uma requisição `POST` com o body:
       ```json
       {
           "name": "Novo Usuário",
           "email": "teste@example.com"
       }
       ```
    4. Verificar se a resposta é `201 Created` e os dados do usuário.

## Configurações Adicionais / Variáveis de Ambiente (se aplicável):
- Liste quaisquer novas variáveis de ambiente ou configurações que esta mudança introduz ou exige.

## Screenshots / Vídeos (se aplicável):
- Adicione capturas de tela ou vídeos que demonstrem a funcionalidade, especialmente para mudanças visuais.

## Pontos para Revisão Específicos:
- Há alguma parte do código que você gostaria que o revisor prestasse atenção especial?
- Alguma decisão que você gostaria de discutir?

## Checklist antes de Mesclar:
- [ ] O código segue as convenções de estilo do projeto?
- [ ] Os testes unitários/de integração foram executados e passaram?
- [ ] A documentação (código, README, swagger) foi atualizada se necessário?
- [ ] As variáveis de ambiente ou configurações foram devidamente documentadas?
- [ ] Meu commit segue as diretrizes de Conventional Commits (se aplicável)?
- [ ] A performance não foi degradada significativamente?