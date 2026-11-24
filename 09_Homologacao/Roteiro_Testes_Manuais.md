# Roteiro de Testes Manuais (Aceitação e Usabilidade)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0
**Casos cobertos:** CT-030 (alternativa manual PostgreSQL), CT-032, CT-033, CT-034, CT-035, CT-038 (visual), CT-040
**Referência:** [Casos_Teste_Rastreaveis.md](../04_Testes/Casos_Teste_Rastreaveis.md) · **Consolidação dos resultados:** [Relatorio_Testes_Aceitacao.md](Relatorio_Testes_Aceitacao.md)

---

## 1. Preparar o ambiente (uma vez por sessão)

No PowerShell, na raiz do projeto:

```powershell
docker compose up -d
```

Depois, execute `.\mvnw.cmd spring-boot:run` e abra **http://localhost:8081**. Caso precise dos dados fictícios `FP001` a `FP010`, aplique `06_Banco_Dados\seed-colaboradores.sql` no PostgreSQL antes dos testes.

Para reiniciar com dados limpos, pare a aplicação e rode `docker compose down -v` antes do script.

## 2. Regras para a execução

- Use somente dados fictícios.
- Registre o resultado de cada caso como **Aprovado**, **Reprovado** ou **Bloqueado**. Bloqueado não é aprovado.
- Para cada passo que pedir evidência, faça a captura com `Win + Shift + S` e salve em `09_Homologacao/evidencias/CT-XXX/` com o nome `CT-XXX-passoN.png`.
- Se um resultado obtido for diferente do esperado, abra um registro em [Registro_Defeitos.md](../10_Qualidade/Registro_Defeitos.md) e anote o identificador `BUG-xxx` no campo de defeito.
- Preencha os campos em branco com a data, o navegador (nome e versão) e o commit (`git rev-parse --short HEAD`).

## 3. Resumo da execução

| Caso | Título | Executor | Data | Navegador | Resultado | Defeito |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CT-030 (A/B/C) | Persistência, histórico e regras no PostgreSQL | Equipe (vídeos anexados no commit `9674717`) | 23/11/2026 | Opera | Aprovado (vídeos da equipe + 3 testes Testcontainers aprovados) | - |
| CT-030 (T130) e CT-030-D | Recuperar dados após reinício | Revisão de fechamento A3 | 23/11/2026 | Edge 158 (headless) | Aprovado | - |
| CT-032 | Fluxo de usabilidade | Equipe (observação registrada abaixo; vídeos CT-032-01 a 05) | 23/11/2026 | Opera | Aprovado | - |
| CT-033 | Extensibilidade do domínio (inspeção) | Revisão de fechamento A3 + teste automatizado | 23/11/2026 | - | Aprovado (sujeito à assinatura da revisora) | - |
| CT-034 | Campos conforme a categoria | Amanda Ruwer | 23/11/2026 | Opera | Aprovado | - |
| CT-035 | Validação compreensível | Amanda Ruwer | 23/11/2026 | Opera | Aprovado | - |
| CT-038 | Adicionais zerados (visual) | Revisão de fechamento A3 | 23/11/2026 | Edge 158 (headless) | Aprovado (nova evidência no demonstrativo individual) | BUG-004 (fechado) |
| CT-040 | Imprimir demonstrativo | Revisão de fechamento A3 | 23/11/2026 | Edge 158 (headless) | Aprovado (nova evidência + PDF da impressão) | BUG-004 (fechado) |

---

## CT-030 — Persistência no PostgreSQL (verificação manual complementar)

Este roteiro verifica manualmente, pela aplicação ligada ao PostgreSQL do Docker Compose, os comportamentos cobertos por `RepositorioColaboradoresPostgresTest`. Desde a atualização do Testcontainers para 1.21.4 (BUG-002), esses três testes também são executados automaticamente em PostgreSQL 17 real; o roteiro manual permanece como evidência complementar pela interface.

### Preparar o ambiente

1. Na raiz do projeto, inicie o PostgreSQL:

   ```powershell
   docker compose up -d
   docker compose ps
   ```

   Confirme que `postgres` está `healthy`.
2. Se a aplicação ainda não estiver em execução, abra outro PowerShell e execute:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

3. Abra `http://localhost:8081` no navegador. Use o pgAdmin em `http://localhost:5050` para executar as consultas SQL de verificação.
4. No pgAdmin, confirme o banco e a versão do servidor:

   ```sql
   SELECT current_database(), version();
   ```

   O banco deve ser `folha_pagamento` e o servidor deve ser PostgreSQL 17.
