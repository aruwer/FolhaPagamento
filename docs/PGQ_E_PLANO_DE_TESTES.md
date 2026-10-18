# Plano de Garantia da Qualidade e Plano de Testes

Status: proposta para revisão da equipe.
Base documental: [T1 - Requisitos do Cliente.pdf], requisitos RF001 a RF009, RN001 a RN008, RNF001 a RNF004, além de recomendações e critérios de aceitação presentes no documento.

## Plano de Garantia da Qualidade (PGQ)

### 1 Padrões de codificação

A equipe deve seguir padrões mínimos de desenvolvimento para garantir consistência, legibilidade e manutenibilidade do sistema.

- Organização do código:
  - manter classes Java em pacotes por responsabilidade, seguindo o padrão já adotado em `folhapagamento`;
  - separar regras de negócio, camada de controle, persistência e apresentação;
  - manter nomes de classes, métodos e variáveis com linguagem clara e coerente.
- Padrões Java/Spring:
  - utilizar nomes significativos para classes, métodos e atributos;
  - evitar duplicação de lógica entre serviços e controladores;
  - manter métodos com responsabilidade única;
  - usar validações e tratamento de exceções em pontos adequados.
- Padrões de interface e frontend:
  - manter HTML, CSS e JavaScript organizados por responsabilidade;
  - manter labels, campos e mensagens de erro consistentes com o fluxo de usuário;
  - evitar lógica de cálculo distribuída em telas sem passar pelo serviço.
- Documentação interna:
  - comentários somente quando houver necessidade de contextualização;
  - manter README e comentários relevantes atualizados com alterações de regra de negócio.

### 1.2 Controle de versões

O projeto deve ser gerenciado com Git, com uso de branches e pull requests para dar rastreabilidade ao desenvolvimento.

- Branches:
  - `main` para a versão estável;
  - `feature/...` para desenvolvimento de funcionalidade;
  - `fix/...` para correções;
  - `docs/...` para documentação e artefatos de processo.
- Commits:
  - cada commit deve ter mensagem objetiva e referenciar a tarefa ou requisito impactado;
  - alterações relacionadas a um mesmo requisito devem ser mantidas em um conjunto lógico de commits.
- Pull requests:
  - toda alteração deve passar por revisão antes do merge;
  - cada PR deve indicar a funcionalidade, os requisitos afetados, os testes executados e os riscos.
- Rastreabilidade:
  - cada tarefa deve estar vinculada ao requisito ou caso de uso correspondente;
  - a equipe deve manter o histórico de alterações acessível para revisão técnica.

### 1.3 Processo de revisão técnica

A revisão técnica deve ser formalizada para reduzir defeitos e garantir que a implementação esteja alinhada aos requisitos do cliente.

- Fluxo sugerido:
  1. desenvolvimento da funcionalidade;
  2. verificação local com compilação e testes;
  3. abertura de pull request;
  4. revisão por outro membro da equipe;
  5. correção de pendências;
  6. aprovação e merge.
- Critérios de revisão:
  - o código atende ao requisito relacionado;
  - a lógica foi validada no contexto do sistema;
  - não há regressão em fluxos já existentes;
  - os nomes, mensagens e telas estão consistentes com o restante da aplicação;
  - a solução foi testada, mesmo que de forma manual quando necessário.
- Responsáveis:
  - o autor implementa a solução;
  - o revisor valida atendimento ao requisito, qualidade do código e riscos;
  - o responsável pela área de testes confirma cobertura mínima de validação.

### 1.4 Critérios de qualidade

A qualidade será avaliada por critérios objetivos e verificáveis.

- O código deve compilar sem erros;
- o sistema deve atender aos requisitos obrigatórios do cliente;
- os cálculos de salário e resumo devem estar coerentes com as regras de negócio;
- os dados essenciais devem ser validados antes da persistência;
- a aplicação deve permitir cadastro, consulta, atualização e exclusão de colaboradores;
- cada funcionalidade deve ter evidência de teste ou revisão;
- a documentação deve refletir o comportamento real do sistema.

