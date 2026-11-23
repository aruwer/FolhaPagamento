# Roteiro de Testes Manuais (Aceitação e Usabilidade)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0
**Casos cobertos:** CT-030 (alternativa manual PostgreSQL), CT-032, CT-033, CT-034, CT-035, CT-038 (visual), CT-040
**Referência:** [`docs/Casos_Teste_Rastreaveis_A1.md`](../docs/Casos_Teste_Rastreaveis_A1.md)

---

## 1. Preparar o ambiente (uma vez por sessão)

No PowerShell, na raiz do projeto:

```powershell
docker compose up -d
```

Depois, execute `.\mvnw.cmd spring-boot:run` e abra **http://localhost:8081**. Caso precise dos dados fictícios `FP001` a `FP010`, aplique `scripts\seed-colaboradores.sql` no PostgreSQL antes dos testes.

Para reiniciar com dados limpos, pare a aplicação e rode `docker compose down -v` antes do script.

## 2. Regras para a execução

- Use somente dados fictícios.
- Registre o resultado de cada caso como **Aprovado**, **Reprovado** ou **Bloqueado**. Bloqueado não é aprovado.
- Para cada passo que pedir evidência, faça a captura com `Win + Shift + S` e salve em `09_Homologacao/evidencias/CT-XXX/` com o nome `CT-XXX-passoN.png`.
- Se um resultado obtido for diferente do esperado, abra um registro em `06_Defeitos` (ou no registro de defeitos do projeto) e anote o identificador `BUG-xxx` no campo de defeito.
- Preencha os campos em branco com a data, o navegador (nome e versão) e o commit (`git rev-parse --short HEAD`).

## 3. Resumo da execução

| Caso | Título | Executor | Data | Navegador | Resultado | Defeito |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CT-030 (manual) | Persistência, histórico e regras no PostgreSQL |  |  |  | Pendente: vídeos anexados não revisados pelo agente | - |
| CT-032 | Fluxo de usabilidade |  |  |  | Pendente: vídeos anexados não revisados pelo agente | - |
| CT-033 | Extensibilidade do domínio (inspeção) |  |  |  | Pendente: conclusão do revisor humano | - |
| CT-034 | Campos conforme a categoria | Amanda Ruwer | 23/11/2026 | Opera | Aprovado: capturas revisadas pelo agente | - |
| CT-035 | Validação compreensível | Amanda Ruwer | 23/11/2026 | Opera | Aprovado: captura revisada pelo agente | - |
| CT-038 | Adicionais zerados (visual) | Amanda Ruwer | 23/11/2026 | Opera | Pendente: captura mostra a folha consolidada, não o demonstrativo individual | BUG-004 |
| CT-040 | Imprimir demonstrativo | Amanda Ruwer | 23/11/2026 | Opera | Pendente: as capturas revisadas mostram a folha consolidada, não o demonstrativo individual | - |

---

## CT-030 — Alternativa manual aos testes PostgreSQL ignorados

Este roteiro verifica manualmente, pela aplicação ligada ao PostgreSQL do Docker Compose, os comportamentos cobertos por `RepositorioColaboradoresPostgresTest`. **Não transforma os testes Testcontainers ignorados em testes automatizados aprovados**; registre o resultado manual e mantenha essa distinção no relatório.

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

- **Resultado geral:** [ ] Aprovado [ ] Reprovado [ ] Bloqueado [X] Pendente de revisão
- **Resultado obtido:** [preencher após revisão dos vídeos CT-030-A/B/C]
- **Executor:** [confirmar após revisão das evidências]
- **Data:** [confirmar após revisão das evidências]
- **Banco/ambiente:** PostgreSQL 17 via Docker Compose (informação registrada pela equipe; não verificada nesta revisão)
- **Observações/defeito:** Os vídeos CT-030-A.mp4, CT-030-B.mp4 e CT-030-C.mp4 estão anexados; conteúdo não revisado pelo agente. A execução Testcontainers consta como ignorada no log automatizado.

### CT-030-D — Persistência após reinício da aplicação

1. Inicie o PostgreSQL pelo Docker Compose e a aplicação na porta 8081.
2. Cadastre um colaborador fictício com matrícula `MANUAL004` e anote os dados usados.
3. Encerre a aplicação sem remover o volume do Docker Compose.
4. Inicie a aplicação novamente e abra a listagem.
5. Confirme se `MANUAL004` e seus dados continuam apresentados.

- **Resultado esperado:** o cadastro persiste após reiniciar a aplicação e o PostgreSQL, sem reaplicar os dados manualmente.
- **Evidência:** [preencher com captura ou vídeo da execução]
- **Resultado obtido:** [preencher: descrever o que apareceu na tela, passo a passo]
- **Situação:** [ ] Aprovado  [ ] Reprovado  [ ] Bloqueado
- **Executor/data/ambiente:** [preencher após execução]
- **Observação:** os vídeos CT-030-A/B/C existentes estão anexados, mas não foram revisados pelo agente e não são considerados evidência revisada deste cenário de reinício.

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

- **Evidência:** `CT-038-passo1.png`
- **Resultado obtido:** A captura CT-038-passo1.png mostra a tabela da folha consolidada e, na linha FP038, adicional exibido como “R$ 0,00” e salário final “R$ 2.000,00”.
- **Situação:** [ ] Aprovado  [ ] Reprovado  [ ] Bloqueado [X] Pendente de evidência correspondente
- **Observações:** __

---

## CT-040 — Imprimir demonstrativo individual

- **Pré-condição:** demonstrativo de `FP038` aberto (ver CT-038).

| Passo | Ação | Resultado esperado |
| :---: | :--- | :--- |
| 1 | Clique em "Imprimir demonstrativo". | A janela de impressão do navegador abre. |
| 2 | Confira a pré-visualização. | Contém matrícula, nome, tipo, salário base, adicionais (com R$ 0,00 explícito) e salário final. Não há botões de navegação na impressão. |

- **Evidência:** `CT-040-passo1.png`, `CT-040-passo2.png`
- **Resultado obtido:** CT-040-passo1.png mostra a folha consolidada e o botão “Imprimir folha”. CT-040-passo2.png mostra a pré-visualização de impressão em duas páginas, com a tabela e sem os controles de navegação do sistema na área impressa. Essas imagens não demonstram a impressão do demonstrativo individual.
- **Situação:** [ ] Aprovado  [ ] Reprovado  [ ] Bloqueado [X] Pendente de revisão humana
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
- **Evidência:** anotações da observação, em `CT-032/`
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

- **Conclusão do revisor:** [preencher após inspeção e validação por revisor humano]
- **Situação:** [ ] Aprovado  [ ] Reprovado  [ ] Bloqueado [X] Pendente
- **Revisor e data:** [preencher]