5. Use as matrículas fictícias abaixo, que contêm somente letras e números. Não execute `docker compose down -v`: isso apaga o volume persistente do banco.
6. Crie a pasta de evidências deste caso, se ainda não existir:

   ```powershell
   New-Item -ItemType Directory -Force .\09_Homologacao\evidencias\CT-030
   ```

### CT-030-A — Persistir cadastro e rejeitar matrícula duplicada

1. Cadastre um colaborador com matrícula `MANUAL001`, nome `Teste PostgreSQL`, salário base `2000,00`, tipo **Comissionado**, vendas `1000,00` e comissão `5`.
2. Confirme na listagem que o cadastro foi concluído.
3. Tente cadastrar outro colaborador com a matrícula `MANUAL001` e um nome diferente.
4. Confirme que o formulário informa a duplicidade e não substitui o cadastro original.
5. No pgAdmin, execute:

   ```sql
   SELECT matricula, nome, ativo
   FROM colaboradores
   WHERE matricula = 'MANUAL001';
   ```

   Deve existir exatamente uma linha, com o primeiro nome e `ativo = true`.

**Evidências:** `CT-030-A.mp4`

### CT-030-B — Congelar competência e preservar histórico após alteração/exclusão

1. Cadastre `MANUAL002`, nome `Historico PostgreSQL`, salário base `2500,00`, tipo **Padrão**.
2. Abra **Folha de pagamento**, selecione mês `2` e ano `2099`, e processe a competência.
3. Confirme que a folha de fevereiro mostra `MANUAL002` com salário base e final de `R$ 2.500,00`. Abra o demonstrativo individual.
4. Volte ao painel, edite `MANUAL002`, alterando o nome para `Nome Atualizado` e o salário base para `3000,00`.
5. Consulte novamente fevereiro de 2099. O snapshot e o demonstrativo devem continuar com o nome e salário final antigos (`R$ 2.500,00`).
6. Consulte março de 2099 e processe essa competência. A nova folha deve mostrar `Nome Atualizado` e `R$ 3.000,00`.
7. Exclua `MANUAL002`. Confirme que saiu da lista de colaboradores ativos.
8. Consulte fevereiro de 2099 e abra o demonstrativo de `MANUAL002`; o snapshot histórico deve continuar disponível.
9. No pgAdmin, confirme o estado lógico e os snapshots:

   ```sql
   SELECT matricula, nome, ativo, excluido_em
   FROM colaboradores
   WHERE matricula = 'MANUAL002';

   SELECT matricula_snapshot, nome_snapshot, mes, ano,
          salario_base_snapshot, adicional_calculado, salario_final
   FROM folhas_processadas
   WHERE matricula_snapshot = 'MANUAL002'
   ORDER BY ano, mes;
   ```

   O cadastro deve estar inativo; os snapshots de fevereiro e março devem preservar, respectivamente, o estado antigo e o atualizado.

**Evidências:** `CT-030-B.mp4`.

### CT-030-C — Validar arredondamento e competência inválida

1. Cadastre `MANUAL003`, nome `Precisao PostgreSQL`, salário base `1,00`, tipo **Comissionado**, vendas `0,01` e comissão `50`.
2. Abra **Folha de pagamento**, selecione mês `4` e ano `2099`, e processe a competência.
3. Confirme no demonstrativo que o adicional é `R$ 0,01` e o salário final é `R$ 1,01` (arredondamento HALF_UP do meio centavo).
4. Na barra de endereço, abra `http://localhost:8081/folha?mes=13&ano=2099`.
5. Confirme que a página informa competência inválida e não apresenta erro HTTP 500.

**Evidências:** `CT-030-C.mp4`.

### Registro do resultado manual

Preencha após executar todos os passos. Se algum passo falhar, marque **Reprovado**, guarde a evidência e registre um defeito; se não puder executar, marque **Bloqueado** e explique o motivo.

