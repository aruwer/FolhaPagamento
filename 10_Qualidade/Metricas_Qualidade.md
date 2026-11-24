# Métricas e Indicadores de Qualidade

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Coleta:** 23/11/2026  
**Fontes:** [log do `clean verify`](../04_Testes/log-verify.txt), [relatório JaCoCo](evidencias/jacoco/index.html), [Registro de Defeitos](Registro_Defeitos.md), [Casos de teste](../04_Testes/Casos_Teste_Rastreaveis.md#6-registro-de-execução), [Matriz de conformidade](../01_Requisitos/Matriz_Conformidade_Requisitos.md) e histórico de Pull Requests do GitHub.  
**Fórmulas e metas:** [PGQ, seção 6.4](PGQ.md#64-métricas-de-qualidade-fórmulas-escopo-ferramental-e-governança).

## Painel

| # | Métrica (PGQ 6.4) | Fórmula | Resultado | Meta | Situação |
| :---: | :--- | :--- | :---: | :---: | :---: |
| 1 | Cobertura de testes (linhas) | 384 / 396 × 100 | **96,97%** | ≥ 80% | ✔ Atingida |
| — | Cobertura de branches (complementar) | 95 / 99 × 100 | **95,96%** | ≥ 80% | ✔ Atingida |
| 2 | Defeitos críticos abertos (S1) | Σ S1 com status ≠ Validado/Fechado | **0** | 0 | ✔ Atingida |
| 3 | Requisitos funcionais atendidos | 11 RF aprovados / 11 RF na ERS × 100 | **100%** | 100% | ✔ Atingida |
| 4 | Índice de revisão de PRs | 1 PR aprovado / 12 PRs integrados na `main` × 100 | **8,3%** | 100% | ✘ **Não atingida** (NC-01) |
| 5 | Taxa de execução de testes | 40 casos executados / 40 planejados × 100 | **100%** | 100% | ✔ Atingida |

## 1. Cobertura de testes (JaCoCo)

| Medida | Cobertos / total | Resultado |
| :--- | ---: | ---: |
| Linhas | 384 / 396 | **96,97%** |
| Branches | 95 / 99 | **95,96%** |
| Instruções | 1676 / 1721 | **97,39%** |
| Métodos | 123 / 125 | **98,40%** |

Escopo: 15 classes de produção, sem exclusões. O build aplica `jacoco:check` (linhas e branches ≥ 80%) e falha se a meta não for atingida. Cobertura por classe no [relatório de execução](../04_Testes/Relatorio_Execucao_Testes.md#por-classe).

**Evolução:** linhas 93,38% → 96,97%; branches 77,32% → 95,96% (a cobertura de branches estava abaixo da meta antes da revisão de fechamento — NC-02).

## 2. Testes

| Indicador | Resultado |
| :--- | ---: |
| Testes automatizados executados | 105 (64 unitários + 41 de integração) |
| Aprovados | **105 (100%)** |
| Falhas / erros / ignorados | 0 / 0 / 0 |
| Casos de teste especificados (A1) | 40 (mínimo exigido: 20) |
| Casos executados / aprovados / reprovados / bloqueados | 40 / **40** / 0 / 0 |
| Cenários de aceitação aprovados | 15 / 15 |
| Critérios gerais de aceitação da ERS aprovados | 6 / 6 |
| Proporção código de teste / código de produção | 1536 / 912 linhas não vazias ≈ 1,7 |

## 3. Defeitos

| Indicador | Resultado |
| :--- | ---: |
| Defeitos encontrados | **6** |
| Defeitos corrigidos e validados | **6 (100%)** |
| Defeitos abertos | 0 |
| Por severidade | S1: 0 · S2: 0 · S3: 5 · S4: 1 |
| Densidade de defeitos | 6 / 0,912 KLOC ≈ **6,6 defeitos/KLOC** (linhas não vazias de produção) |
| Encontrados por execução em PostgreSQL real | 1 (BUG-005) |
| Encontrados por revisão de artefatos/evidências | 4 (BUG-001, 003, 004, 006) |
| Encontrados por falha de ambiente de teste | 1 (BUG-002) |

## 4. Requisitos atendidos

| Grupo | Verificados e aprovados | Percentual |
| :--- | :---: | :---: |
| Requisitos funcionais (RF001–RF011) | 11 / 11 | **100%** |
| Requisitos não funcionais (RNF001–RNF004) | 4 / 4 | 100% |
| Regras de negócio (RN001–RN010) | 10 / 10 | 100% |

Cada requisito aponta para casos executados e evidências na [Matriz de conformidade](../01_Requisitos/Matriz_Conformidade_Requisitos.md).

## 5. Revisão de Pull Requests

Levantado com `gh pr list --state merged` em 23/11/2026:

| PR | Título | Revisão registrada no GitHub |
| :---: | :--- | :---: |
| #17 | docs(pgq): define plano de garantia da qualidade e fluxo de PR | não |
| #18 | docs(arquitetura): adiciona documento de arquitetura e diagramas UML | não |
| #19 | docs: completa artefatos A1 de testes e qualidade | não |
| #20 | docs: adiciona a visão do sistema e o ERS Rastreável ao código | não |
| #21 | docs: adiciona EAP, cronograma e responsabilidades | não |
| #22 | docs: atualiza Arquitetura e PGQ | não |
| #23 | docs(qds): Arquitetura e PGQ | não |
| #24 | Correção EAP e Responsabilidades | não |
| #25 | Feat/qds 2.0.0/conformidade requisitos | **aprovado** |
| #26 | feat: adiciona estrutura básica de testes e testes iniciais JUnit | não |
| #27 | Feat/qds 2.0.0/testes unitarios junit | não |
| #28 | Feat/qds 2.0.0/db postgresql | não |

Resultado: **1 / 12 = 8,3%**, abaixo da meta de 100%. Revisões feitas fora do GitHub (conversa, chamada) não deixam registro verificável e não foram contadas. Tratamento em NC-01 do [Relatório de Revisão Técnica](Relatorio_Revisao_Tecnica.md).
