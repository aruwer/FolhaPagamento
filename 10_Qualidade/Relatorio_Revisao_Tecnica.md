# Relatório de Revisão Técnica

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Branch revisada:** `feat/QDS_2.0.0/fechamento-a3`  
**Data da revisão:** 23/11/2026  
**Escopo:** código-fonte, suíte de testes, configuração de build, banco de dados, documentação A1/A3 e evidências de homologação, contra a [ERS](../01_Requisitos/Documento_Visao_e_ERS.md), o [PGQ](PGQ.md) e o enunciado do Trabalho Integrador.  
**Método:** inspeção dos artefatos com o checklist do PGQ 6.3, execução de `.\mvnw.cmd clean verify`, execução da aplicação contra PostgreSQL real e revisão visual de todas as capturas usadas como evidência.

---

## 1. Não conformidades

Desvios em relação ao que o próprio PGQ, o Plano de Testes ou o enunciado exigem.

| ID | Não conformidade | Referência violada | Situação | Ação |
| :--- | :--- | :--- | :--- | :--- |
| **NC-01** | Apenas 3 de 14 PRs integrados na `main` até o #36 têm revisão aprovada registrada no GitHub (21,4%): o #25, o #35 (Rafaela Acosta, 23/11/2026) e o #36 (Ana Luísa Lopes, 24/11/2026). Medição fixada no #36; PRs posteriores não entram no cálculo. | PGQ 6.3 e 6.6-4 (100% dos PRs revisados) | **Parcialmente corrigida** (PR #35 aprovado; PRs antigos não podem ser revisados retroativamente) | Revisar formalmente o PR desta branch antes do *merge*; ativar a regra de proteção da `main` que exige 1 aprovação. PRs já integrados não podem ser revisados retroativamente de forma válida. |
| **NC-02** | Cobertura de branches 77,32%, abaixo da meta de 80%; o build não aplicava limite automático. | PGQ 6.4-1 e 6.6-2 | Corrigida | 18 novos testes (bordas de competência, nulos, CT-033, CT-039); `jacoco:check` ≥ 80% (linhas e branches) no `pom.xml`. Resultado: 95,96%. |
| **NC-03** | RNF003 sem verificação automatizada em PostgreSQL real (3 testes ignorados). | Plano de Testes 3 ("o teste Testcontainers ignorado não substitui a evidência em PostgreSQL") | Corrigida | BUG-002 corrigido; 3/3 testes executados. |
| **NC-04** | Entrega fora da estrutura `01_Requisitos` … `11_Apresentacao` e README apontando para arquivos inexistentes (`VisaoDeSistema+ERSRastreavel.pdf`, `Consolidar EAP...pdf`, `CASOS_TESTE_RASTREAVEIS_A1.md`). | Enunciado, seção 7 | Corrigida | Documentos movidos com `git mv` (histórico preservado), README de cada pasta e índice novo no README principal. |
| **NC-05** | Registro de execução dos casos de teste vazio ("A preencher"); ACE-3, ACE-4 e ACE-6 pendentes; métrica "requisitos atendidos" não calculada; Termo de Homologação sem parecer. | Plano de Testes 6 e 7; PGQ 6.4-3 e 6.4-5 | Corrigida | 40 casos executados e registrados; 6/6 critérios aprovados; métricas calculadas; parecer proposto no Termo. |
| **NC-06** | Os defeitos foram registrados apenas em Markdown; o PGQ exige registro em GitHub Issues com rótulo de severidade. | PGQ 6.5 | **Aberta — ação corretiva definida** | Abrir uma Issue por BUG-001…006 (rótulo de severidade, link do PR) e encerrá-las com o *merge*, passando os defeitos de "Validado" para "Fechado". |

## 2. Problemas encontrados

Defeitos do produto, dos testes e das evidências — detalhados no [Registro de Defeitos](Registro_Defeitos.md):