Além disso, a equipe deve garantir que requisitos como RF001 a RF009, RN001 a RN008 e RNF001 a RNF004 sejam rastreados até a implementação e aos testes, evitando requisitos sem evidência de atendimento.

### 1.5 Métricas adotadas

As métricas devem ser coletadas e documentadas de forma simples, objetiva e repetível.

- Cobertura de testes: meta recomendada de pelo menos 80% para as classes de regra, cálculos e serviços críticos;
- Defeitos abertos críticos: zero para homologação final, salvo justificativa formal;
- Requisitos atendidos: 100% dos requisitos obrigatórios ou exceção documentada e aprovada;
- Casos de teste executados: percentual de casos planejados aprovados, reprovados e bloqueados;
- Defeitos por severidade: classificados em crítico, alto, médio e baixo;
- Tempo de correção: tempo médio para resolver defeitos por prioridade.

Essas métricas devem ser registradas em relatórios de execução, sem apresentar metas como resultados já obtidos antes da execução real.

### 1.6 Critérios de aprovação

Uma funcionalidade ou entrega será aprovada somente quando forem atendidos os critérios abaixo:

- o requisito correspondente foi validado;
- a funcionalidade foi testada com evidência;
- não restaram defeitos críticos abertos;
- o código foi revisado por outra pessoa da equipe;
- o comportamento observado coincide com o resultado esperado;
- a documentação foi atualizada quando necessário;
- a entrega foi analisada sob o contexto do cliente e dos requisitos do documento de requisitos.

A aprovação final do sistema deve ocorrer somente após verificação do conjunto de requisitos obrigatórios, testes relevantes concluídos e ausência de discrepâncias graves.

---

## 2 Plano de Testes

### 2.1 Escopo

O escopo do plano de testes contempla os principais requisitos do cliente para o sistema de folha de pagamento, com foco em:

- cadastro de colaboradores;
- tipos de colaborador: padrão, comissionado e por produção;
- consulta e manutenção dos dados cadastrais;
- cálculo do salário final;
- geração da folha individual e consolidada;
- resumo da folha por categoria;
- persistência dos dados;
- validação de regras de negócio e integridade.

Os requisitos diretamente alinhados ao escopo são RF001 a RF009, RN001 a RN008 e RNF001 a RNF004. O plano também deve cobrir recomendações do documento do cliente quando estas forem aprovadas pela equipe como parte do escopo do projeto.

### 2.2 Estratégia

A estratégia deve seguir uma abordagem progressiva em níveis de teste, com foco em garantir qualidade em diferentes pontos do ciclo de desenvolvimento.

- Teste unitário: validar regras de cálculo e validações isoladas;
- Teste de integração: verificar a comunicação entre serviço, camada de acesso e persistência;
- Teste de sistema: validar fluxos completos na aplicação web;
- Teste de aceitação: confirmar se os requisitos do cliente são atendidos de forma prática e verificável.

A estratégia considera que o sistema é composto por regras de negócio, formulário de entrada, cálculo de remuneração e geração de relatórios, sendo importante testar tanto as condições válidas quanto as inválidas.

### 2.3 Níveis de teste

#### a) Teste unitário

- objetivo: testar funções e regras isoladas;
- foco: cálculo de salário por categoria, validação de campos, unicidade de matrícula e lógica de resumo;
- exemplos:
  - salário base calculado corretamente;
  - comissão e produção aplicados conforme regra da categoria;
  - nome obrigatório e matrícula única;
  - valores inválidos rejeitados.

#### b) Teste de integração

- objetivo: validar interação entre módulos;
- foco: controller, service e persistência;
- exemplos:
  - cadastro de colaborador persiste corretamente;
  - alteração e exclusão refletem no conjunto de dados;
  - resumo da folha considera os dados atualmente armazenados.

#### c) Teste de sistema

