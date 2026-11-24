# Roteiro de Reteste dos Defeitos (BUG-001 a BUG-006)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0  
**Base:** [Registro de Defeitos](Registro_Defeitos.md) · Ciclo de vida do PGQ 6.5.2: *Resolvido → **Validado** → Fechado*  
**Finalidade:** reexecutar, por uma pessoa da equipe, o caso que reproduzia cada defeito e confirmar que ele não ocorre mais e que não houve regressão.

> **Nota de processo:** o PGQ designa a Ana Luísa (QA) para validar. Neste ciclo o reteste será feito por quem preencher o campo "Executor" de cada seção, o que deve ficar registrado como está. A validação fica no nome de quem executou.

---

## Execução automatizada já realizada (Bruno CLI + Cypress)

Em 24/11/2026 (01:17), a aplicação real (JAR 2.0.0 + PostgreSQL 17, database limpa `folha_reteste`) foi exercitada por `tests-e2e/scripts/run-all.ps1` ([como executar](../tests-e2e/README.md)). **Esta execução gera as evidências; ela não valida os defeitos.** Quem valida é a pessoa que revisar as evidências abaixo e preencher a seção 4.

| Resultado da execução | Bruno CLI (backend) | Cypress (frontend) |
| :--- | :--- | :--- |
| Situação | 31 de 31 requisições aprovadas, 106 asserções | 3 specs, 9 testes, 9 aprovados |
| Onde | [`bruno/relatorio.html`](../09_Homologacao/evidencias-auto/bruno/relatorio.html), [`resultado.json`](../09_Homologacao/evidencias-auto/bruno/resultado.json) (respostas completas), [`saida.txt`](../09_Homologacao/evidencias-auto/bruno/saida.txt) | [`screenshots/`](../09_Homologacao/evidencias-auto/cypress/screenshots/) e [`videos/`](../09_Homologacao/evidencias-auto/cypress/videos/) |

**O que revisar por defeito** (as pastas dentro de `cypress/screenshots/` têm o nome do spec):

| Defeito | Evidência automatizada | O que conferir |
| :--- | :--- | :--- |
| BUG-001 | Bruno `04 CT-015 campos variaveis em branco valem zero (BUG-001)`; Cypress `reteste-bugs.cy.js/BUG-001/` | Cadastro concluído com vendas e comissão em branco; `RT001` aparece no painel com R$ 2.000,00 |
| BUG-002 | só JUnit (seção 1) | 3 testes PostgreSQL executados, 0 ignorados |
| BUG-003 | os 31 requests do Bruno, via HTTP real | Todos aprovados |
| BUG-004 | Cypress `reteste-bugs.cy.js/BUG-004/` e [PDF de impressão](../09_Homologacao/evidencias-auto/impressao/demonstrativo-BR002.pdf) | Adicionais R$ 0,00 no demonstrativo; o PDF traz só os dados, sem menu nem botões |
| BUG-005 | Bruno `24 BUG-005 reprocessar nao duplica`; Cypress `reteste-bugs.cy.js/BUG-005/`; JUnit em PostgreSQL | Uma linha por colaborador; total inalterado |
| BUG-006 | Bruno `17 CT-036 competencia nao processada ...`; Cypress `reteste-bugs.cy.js/BUG-006/` | Texto novo "Esta competência ainda não tem folha processada…"; sem o texto antigo |
| Regressão CT-036/037 | Bruno `27` a `31`; Cypress `regressao-ct036-ct037.cy.js/` | Outra competência vazia; excluído some do painel e o demonstrativo continua sem "Editar cadastro" |

**Limites desta automação:** a impressão (diálogo do navegador) é coberta pelo PDF gerado pelo Edge e pela checagem das regras `@media print` do CSS, não pelo diálogo nativo. A seção 3, passo 3, abaixo também vale para a tela: depois de processada, a competência não oferece mais o botão de processamento.

---

## 0. Preparação (uma vez)

Use uma database separada para não misturar com os dados de desenvolvimento da equipe.

```powershell
docker compose up -d
docker exec a3_folhapag-postgres-1 psql -U folha_app -d folha_pagamento -c "CREATE DATABASE folha_reteste"
$env:DATABASE_PASSWORD = "<mesma senha de POSTGRES_PASSWORD no .env>"
$env:DATABASE_URL = "jdbc:postgresql://localhost:5433/folha_reteste"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8090"
```

