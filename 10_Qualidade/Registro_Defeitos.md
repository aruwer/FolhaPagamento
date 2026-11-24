# Registro de Defeitos

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0  
**Classificação:** severidade S1–S4 conforme a matriz do [PGQ, seção 6.5.1](PGQ.md#651-matriz-operacional-de-severidade-de-defeitos); prioridade P1 (corrigir no ciclo atual) e P2 (corrigir antes da entrega).  
**Ciclo de vida (PGQ 6.5.2):** Aberto → Em Análise → Em Correção → Em Revisão → Resolvido → **Validado** → Fechado. "Validado" significa corrigido e retestado sem regressão; passa a "Fechado" quando a Issue correspondente for encerrada com o link do PR (ver NC-06 no [Relatório de Revisão Técnica](Relatorio_Revisao_Tecnica.md)).

## Resumo

| Total encontrados | Corrigidos e validados | Abertos | S1 (crítico) | S2 (alto) | S3 (médio) | S4 (baixo) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **6** | **6** | **0** | 0 | 0 | 5 | 1 |

## Defeitos

| ID | Descrição | Requisito / caso | Detectado em | Severidade | Prioridade | Status | Solução aplicada | Verificação |
| :--- | :--- | :--- | :--- | :---: | :---: | :---: | :--- | :--- |
| **BUG-001** | A especificação de CT-015, CT-016, CT-019 e CT-020 exigia rejeitar campos variáveis em branco, contrariando a RN010 (valores ausentes = zero) e o comportamento implementado. | RN010; CT-015/016/019/020 | Revisão dos casos de teste (commit `2e32eec`) | S3 | P1 | Validado | Especificação corrigida para tratar branco como zero e concluir o cadastro. | `ct015E016AceitaCamposComissionadosEmBranco`, `ct019E020AceitaCamposProducaoEmBranco` aprovados |
| **BUG-002** | Os 3 testes de `RepositorioColaboradoresPostgresTest` eram ignorados: o Testcontainers 1.21.3 recebia **HTTP 400** do Docker Desktop (Engine 29.9.3) ao consultar o daemon, embora o Docker CLI funcionasse. RNF003 ficava sem verificação em PostgreSQL real. | RNF003; CT-030, CT-037 | Execução da suíte em 23/11/2026 (log anterior: 3 *skipped*) | S3 | P1 | Validado | Testcontainers atualizado para **1.21.4** no `pom.xml` (versão compatível com o Docker Engine 29). Nenhuma alteração na condição `disabledWithoutDocker`. | 3/3 testes PostgreSQL executados e aprovados; 0 ignorados |
| **BUG-003** | Não havia teste HTTP de ponta a ponta para os cenários funcionais especificados nas rotas web. | RF001–RF011; CT-001 a CT-039 | Revisão técnica da suíte | S3 | P1 | Validado | Criada `FolhaPagamentoWebCasosDeTesteTest` (MockMvc + H2), hoje com 21 testes. | 21/21 aprovados |
| **BUG-004** | As capturas de CT-038 e CT-040 mostravam a folha **consolidada**, enquanto os casos descrevem o **demonstrativo individual**; CT-040-passo1.png exibia o botão "Imprimir folha". | RF010, RN010; CT-038, CT-040 | Revisão das evidências | S4 | P2 | Validado | CT-038 e CT-040 reexecutados no demonstrativo individual; novas capturas e PDF de impressão gerados. Capturas antigas preservadas como complementares. | Evidências em `09_Homologacao/evidencias/CT-038` e `CT-040` |
| **BUG-005** | Ao ler um snapshot do PostgreSQL, `data_processamento` voltava com offset UTC (`Z`) em vez de `-03:00`; o mesmo instante gerava um `FolhaProcessada` diferente do gravado, quebrando a comparação de snapshots. Não ocorria com H2. | RNF003, RN009; CT-037 | Primeira execução real em PostgreSQL após a correção do BUG-002 | S3 | P1 | Validado | `FolhaProcessadaJpaEntity.paraDominio()` normaliza o instante para o fuso da folha (America/Sao_Paulo) (`noFusoDaFolha`). | `congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete` (PostgreSQL) e `snapshotLidoEmUtcEhIgualAoGravado` aprovados |
| **BUG-006** | Em competência não processada, o estado vazio da folha dizia "Cadastre colaboradores para gerar o demonstrativo", mesmo com colaboradores cadastrados, induzindo o usuário à ação errada. | RNF001, RF007; CT-036 | Revisão visual das evidências de CT-036 | S3 | P2 | Validado | `folha.html` passou a orientar "Processar folha desta competência" quando a competência é válida e "Selecione um mês e um ano válidos" quando não é. | `competenciaNaoProcessadaOrientaProcessamento` aprovado; [captura](../09_Homologacao/evidencias/CT-036/CT-036-A3-competencia-nao-processada-01-2027.png) |

## Reteste (PGQ 6.5.2)

Reteste por execução automatizada ([Bruno CLI e Cypress](../tests-e2e/README.md)) em 24/11/2026, com as evidências revisadas e validadas por **Amanda Ruwer**. Detalhes por defeito no [Roteiro de Reteste](Roteiro_Reteste_Bugs.md#4-registro-do-reteste).

| Defeito | Reteste E2E (24/11/2026) | Evidência |
| :--- | :--- | :--- |
| BUG-001 | Aprovado | [Cypress BUG-001](../09_Homologacao/evidencias-auto/cypress/screenshots/reteste-bugs.cy.js/BUG-001/), [Bruno](../09_Homologacao/evidencias-auto/bruno/relatorio.html) |
| BUG-002 | Não coberto pelo E2E (prova pela suíte JUnit, 23/11/2026) | [log](../04_Testes/log-verify.txt) |
| BUG-003 | Aprovado | [Bruno](../09_Homologacao/evidencias-auto/bruno/relatorio.html) |
| BUG-004 | Aprovado | [Cypress BUG-004](../09_Homologacao/evidencias-auto/cypress/screenshots/reteste-bugs.cy.js/BUG-004/), [PDF](../09_Homologacao/evidencias-auto/impressao/demonstrativo-BR002.pdf) |
| BUG-005 | Aprovado (parte E2E; prova principal em PostgreSQL pelo JUnit) | [Cypress BUG-005](../09_Homologacao/evidencias-auto/cypress/screenshots/reteste-bugs.cy.js/BUG-005/) |
| BUG-006 | Aprovado | [Cypress BUG-006](../09_Homologacao/evidencias-auto/cypress/screenshots/reteste-bugs.cy.js/BUG-006/) |

Os defeitos continuam em **Validado**. Passam a **Fechado** quando o PR #35 for integrado e as Issues #29 a #34 forem encerradas com o link dele.

## Observações

- Nenhum defeito S1 ou S2 foi encontrado; o critério de aprovação do PGQ (0 críticos e 0 altos abertos) está atendido.
- BUG-002, BUG-005 e BUG-006 foram encontrados e corrigidos na revisão de fechamento da A3 (23/11/2026). BUG-005 só se manifesta no PostgreSQL real — ele não seria detectado enquanto o BUG-002 mantinha os testes ignorados.
- Itens de processo e documentação (cobertura de branches abaixo da meta, estrutura de entrega, revisões de PR) não são defeitos do produto e estão registrados como não conformidades no [Relatório de Revisão Técnica](Relatorio_Revisao_Tecnica.md).