- **Resultado geral:** [X] Aprovado [ ] Reprovado [ ] Bloqueado
- **Resultado obtido:** vídeos CT-030-A, CT-030-B e CT-030-C registrados pela equipe com os passos acima. Os mesmos comportamentos (duplicidade, congelamento da competência, histórico após exclusão, arredondamento e competência inválida) foram confirmados de forma automatizada em PostgreSQL 17 real: `RepositorioColaboradoresPostgresTest` — 3 testes, 0 falhas ([log](../04_Testes/log-verify.txt)).
- **Executor:** equipe (vídeos) e suíte automatizada
- **Data:** 23/11/2026
- **Banco/ambiente:** PostgreSQL 17 via Docker Compose (vídeos) e PostgreSQL 17 via Testcontainers (suíte)
- **Observações/defeito:** a execução em PostgreSQL real revelou o BUG-005 (offset de `data_processamento`), corrigido antes da homologação.

### CT-030 — Cenário especificado (T130) com reinício

Executado exatamente como na especificação, com a competência 11/2026 (a regra de elegibilidade não inclui meses anteriores à data de cadastro): cadastro de `T130` (produção 1.900,00 + 80 × 3,00), processamento, encerramento do processo Java, reinício e nova consulta.

- **Evidências:** [CT-030-A3-T130-reinicio.txt](evidencias/CT-030/CT-030-A3-T130-reinicio.txt) (passos, respostas HTTP, log de inicialização e consulta SQL) e [captura após o reinício](evidencias/CT-030/CT-030-A3-T130-demonstrativo-apos-reinicio.png)
- **Resultado obtido:** salário final R$ 2.140,00 antes e depois do reinício; uma única linha com matrícula `T130` no banco.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado

### CT-030-D — Persistência após reinício da aplicação

1. Inicie o PostgreSQL pelo Docker Compose e a aplicação na porta 8081.
2. Cadastre um colaborador fictício com matrícula `MANUAL004` e anote os dados usados.
3. Encerre a aplicação sem remover o volume do Docker Compose.
4. Inicie a aplicação novamente e abra a listagem.
5. Confirme se `MANUAL004` e seus dados continuam apresentados.

- **Resultado esperado:** o cadastro persiste após reiniciar a aplicação e o PostgreSQL, sem reaplicar os dados manualmente.
- **Evidência:** [CT-030-D-A3-persistencia-apos-reinicio.txt](evidencias/CT-030/CT-030-D-A3-persistencia-apos-reinicio.txt), [painel após o reinício](evidencias/CT-030/CT-030-D-A3-painel-apos-reinicio.png) e [folha 11/2026 após o reinício](evidencias/CT-030/CT-030-D-A3-folha-11-2026-apos-reinicio.png)
- **Resultado obtido:** cadastrados E001–E004 (três categorias) e processadas as competências 11/2026 e 12/2026; o processo Java foi encerrado (HTTP indisponível) e reiniciado. O Flyway validou as 2 migrations sem reaplicá-las, o painel listou os 3 colaboradores ativos (o excluído E004 não aparece) e a folha 11/2026 manteve o total de R$ 9.600,00 com os 4 snapshots. A consulta SQL confirma os 4 cadastros e os 7 snapshots.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Executor/data/ambiente:** revisão de fechamento A3 · 23/11/2026 · Windows 11, JDK 23.0.1, PostgreSQL 17 (Docker Compose, database dedicada `folha_evidencias`), Edge 158 headless

---

## CT-034 — Exibir campos conforme a categoria selecionada

- **Tela:** Novo colaborador (`/colaboradores/novo`)
- **Pré-condição:** formulário aberto, sem dados preenchidos.

| Passo | Ação | Resultado esperado |
| :---: | :--- | :--- |
| 1 | Selecione "Padrão" em "Tipo de remuneração". | Somente Matrícula, Nome completo e Salário base aparecem. Campos de vendas, comissão, quantidade e valor por unidade ficam ocultos. |
| 2 | Selecione "Comissionado". | Aparecem "Valor das vendas" e "Comissão sobre vendas". Os campos de produção ficam ocultos. |
| 3 | Selecione "Produção". | Aparecem "Quantidade produzida" e "Valor por unidade". Os campos de comissão ficam ocultos. |

- **Evidência:** `CT-034-passo1.png`, `CT-034-passo2.png`, `CT-034-passo3.png`
- **Resultado obtido:** Nas capturas CT-034-passo1.png, CT-034-passo2.png e CT-034-passo3.png, a seleção padrão mostra apenas a observação da categoria padrão; a seleção comissionada mostra os campos de vendas e percentual; e a seleção de produção mostra quantidade e valor por unidade. Os campos variáveis correspondem à categoria selecionada.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Observações:** ____

---