Abra <http://localhost:8090>. O nome do contêiner pode variar; confira com `docker compose ps`. As matrículas usadas aqui começam com `RT`. Ao terminar, pare a aplicação e, se quiser, apague a database: `DROP DATABASE folha_reteste`.

Anote antes de começar: commit testado (`git rev-parse --short HEAD`): ____________ · Navegador e versão: ____________ · Data: ____/____/2026

---

## 1. Suíte automatizada (cobre BUG-001, 002, 003 e 005)

Em outro PowerShell, na raiz do projeto (com o Docker ligado):

```powershell
.\mvnw.cmd clean verify
```

**Esperado:** `BUILD SUCCESS`, `Tests run: 105, Failures: 0, Errors: 0, Skipped: 0` e `All coverage checks have been met`.

- [ ] Passou  [ ] Falhou — Resultado obtido: ______________________________________

---

## 2. Reteste por defeito

### BUG-001 — Campos variáveis em branco valem zero (RN010)
1. **Novo colaborador** → matrícula `RT001`, nome `Reteste Um`, salário base `2000`, tipo **Salário + comissão**.
2. Deixe **Valor das vendas** e **Comissão sobre vendas** em branco e clique em **Cadastrar colaborador**.

**Esperado:** o cadastro conclui (mensagem "Colaborador cadastrado") e `RT001` aparece no painel com salário final **R$ 2.000,00**. Não pode haver erro de campo obrigatório.

- Teste automatizado relacionado: `ct015E016AceitaCamposComissionadosEmBranco`, `ct019E020AceitaCamposProducaoEmBranco` (item 1 acima).
- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

### BUG-002 — Testes PostgreSQL executam (não são ignorados)
```powershell
.\mvnw.cmd test -Dtest=RepositorioColaboradoresPostgresTest
```
**Esperado:** `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0`. Se aparecer `Skipped: 3`, o Docker não está acessível ou o Testcontainers voltou a ser incompatível.

- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

### BUG-003 — Testes HTTP de ponta a ponta
```powershell
.\mvnw.cmd test -Dtest=FolhaPagamentoWebCasosDeTesteTest
```
**Esperado:** `Tests run: 21, Failures: 0, Errors: 0`.

- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

### BUG-004 — Evidências de CT-038 e CT-040 no demonstrativo individual
1. Cadastre `RT002`, nome `Reteste Dois`, salário `2000`, **Salário + comissão**, vendas e comissão em branco.
2. **Folha de pagamento** → mês e ano atuais → **Processar folha desta competência**.
3. Clique no nome `Reteste Dois` para abrir o **demonstrativo individual**.
4. Clique em **Imprimir demonstrativo** e olhe a pré-visualização (não precisa imprimir).

**Esperado:** o demonstrativo mostra matrícula, nome, tipo, competência, **Salário base R$ 2.000,00**, **Adicionais R$ 0,00** e **Salário final R$ 2.000,00**. A pré-visualização de impressão traz esses dados **sem** menu de navegação nem botões. Compare com `09_Homologacao/evidencias/CT-038/CT-038-A3-individual-adicional-zero-E004.png` e `CT-040/CT-040-A3-impressao-demonstrativo-E003.pdf`.

- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