1. **BUG-001 (S3)** — especificação de 4 casos contrariava a RN010.
2. **BUG-002 (S3)** — Testcontainers 1.21.3 incompatível com o Docker Engine 29 (HTTP 400): testes PostgreSQL ignorados.
3. **BUG-003 (S3)** — ausência de testes HTTP ponta a ponta.
4. **BUG-004 (S4)** — evidências de CT-038/CT-040 mostravam a tela errada.
5. **BUG-005 (S3)** — snapshot lido do PostgreSQL diferente do gravado por causa do offset de fuso de `data_processamento`.
6. **BUG-006 (S3)** — mensagem do estado vazio da folha orientava a cadastrar colaboradores em vez de processar a competência.
7. **BUG-007 (S3)** — teto de 100% no percentual de comissão, sem respaldo na RN005 do cliente (apontado pela avaliação da A3; [Issue #38](https://github.com/aruwer/FolhaPagamento/issues/38)).

Observações sem defeito:

- O documento de arquitetura e o PGQ afirmavam que o build "não aplica limite automático" de cobertura e que os testes PostgreSQL estavam "pendentes" — textos atualizados.
- `Arquitetura.md` misturava UML (análise) e arquitetura (projeto); foi dividido em `02_Analise/Modelagem_UML.md` e `03_Projeto/Documento_Arquitetura.md`, e a UML ganhou diagramas de estados, implantação e ER.
- O arquivo `docs/Resumo_Fechamento_Agente.md` (anotações de trabalho, não um entregável) foi removido; seu conteúdo útil está neste relatório.

## 3. Correções realizadas

| Área | Correção | Arquivos |
| :--- | :--- | :--- |
| Build | Testcontainers 1.21.3 → 1.21.4; regra `jacoco:check` (≥ 80% linhas e branches) | `pom.xml` |
| Código de produção | Normalização do fuso de `data_processamento` (BUG-005) | `FolhaProcessadaJpaEntity.java` |
| Código de produção e banco | Remoção do teto de 100% do percentual de comissão, sem respaldo na RN005 (BUG-007) | `ColaboradorComissionado.java`, `ColaboradorForm.java`, `form.html`, migração `V3__remove_teto_percentual_comissao.sql` |
| Interface | Mensagem do estado vazio da folha (BUG-006) | `templates/folha.html` |
| Testes | `CompetenciaEBordasTest` (16 testes: bordas de competência, nulos, fuso, mensagem); `ct039ResumePorCategoriaECompetencia`; `novoTipoDeRemuneracaoNaoAlteraCalculosExistentes` (CT-033) | `src/test/java/folhapagamento/` |
| Evidências | 15 novos arquivos: capturas de CT-023, 026, 027, 030, 032, 036, 038, 040, PDF de impressão e 2 logs de reinício com consultas SQL | `09_Homologacao/evidencias/` |
| Documentação | Estrutura 01–11; matrizes com status verificado; registro de execução dos 40 casos; relatórios de execução, integração e aceitação; manuais reescritos; métricas; termo de homologação; apresentação | todas as pastas |

**Resultado da verificação após as correções:** `clean verify` — BUILD SUCCESS, 106 testes, 0 falhas, 0 erros, 0 ignorados; cobertura 96,96% (linhas) e 95,88% (branches) (execução de 25/11/2026, após a correção do BUG-007).

## 4. Lições aprendidas

1. **Teste ignorado não é teste aprovado.** Enquanto os testes PostgreSQL estavam *skipped*, o BUG-005 ficou escondido. Um *skip* precisa ser investigado como bloqueio, não aceito como normal.
2. **Testar no mesmo SGBD de produção.** O H2 em modo PostgreSQL não reproduz o tratamento de `TIMESTAMP WITH TIME ZONE`; só o PostgreSQL real revelou a diferença.
3. **Dependências de teste também envelhecem.** Uma atualização do Docker Desktop quebrou o Testcontainers sem nenhuma mudança no projeto. Manter as bibliotecas de infraestrutura atualizadas faz parte da qualidade.
4. **Metas sem verificação automática se perdem.** A meta de 80% existia no PGQ, mas a cobertura de branches caiu para 77% sem ninguém perceber. Com `jacoco:check` o build passa a impedir a regressão.
5. **Evidência precisa corresponder ao caso.** Uma captura correta de outra tela (BUG-004) não comprova o requisito; revisar visualmente cada evidência antes de aprovar um critério evitou uma aprovação indevida — e revelou o BUG-006.
6. **Processo também precisa de evidência.** As revisões de PR provavelmente aconteceram, mas sem registro no GitHub não podem ser comprovadas (NC-01). O registro faz parte da entrega.
7. **Isolar dados de teste.** As evidências de homologação foram geradas em uma database dedicada (`folha_evidencias`) e em porta separada, sem tocar nos dados de desenvolvimento da equipe.
8. **Regra adicional exige aprovação do cliente.** O teto de 100% na comissão não estava na RN005 e só foi percebido na avaliação da A3 (BUG-007). Restrições criadas pela equipe precisam ser aprovadas e registradas na ERS; sem isso, valem os requisitos do cliente, e os testes devem verificá-los, não a regra inventada.

## 5. Parecer

**Conclusão proposta:** **Aprovado com ressalvas.** O produto atende a 100% dos requisitos funcionais, não funcionais e regras de negócio, sem defeitos abertos e com cobertura acima da meta. Permanecem duas não conformidades **de processo** (NC-01 — registro de revisão de PRs; NC-06 — defeitos fora das Issues), com ações corretivas definidas e sem impacto no comportamento do sistema.

**Revisora técnica (PGQ 6.3):** Amanda Ruwer  **Assinatura:** Amanda M Ruwer  **Data:** 24/11/2026

**Responsável pela qualidade (PGQ):** Ana Luísa Lopes  **Assinatura:** Ana Luísa Lopes  **Data:** 24/11/2026