## CT-035 — Explicar erros de campos obrigatórios

- **Tela:** Novo colaborador (`/colaboradores/novo`)
- **Pré-condição:** formulário aberto.

| Passo | Ação | Resultado esperado |
| :---: | :--- | :--- |
| 1 | Deixe "Nome completo" e "Salário base" em branco. | |
| 2 | Clique em "Cadastrar colaborador". | O navegador bloqueia o envio e destaca os campos obrigatórios. A página do formulário continua aberta, e nenhum colaborador é criado. |

- **Evidência:** `CT-035-passo1.png`
- **Resultado obtido:** A captura CT-035-passo1.png mostra matrícula preenchida, nome e salário base vazios e a validação nativa do navegador “Preencha este campo.” junto ao campo de nome.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Observações:** __

---

## CT-038 — Adicionais zerados aparecem como R$ 0,00 (verificação visual)

- **Pré-condição:** cadastre um colaborador comissionado com "Valor das vendas" em branco (ex.: matrícula `FP038`, salário base `2000`, comissão `5`). Processe a folha do mês atual pelo botão "Processar folha desta competência".

| Passo | Ação | Resultado esperado |
| :---: | :--- | :--- |
| 1 | Abra o demonstrativo de `FP038` pela lista de folha. | Matrícula, nome, tipo, salário base, adicional e salário final aparecem. |
| 2 | Observe o campo de adicional. | O valor aparece como **R$ 0,00**, e não vazio ou oculto. |

- **Evidência:** [CT-038-A3-individual-adicional-zero-E004.png](evidencias/CT-038/CT-038-A3-individual-adicional-zero-E004.png) (demonstrativo individual); `CT-038-passo1.png` (folha consolidada, evidência complementar da equipe)
- **Dados:** `E004`, Diego Rocha, comissionado, salário base 2.000,00, vendas e percentual **em branco**, competência 11/2026.
- **Resultado obtido:** o demonstrativo individual exibe matrícula E004, nome, tipo "Comissionado", competência 11/2026, salário base R$ 2.000,00, **Adicionais R$ 0,00** e salário final R$ 2.000,00. Como E004 foi excluído logicamente depois do processamento, a mesma captura demonstra também o CT-037 (snapshot disponível após a exclusão; o botão "Editar cadastro" não aparece para colaborador inativo).
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Observações:** __

---

## CT-040 — Imprimir demonstrativo individual

- **Pré-condição:** demonstrativo individual aberto (executado com `E003`, Carla Dias, produção, 11/2026).

| Passo | Ação | Resultado esperado |
| :---: | :--- | :--- |
| 1 | Clique em "Imprimir demonstrativo". | A janela de impressão do navegador abre. |
| 2 | Confira a pré-visualização. | Contém matrícula, nome, tipo, salário base, adicionais (com R$ 0,00 explícito) e salário final. Não há botões de navegação na impressão. |

- **Evidência:** [tela com o botão "Imprimir demonstrativo"](evidencias/CT-040/CT-040-A3-individual-producao-E003.png), [PDF gerado pela impressão do navegador](evidencias/CT-040/CT-040-A3-impressao-demonstrativo-E003.pdf) e [página 1 do PDF](evidencias/CT-040/CT-040-A3-impressao-demonstrativo-E003-pagina-1.png). `CT-040-passo1.png` e `CT-040-passo2.png` (impressão da folha consolidada) permanecem como evidência complementar.
- **Resultado obtido:** o demonstrativo individual exibe o botão "Imprimir demonstrativo". A saída de impressão (estilos `@media print`) contém matrícula E003, nome, tipo "Por produção", competência 11/2026, salário base R$ 1.800,00, adicionais R$ 300,00 e salário final R$ 2.100,00, **sem** menu de navegação, botões ou rodapé. O adicional zerado explícito usa o mesmo componente validado em CT-038.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Observações:** __

---

## CT-032 — Fluxo de tarefas de usabilidade

Esse caso é observado com uma pessoa que não participou do desenvolvimento. Ela recebe apenas o enunciado abaixo, sem instruções adicionais. Ninguém da equipe deve ajudar durante a tarefa, exceto para anotar.