### BUG-005 — Snapshot lido do PostgreSQL igual ao gravado
1. Depois do passo 3 do BUG-004, abra o pgAdmin (<http://localhost:5050>) e execute na database `folha_reteste`:

   ```sql
   SELECT matricula_snapshot, mes, ano, salario_final, data_processamento
   FROM folhas_processadas WHERE matricula_snapshot = 'RT002';
   ```
2. Recarregue o demonstrativo de `RT002` duas vezes e consulte o SQL de novo.

**Esperado:** a consulta mostra uma única linha para `RT002` na competência; os valores do demonstrativo são os mesmos antes e depois de recarregar. Depois de processada, a tela deixa de oferecer o botão de processamento; a ausência de duplicidade em reprocessamentos é conferida pelos testes automatizados (JUnit e Bruno).

- Comprovação principal (automatizada): `RepositorioColaboradoresPostgresTest.congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete` e `CompetenciaEBordasTest.snapshotLidoEmUtcEhIgualAoGravado` (item 1).
- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

### BUG-006 — Mensagem da folha vazia orienta processar a competência
1. **Folha de pagamento** → mês `1` e ano `2099` → **Consultar competência**.
2. Na barra de endereço, abra `http://localhost:8090/folha?mes=13&ano=2099`.

**Esperado:**
- passo 1: aviso "A folha de 01/2099 ainda não foi processada" e, na área vazia, o texto **"Esta competência ainda não tem folha processada. Use “Processar folha desta competência” acima…"**. O texto antigo "Cadastre colaboradores para gerar o demonstrativo" **não** pode aparecer;
- passo 2: mensagem "Informe uma competência válida." e, na área vazia, "Selecione um mês e um ano válidos para consultar a folha." Sem erro HTTP 500.

- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

---

## 3. Regressão (CT-036 e CT-037, pedidos para validação final)

1. Com `RT002` processado na competência atual (BUG-004), cadastre `RT003`, nome `Reteste Tres`, salário `1500`, tipo **Salário base**.
2. Em **Folha de pagamento**, consulte um **outro** mês e ano (por exemplo `1` / `2099`). **Esperado:** nenhuma linha de `RT002` nem de `RT003` (CT-036: filtro por mês e ano).
3. Volte à competência atual. **Esperado:** `RT002` continua com **uma** linha e o botão de processamento **não** é mais exibido; `RT003`, cadastrado depois do processamento, **não** aparece nessa folha (a tela só oferece processar competências sem folha). Para ver `RT003`, processe uma competência ainda não processada, como o mês seguinte.
4. No painel, **Excluir** `RT002`. **Esperado:** `RT002` some da lista de colaboradores ativos.
5. Abra de novo a folha da competência atual e clique em `Reteste Dois`. **Esperado:** o demonstrativo continua disponível, sem o botão **Editar cadastro** (CT-037: histórico preservado após exclusão lógica).

- [ ] Aprovado  [ ] Reprovado — Obtido: ______________________________________

---

## 4. Registro do reteste

| Defeito | Executor | Data | Resultado | Observações |
| :--- | :--- | :---: | :---: | :--- |
| BUG-001 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado** | Bruno `04` e Cypress `BUG-001/`: cadastro concluído com campos em branco; `RT001` com R$ 2.000,00 |
| BUG-002 | — | — | **Fora da revisão E2E** | Só a suíte JUnit prova este defeito (3 testes PostgreSQL, 0 ignorados no `clean verify` de 23/11/2026). A revisão E2E não o cobre; repetir `.\mvnw.cmd test -Dtest=RepositorioColaboradoresPostgresTest` se quiser validá-lo pessoalmente |
| BUG-003 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado** | Bruno: 31 requisições HTTP reais, 106 asserções, 0 falhas |
| BUG-004 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado** | Cypress `BUG-004/` e PDF de impressão: Adicionais R$ 0,00; impressão sem menu e botões |
| BUG-005 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado (parte E2E)** | Bruno `24` e Cypress `BUG-005/`: uma linha por colaborador, total inalterado. A comprovação principal em PostgreSQL é do JUnit (23/11/2026) |
| BUG-006 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado** | Bruno `17` e Cypress `BUG-006/`: texto novo presente e texto antigo ausente |
| Regressão CT-036 / CT-037 | Execução automatizada (Bruno CLI + Cypress, 24/11/2026 01:17); evidências revisadas por Amanda Ruwer | 24/11/2026 | **Aprovado** | Bruno `27` a `31` e Cypress `regressao-ct036-ct037.cy.js/` |

**Se algum item reprovar:** não altere o [Registro de Defeitos](Registro_Defeitos.md) para "Validado". Anote o que foi obtido, guarde a captura e volte o defeito para "Em Correção" na Issue correspondente.

**Situação em 24/11/2026:** reteste E2E aprovado por Amanda Ruwer (BUG-001, 003, 004, 005 na parte E2E, 006 e regressão). BUG-002 não é coberto pela revisão E2E.

**Se todos aprovarem:** os defeitos podem ser marcados como **Validados** por quem executou e passam a **Fechados** quando o PR for integrado e as Issues #29 a #34 forem encerradas com o link dele.
