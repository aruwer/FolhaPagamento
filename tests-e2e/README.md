# Testes de backend (Bruno CLI) e de frontend (Cypress)

Executam a aplicação **real** (JAR + PostgreSQL 17) e salvam as evidências em arquivos, para uma pessoa revisar e decidir se o teste passou. Complementam os 106 testes JUnit (que usam MockMvc/H2 e Testcontainers); não os substituem.

| Ferramenta | O que cobre | Evidência gerada |
| :--- | :--- | :--- |
| **Bruno CLI** (`bruno/`) | Casos de backend por endpoint (seção 2 de [Casos_Teste_Rastreaveis.md](../04_Testes/Casos_Teste_Rastreaveis.md)): cadastro, validações, competência, folha, demonstrativo, alteração, exclusão lógica e histórico. 33 requisições, 112 testes. | `resultado.json` (requisições e respostas completas), `relatorio.html`, `resultado.xml` (JUnit), `saida.txt` |
| **Cypress** (`cypress/e2e/`) | Casos de tela: CT-034, CT-035, CT-010 (UI), CT-036, CT-037, o reteste dos BUG-001, 004, 005 e 006 e o BUG-007 (comissão acima de 100%). | capturas de tela (`.png`) por passo e vídeo (`.mp4`) de cada spec |

Tudo é gravado em [`09_Homologacao/evidencias-auto/`](../09_Homologacao/evidencias-auto/).

## O que não é automatizado (e por quê)

- **CT-032 (usabilidade):** mede uma pessoa usando o sistema sem ajuda; script não prova isso.
- **CT-033 (extensibilidade):** inspeção de código, coberta por teste JUnit.
- **CT-040 (diálogo de impressão):** o Cypress não abre o diálogo nativo. O spec emula a mídia `print` e confere o que fica oculto; o PDF real da impressão está em `09_Homologacao/evidencias/CT-040/`.
- **CT-030 com reinício da aplicação:** exige parar e subir o processo; está em `09_Homologacao/evidencias/CT-030/`.

## Como executar

Pré-requisitos: Docker Desktop em execução, `docker compose up -d` na raiz, JAR gerado (`.\mvnw.cmd clean package`), Node.js 22 ou superior.

```powershell
cd tests-e2e
npm install                              # uma vez
.\scripts\run-all.ps1                    # Bruno + Cypress
.\scripts\run-all.ps1 -SoBruno           # só backend
.\scripts\run-all.ps1 -SoCypress         # só frontend
```

O script recria a database **`folha_reteste`** (não toca em `folha_pagamento`), sobe o JAR na porta 8090, roda os testes, salva as evidências e encerra a aplicação. O código de saída é diferente de zero se algum teste falhar.

Para rodar manualmente contra uma aplicação já em execução:

```powershell
cd tests-e2e\bruno
..\node_modules\.bin\bru run --env local --env-var baseUrl=http://localhost:8090 --env-var mes=11 --env-var ano=2026 --env-var anoOutro=2025
cd ..
$env:BASE_URL = "http://localhost:8090"; .\node_modules\.bin\cypress.cmd open
```

> Use sempre o binário local (`node_modules\.bin\bru`). Rodar `npx bru` **fora** desta pasta baixa do registro um pacote diferente chamado `bru`, que não é o Bruno CLI.

> **Cypress dentro do VS Code:** se aparecer `bad option: --smoke-test`, a variável `ELECTRON_RUN_AS_NODE` está definida no terminal (o VS Code a define). O `run-all.ps1` já a remove; no terminal manual use `Remove-Item Env:ELECTRON_RUN_AS_NODE`.

Os dados (`BR…`, `CY…`, `RT001`) são gravados na database de teste, que é recriada a cada execução. A coleção Bruno usa competência do **mês atual**, porque um colaborador só entra na folha de meses a partir do cadastro.

## Como revisar as evidências

1. Abra `evidencias-auto/bruno/relatorio.html` e confira quais requisições/asserções passaram; o `resultado.json` traz a resposta completa de cada requisição.
2. Abra as capturas em `evidencias-auto/cypress/screenshots/<spec>/<caso>/` e assista aos vídeos em `cypress/videos/`.
3. Registre o resultado e seu nome no [Roteiro de Reteste](../10_Qualidade/Roteiro_Reteste_Bugs.md). Uma execução automatizada **não** valida o defeito sozinha; quem valida é a pessoa que revisa a evidência.

## Estrutura

```text
tests-e2e/
├── bruno/              coleção Bruno (bruno.json, environments/local.bru, *.bru)
├── cypress/e2e/        specs do Cypress
├── cypress.config.js   pastas de evidência, baseUrl, viewport
├── scripts/run-all.ps1 orquestra database, aplicação, Bruno e Cypress
└── package.json        Cypress e @usebruno/cli (devDependencies)
```