- objetivo: verificar o comportamento real da aplicação;
- foco: uso do navegador, formulário, retorno de dados e geração de relatórios;
- exemplos:
  - preencher formulário com dados válidos e confirmar cadastro;
  - consultar a folha individual e a folha consolidada;
  - verificar total geral e totais por categoria.

#### d) Teste de aceitação

- objetivo: confirmar que o sistema atende às expectativas do cliente;
- foco: requisitos do documento do cliente e critérios de aceitação;
- exemplos:
  - cadastro do colaborador com dados corretos;
  - rejeição de dados obrigatórios ausentes;
  - geração correta da folha salarial;
  - visão consolidada do custo total da folha.

### 2.4 Ambiente

O ambiente de teste deve ser definido para permitir reprodução e consistência entre execuções.

- Java 17 ou superior;
- Maven Wrapper do projeto;
- Spring Boot 3.x;
- estrutura web com Thymeleaf, HTML, CSS e JavaScript;
- dados em ambiente local, seguindo a persistência atual do sistema e evoluindo conforme a arquitetura definida;
- navegador para validação manual de interface;
- ambiente de testes isolado, sem dependência de dados reais ou sensíveis.

Se a equipe decidir migrar a persistência para banco relacional em etapas posteriores, o plano deve ser ajustado para incluir ambiente com banco de teste, scripts de criação e limpeza de dados.

### 2.5 Ferramentas

As ferramentas devem ser compatíveis com o stack do projeto e com a necessidade de evidência documental.

- JUnit 5: para testes automatizados de unidade e integração;
- Maven: execução da suíte e relatórios de build;
- Spring Boot Test: para suporte à infraestrutura de testes;
- navegador e validação manual: para testes de interface e fluxo de negócio;
- GitHub Issues / board: para registro de defeitos e acompanhamento;
- relatórios gerados pelo Maven e pelos testes: para evidência e métricas.

A ferramenta de gestão de casos, como Kimi TCMS, pode ser usada como apoio para gestão e rastreabilidade, desde que a equipe a configure e utilize de forma consistente.

### 2.6 Critérios de entrada e saída

#### Critérios de entrada

Antes de iniciar a execução dos testes, a equipe deve assegurar que:

- os requisitos a serem validados foram identificados;
- os casos de teste foram definidos e revisados;
- o ambiente está disponível e configurado;
- a versão a ser testada foi identificada;
- os dados de teste foram preparados;
- o código foi compilado e está funcional em nível básico.

#### Critérios de saída

Ao final de cada execução, devem ser registrados:

- casos executados;
- resultado por caso (aprovado, reprovado, bloqueado);
- defeitos encontrados;
- evidências de execução;
- ajustes e correções realizadas;
- status final do requisito relacionado.

### 2.7 Critérios de aprovação

Um teste é considerado aprovado quando:

- o resultado obtido corresponde ao resultado esperado;
- não houve falha crítica no fluxo validado;
- a funcionalidade respeita as regras de negócio estabelecidas;
- os dados exibidos e calculados estão coerentes com o requisito.

A entrega do sistema será considerada aprovada quando:

- todos os requisitos obrigatórios tiverem sido validados ou, se houver exceção, forem formalmente justificados;
- os testes críticos forem aprovados;
- não houver defeitos críticos abertos sem correção ou aceite formal;
- existirem evidências suficientes para demonstrar que os testes foram executados e revisados.

A aprovação final deve estar apoiada em registro documental, preferencialmente no repositório, no board de tarefas e em relatórios de execução.

---

## Observação para revisão

Este documento foi estruturado como proposta inicial para revisão da equipe. Ele foi fundamentado no documento do cliente e nos requisitos observados no projeto atual. Antes da entrega final, a equipe deve validar:

- se alguma regra de negócio específica deve ser tratada como obrigatória ou recomendada;
- quais requisitos serão cobertos em versão atual e quais ficarão como evolução;
- quais métricas e critérios de aceitação serão adotados oficialmente na disciplina.