| Tarefa | Concluiu sem ajuda? | Tempo | Dificuldade observada |
| :--- | :---: | :---: | :--- |
| 1. Cadastrar um colaborador padrão (matrícula `FP132`, Tina Reis, salário 2500) | [X] Sim [ ] Não | 20s | Não |
| 2. Cadastrar um comissionado (`FP133`, Ugo Dias, salário 2000, vendas 10000, comissão 5) | [X] Sim [ ] Não | 30s | Não |
| 3. Cadastrar um colaborador de produção (`FP134`, Vera Luz, salário 1800, quantidade 120, valor 2,50) | [X] Sim [ ] Não | 30s | Não |
| 4. Corrigir um formulário com o nome em branco | [X] Sim [ ] Não | 30s | Não é possível enviar um formulário em branco, mas é possível corrigir dados dos funcionários |
| 5. Abrir o relatório da folha do mês | [X] Sim [ ] Não | 30s | Não |

- **Resultado esperado:** todas as tarefas concluídas, com as dificuldades registradas.
- **Evidência:** anotações da observação (tabela acima) e vídeos [CT-032-01 a CT-032-05](evidencias/CT-032/); captura complementar do [formulário de cadastro](evidencias/CT-032/CT-032-A3-formulario-cadastro.png)
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Observações:** __

---

## CT-033 — Avaliar extensibilidade do domínio (inspeção de código)

Esse caso é uma inspeção, não um teste de execução. Um revisor lê o código e anota quais partes mudariam para incluir um novo tipo de remuneração, por exemplo `BONUS_FIXO`.

| Item a verificar | Arquivo | O que anotar |
| :--- | :--- | :--- |
| Classe base | `Colaborador.java` | Quais métodos precisam ser sobrescritos |
| Cálculo | `ColaboradorComissionado.java`, `ColaboradorProducao.java` | Confirmar que não precisam mudar |
| Validação de formulário | `ColaboradorForm.java` | Quantos `case` novos |
| Persistência | `ColaboradorJpaEntity.java` | Enum e conversão para o domínio |
| Banco | `db/migration/V1__schema.sql` | Restrição `CHECK` por categoria |
| Interface | `colaboradores/form.html`, `colaborador-form.js` | Opção nova e campos visíveis |

**Inspeção realizada em 23/11/2026 (código da versão 2.0.0):**

| Item | Arquivo | Constatação |
| :--- | :--- | :--- |
| Classe base | `Colaborador.java` | Um novo tipo só precisa estender `Colaborador` e implementar 4 métodos abstratos: `getTipo()`, `getCodigoPersistencia()`, `getAdicional()` e `getDetalhesRemuneracao()`. `getSalarioFinal()` é `final` e soma base + adicional para qualquer subtipo. |
| Cálculo | `ColaboradorPadrao`, `ColaboradorComissionado`, `ColaboradorProducao` | **Nenhuma alteração.** Cada fórmula fica isolada no `getAdicional()` da própria subclasse. |
| Serviço e resumo | `FolhaPagamentoService.resumir` | **Nenhuma alteração.** Agrupa por `getTipo()` e soma `getSalarioFinal()` de forma polimórfica. |
| Formulário | `ColaboradorForm.java` | +1 valor no `@Pattern` do tipo, +1 `case` em `construirColaborador()` e +1 ramo em `de()`; novos campos, se o tipo tiver dados próprios. |
| Persistência | `ColaboradorJpaEntity.java` | +1 constante em `TipoColaborador` e +1 `case` em `paraDominio()`. |
| Banco | Nova migration `V3__...sql` | Ampliar as restrições `CHECK` de `tipo_colaborador`/`tipo_snapshot` e de campos por tipo (migrations já aplicadas não são editadas). |
| Interface | `colaboradores/form.html`, `FolhaProcessada.tipoDescricao()` | +1 `<option>`, campos condicionais e rótulo de exibição. |

**Verificação executável:** `ConformidadeRequisitosTest.novoTipoDeRemuneracaoNaoAlteraCalculosExistentes` cria o tipo `BONUS_FIXO` apenas como subclasse no código de teste (salário 2.000,00 + bônus 350,00 = 2.350,00) e confirma que os três tipos existentes continuam produzindo os mesmos valores. Resultado: aprovado ([log](../04_Testes/log-verify.txt)).

- **Conclusão:** o RNF004 é atendido — novos tipos de remuneração são incluídos **sem alterar os cálculos existentes**. As mudanças necessárias são aditivas e restritas a cadastro, mapeamento de persistência, migration e interface.
- **Situação:** [X] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Revisora (PGQ 6.3):** ______________________________ **Data:** ____/____/________
