# Termo de Homologação

**Projeto:** Sistema de Folha de Pagamento ("Folha Clara")  
**Versão homologada:** 2.0.0 (`folha-pagamento-2.0.0.jar`; branch `feat/QDS_2.0.0/fechamento-a3`)  
**Unidade Curricular:** Garantia da Qualidade de Software — Trabalho Integrador A3  
**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta

---

## 1. Declaração

Declaramos, para os devidos fins, que a versão 2.0.0 do Sistema de Folha de Pagamento foi submetida aos testes unitários, de integração, de sistema e de aceitação previstos no [Plano de Testes](../04_Testes/Plano_Testes.md), e que **atende aos requisitos especificados pelo cliente** na [Especificação de Requisitos de Software](../01_Requisitos/Documento_Visao_e_ERS.md), conforme as evidências abaixo:

| Item verificado | Resultado |
| :--- | :--- |
| Requisitos funcionais RF001–RF011 | 11/11 atendidos |
| Requisitos não funcionais RNF001–RNF004 | 4/4 atendidos |
| Regras de negócio RN001–RN010 | 10/10 atendidas |
| Critérios gerais de aceitação ACE-1 a ACE-6 | 6/6 aprovados ([Criterios_Aceitacao.md](Criterios_Aceitacao.md)) |
| Cenários de aceitação | 15/15 aprovados ([Relatorio_Testes_Aceitacao.md](Relatorio_Testes_Aceitacao.md)) |
| Casos de teste CT-001 a CT-041 | 41/41 executados e aprovados |
| Testes automatizados (JUnit) | 106/106 aprovados, 0 ignorados |
| Testes E2E contra a aplicação real | Bruno CLI 33/33 (112 testes) e Cypress 11/11, execução de 24/11/2026 23:50; evidências em [evidencias-auto](evidencias-auto/) (a primeira execução foi validada em 24/11/2026; a revisão da reexecução, pelo BUG-007, está pendente) |
| Cobertura de código | 96,96% linhas · 95,88% branches (meta ≥ 80%) |
| Defeitos | 7 encontrados e 7 corrigidos; BUG-001 a BUG-006 validados e encerrados nas Issues #29 a #34; BUG-007 (Issue #38, teto de 100% na comissão, apontado pela avaliação da A3) corrigido e a validar; 0 abertos; 0 críticos/altos |

## 2. Critérios de aprovação do PGQ (seção 6.6)

| # | Critério | Situação |
| :---: | :--- | :---: |
| 1 | 100% dos requisitos funcionais implementados e rastreados com casos aprovados | ✔ Atendido |
| 2 | Cobertura JaCoCo ≥ 80% | ✔ Atendido |
| 3 | 0 defeitos S1 e 0 defeitos S2 abertos | ✔ Atendido |
| 4 | 100% dos PRs integrados com revisão aprovada | ✘ Não atendido (21,4%: 3 de 14 PRs até o #36 — NC-01) |

## 3. Parecer

- [ ] **Homologado**
- [X] **Homologado com ressalvas**
- [ ] **Não homologado**

**Ressalvas (de processo, sem impacto no comportamento do sistema):**

1. **NC-01** — apenas 3 de 14 PRs integrados na `main` até o #36 têm revisão aprovada registrada no GitHub (21,4%): #25, #35 (Rafaela Acosta) e #36 (Ana Luísa Lopes). Os PRs anteriores não podem ser revisados retroativamente. A medição está fixada no #36; PRs posteriores (como o #37, que contém esta atualização) não entram no cálculo, para o denominador não mudar a cada PR de documentação. Ação: os PRs seguintes passam por revisão aprovada de outra integrante e a branch `main` passa a exigir aprovação.

**Não conformidade corrigida durante o fechamento:** NC-06 (defeitos registrados só em Markdown) — as Issues #29 a #34 foram abertas, retestadas e encerradas com o link do PR #35.

Detalhes no [Relatório de Revisão Técnica](../10_Qualidade/Relatorio_Revisao_Tecnica.md) e no [Relatório Final de Qualidade](../10_Qualidade/Relatorio_Final_Qualidade.md).

## 4. Assinaturas

| Papel | Nome | Assinatura | Data |
| :--- | :--- | :--- | :--- |
| Responsável pela qualidade e homologação (PGQ; KAN-15) | Ana Luísa Lopes | Ana Luísa Lopes | 24/11/2026 |
| Revisora técnica (PGQ 6.3) | Amanda Ruwer | Amanda Ruwer | 24/11/2026 |
| Responsável por arquitetura e desenvolvimento | Rafaela Acosta | Rafaela Acosta | 24/11/2026 |
| Representante do cliente / avaliador(a) | Paulo | Paulo | 30/11/2026 |
