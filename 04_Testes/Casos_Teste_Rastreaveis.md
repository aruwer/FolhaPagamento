# Casos de Teste Rastreáveis

**Projeto:** Sistema de Folha de Pagamento  
**Versão-alvo:** QDS_2.0.0<br>
**Tarefa:** [Testes][A1] Especificar no mínimo 20 casos de teste rastreáveis  
**Status:** 40 casos especificados (A1) e **executados na A3 — 40 aprovados, 0 reprovados, 0 bloqueados** (registro na seção 6)

## 1. Convenções

- Os IDs `CT-NNN` são estáveis e devem ser usados no registro de execução e nas issues.
- Prioridades: **P0** bloqueadora para aceitação; **P1** alta; **P2** padrão/complementar. Prioridade ordena execução e risco, não transforma requisito da ERS em opcional.
- Níveis: **U** unitário; **I** integração; **S** sistema; **A** aceitação.
- **Automação:** `Sim` indica candidato a JUnit/Spring Boot Test; `Manual` indica navegador ou validação/aceitação manual. A ferramenta final depende da arquitetura implementada.
- `RF`, `RN` e `RNF` referem-se aos identificadores de [ERS](../01_Requisitos/Documento_Visao_e_ERS.md), fonte única dos requisitos para estes casos.
- Os requisitos da ERS são critérios desta especificação. Um comportamento complementar não descrito na ERS é identificado como tal e não é contabilizado como cobertura de requisito.
- Pré-condição geral: versão/commit identificado, ambiente de teste isolado e uso de dados fictícios. Para casos de UI, aplicação iniciada e navegador registrado.
- Em cada cenário, `Scenario`, `Given`, `When` e `Then` são palavras-chave Gherkin em inglês; nomes, contexto, ação e resultado estão em português.
- Variáveis como `matricula`, `salarioBase` e `salarioFinalEsperado` são sugestões de nomes para dados e asserções. Para casos automatizados, criar novos dados por teste e usar arquivo temporário quando houver persistência.
- Valores monetários estão expressos com ponto decimal para facilitar leitura e automação; adaptar ao formato de entrada da interface quando necessário.
- O resultado de execução é registrado por caso como **aprovado**, **reprovado** ou **bloqueado**, com versão, executor, ambiente e evidência (seção 6); a aprovação nunca é inferida apenas da implementação.

## 2. Backend - testes por endpoint

Os cenários desta seção exercitam o backend por requisições HTTP aos endpoints MVC reais, usando formulário `application/x-www-form-urlencoded` e verificando redirect, página HTML, dados exibidos e ausência de alteração indevida. O método e o caminho constam no campo **Endpoint** de cada caso e devem ser usados na execução; não substituir a requisição por chamada direta ao service ou ao domínio. Para automação, usar MockMvc com o contexto Spring; alternativamente, executar contra a aplicação local isolada. Os endpoints retornam páginas Thymeleaf, não JSON.

Em cada caso está indicado o método e caminho HTTP usado. Nos cadastros válidos, o POST deve redirecionar para `/`; o teste pode então consultar o endpoint GET indicado para conferir o resultado. Nos casos inválidos, espera-se retorno à tela do formulário com erros e sem persistência; resposta 5xx é falha do endpoint. A competência é informada pelos parâmetros `mes` e `ano` em `GET /folha`, `POST /folha/processar` e `GET /folha/{matricula}`. Onde o texto original da A1 propunha outra rota (por exemplo `GET /?mes=...`), a execução usou essas rotas implementadas.

### CT-001 - Calcular salário padrão

**Requisitos:** RF001, RF002, RF006 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/A100`.

**Variáveis sugeridas:** `matricula = "A100"`; `nome = "Ana Lima"`; `salarioBase = 2500.00`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: calcular salário de colaborador padrão
	Given que existe um colaborador padrão com matrícula "A100", nome "Ana Lima" e salário base 2500.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/A100` deve exibir adicional 0.00 e salário final 2500.00
```

### CT-002 - Aceitar salário base zero

**Requisitos:** RF002, RN003, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/A102`.

**Variáveis sugeridas:** `matricula = "A102"`; `salarioBase = 0.00`; `salarioFinalEsperado = 0.00`.

```gherkin
Scenario: calcular salário padrão com base zero
	Given que existe um colaborador padrão com salário base 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/A102` deve exibir salário final 0.00 sem adicional
```

### CT-003 - Calcular comissão positiva

**Requisitos:** RF003, RF006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C100`.

**Variáveis sugeridas:** `matricula = "C100"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `comissaoEsperada = 500.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: calcular remuneração de colaborador comissionado
	Given que existe um colaborador com base 2000.00, vendas de 10000.00 e comissão de 5.00 por cento
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C100` deve exibir comissão 500.00 e salário final 2500.00
```

### CT-004 - Aceitar vendas zero

**Requisitos:** RF003, RN004, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C104`.

**Variáveis sugeridas:** `matricula = "C104"`; `salarioBase = 2000.00`; `valorVendas = 0.00`; `percentualComissao = 5.00`; `comissaoEsperada = 0.00`; `salarioFinalEsperado = 2000.00`.

```gherkin
Scenario: calcular comissão com vendas zero
	Given que existe um colaborador comissionado com base 2000.00, vendas 0.00 e percentual 5.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C104` deve exibir comissão 0.00 e salário final 2000.00
```

### CT-005 - Aceitar percentual de comissão zero

**Requisitos:** RF003, RN005, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C105`.

**Variáveis sugeridas:** `matricula = "C105"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 0.00`; `comissaoEsperada = 0.00`; `salarioFinalEsperado = 2000.00`.

```gherkin
Scenario: calcular comissão com percentual zero
	Given que existe um colaborador comissionado com base 2000.00, vendas 10000.00 e percentual 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C105` deve exibir comissão 0.00 e salário final 2000.00
```

### CT-006 - Calcular remuneração por produção

**Requisitos:** RF004, RF006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P100`.

**Variáveis sugeridas:** `matricula = "P100"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 2.50`; `adicionalEsperado = 300.00`; `salarioFinalEsperado = 2100.00`.

```gherkin
Scenario: calcular remuneração por produção
	Given que existe um colaborador com base 1800.00, produção de 120 unidades e valor unitário 2.50
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P100` deve exibir adicional 300.00 e salário final 2100.00
```

### CT-007 - Aceitar quantidade produzida zero

**Requisitos:** RF004, RN006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P107`.

**Variáveis sugeridas:** `matricula = "P107"`; `salarioBase = 1800.00`; `quantidadeProduzida = 0.00`; `valorPorUnidade = 2.50`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 1800.00`.

```gherkin
Scenario: calcular produção com quantidade zero
	Given que existe um colaborador de produção com base 1800.00, quantidade 0 e valor unitário 2.50
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P107` deve exibir adicional 0.00 e salário final 1800.00
```

### CT-008 - Aceitar valor por unidade zero

**Requisitos:** RF004, RN007, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P108`.

**Variáveis sugeridas:** `matricula = "P108"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 0.00`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 1800.00`.

```gherkin
Scenario: calcular produção com valor unitário zero
	Given que existe um colaborador de produção com base 1800.00, quantidade 120 e valor unitário 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P108` deve exibir adicional 0.00 e salário final 1800.00
```

### CT-009 - Rejeitar matrícula duplicada

**Requisitos:** RF001, RF009, RN001 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` duas vezes com a mesma matrícula; conferir em `GET /`.

**Variáveis sugeridas:** `matricula = "A101"`; `nomeOriginal = "Bruno Alves"`; `nomeDuplicado = "Bruna Alves"`; `salarioBase = 2100.00`; `quantidadeInicialEsperada = 1`.

```gherkin
Scenario: rejeitar cadastro com matrícula já utilizada
	Given que o repositório contém o colaborador "A101" com nome "Bruno Alves"
	When enviar os dados duplicados por `POST /colaboradores`
	Then o cadastro duplicado deve ser rejeitado e o registro original deve permanecer inalterado
```

### CT-010 - Rejeitar matrícula com pontuação no endpoint (complementar)

**Requisitos:** RF001, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P2 | **Execução:** MockMvc ou HTTP local
**Endpoint:** enviar diretamente `matricula=A-102` por `POST /colaboradores`, sem executar JavaScript; consultar `GET /` para confirmar ausência do registro. É uma validação complementar do formulário, não uma regra de formato atribuída à RN001.

**Variáveis sugeridas:** `matriculaInvalida = "A-102"`; `nome = "Caio Reis"`; `tipo = "PADRAO"`; `salarioBase = 2200.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar matrícula com pontuação enviada diretamente ao servidor
	Given que não existe colaborador com matrícula "A-102"
	When enviar os dados válidos restantes e `matricula=A-102` por `POST /colaboradores`
	Then o formulário deve indicar matrícula inválida, não responder com erro 5xx e não persistir o registro; consultar `GET /` para confirmar
```

### CT-011 - Rejeitar matrícula vazia

**Requisitos:** RF001, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `matricula=`.

**Variáveis sugeridas:** `matricula = ""`; `nome = "Dora Silva"`; `tipo = "PADRAO"`; `salarioBase = 2300.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar matrícula em branco
	Given que o formulário de cadastro contém matrícula vazia e os demais campos válidos
	When a pessoa envia o formulário
	Then o campo obrigatório deve ser indicado e o cadastro não deve ser concluído
```

### CT-012 - Rejeitar nome vazio

**Requisitos:** RF001, RN002, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `nome=` ou `nome=   `.

**Variáveis sugeridas:** `matricula = "A112"`; `nome = "   "`; `tipo = "PADRAO"`; `salarioBase = 2300.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro sem nome
	Given que o formulário contém nome composto apenas por espaços e os demais campos válidos
	When a pessoa envia o formulário
	Then o nome obrigatório deve ser indicado e o colaborador não deve ser salvo
```

### CT-013 - Rejeitar salário base ausente

**Requisitos:** RF001, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` sem o parâmetro `salarioBase`.

**Variáveis sugeridas:** `matricula = "A113"`; `nome = "Eva Nunes"`; `tipo = "PADRAO"`; `salarioBase = null`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro sem salário base
	Given que o formulário contém matrícula e nome válidos, mas não contém salário base
	When a pessoa envia o formulário
	Then o campo salário base deve ser indicado e nenhum cadastro parcial deve ser persistido
```

### CT-014 - Rejeitar salário base negativo

**Requisitos:** RF002, RN003, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `salarioBase=-0.01`.

**Variáveis sugeridas:** `matricula = "A114"`; `nome = "Fabio Melo"`; `salarioBase = -0.01`; `tipo = "PADRAO"`.

```gherkin
Scenario: rejeitar salário base negativo
	Given que os dados do colaborador padrão incluem salário base -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-015 - Vendas em branco no cadastro comissionado são tratadas como zero (RN010)

**Requisitos:** RF003, RN010, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local
**Endpoint:** `POST /colaboradores` com `tipo=COMISSIONADO` e sem `valorVendas`.

**Variáveis sugeridas:** `matricula = "C115"`; `nome = "Gabi Luz"`; `salarioBase = 2000.00`; `valorVendas = null`; `percentualComissao = 5.00`; `quantidadePersistidaEsperada = 1`.

```gherkin
Scenario: tratar valor de vendas em branco como zero no cadastro comissionado
	Given que o formulário comissionado contém base 2000.00 e percentual 5.00, mas vendas não informadas
	When a pessoa envia o formulário
	Then o cadastro deve ser concluído, o adicional (ou a comissão) deve aparecer como R$ 0,00 e o salário final deve ser igual à base
```

### CT-016 - Percentual em branco no cadastro comissionado é tratado como zero (RN010)

**Requisitos:** RF003, RN010, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local
**Endpoint:** `POST /colaboradores` com `tipo=COMISSIONADO` e sem `percentualComissao`.

**Variáveis sugeridas:** `matricula = "C116"`; `nome = "Hugo Paz"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = null`; `quantidadePersistidaEsperada = 1`.

```gherkin
Scenario: tratar percentual em branco como zero no cadastro comissionado
	Given que o formulário comissionado contém base 2000.00 e vendas 10000.00, mas percentual não informado
	When a pessoa envia o formulário
	Then o cadastro deve ser concluído, o adicional (ou a comissão) deve aparecer como R$ 0,00 e o salário final deve ser igual à base
```

### CT-017 - Rejeitar valor de vendas negativo

**Requisitos:** RF003, RN004, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `valorVendas=-0.01`.

**Variáveis sugeridas:** `salarioBase = 2000.00`; `valorVendas = -0.01`; `percentualComissao = 5.00`.

```gherkin
Scenario: rejeitar valor de vendas negativo
	Given que os dados do colaborador comissionado incluem vendas -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-018 - Rejeitar percentual de comissão negativo

**Requisitos:** RF003, RN005, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `percentualComissao=-0.01`.

**Variáveis sugeridas:** `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = -0.01`.

```gherkin
Scenario: rejeitar percentual de comissão negativo
	Given que os dados do colaborador comissionado incluem percentual -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-019 - Quantidade produzida em branco é tratada como zero (RN010)

**Requisitos:** RF004, RN010, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local
**Endpoint:** `POST /colaboradores` com `tipo=PRODUCAO` e sem `quantidadeProduzida`.

**Variáveis sugeridas:** `matricula = "P119"`; `nome = "Iara Costa"`; `salarioBase = 1800.00`; `quantidadeProduzida = null`; `valorPorUnidade = 2.50`; `quantidadePersistidaEsperada = 1`.

```gherkin
Scenario: tratar quantidade produzida em branco como zero
	Given que o formulário de produção contém base 1800.00 e valor unitário 2.50, mas quantidade não informada
	When a pessoa envia o formulário
	Then o cadastro deve ser concluído, o adicional (ou a comissão) deve aparecer como R$ 0,00 e o salário final deve ser igual à base
```

### CT-020 - Valor por unidade em branco é tratado como zero (RN010)

**Requisitos:** RF004, RN010, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local
**Endpoint:** `POST /colaboradores` com `tipo=PRODUCAO` e sem `valorPorUnidade`.

**Variáveis sugeridas:** `matricula = "P120"`; `nome = "Joao Luz"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = null`; `quantidadePersistidaEsperada = 1`.

```gherkin
Scenario: tratar valor por unidade em branco como zero
	Given que o formulário de produção contém base 1800.00 e quantidade 120, mas valor unitário não informado
	When a pessoa envia o formulário
	Then o cadastro deve ser concluído, o adicional (ou a comissão) deve aparecer como R$ 0,00 e o salário final deve ser igual à base
```

### CT-021 - Rejeitar quantidade produzida negativa

**Requisitos:** RF004, RN006, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `quantidadeProduzida=-1`.

**Variáveis sugeridas:** `salarioBase = 1800.00`; `quantidadeProduzida = -1.00`; `valorPorUnidade = 2.50`.

```gherkin
Scenario: rejeitar quantidade produzida negativa
	Given que os dados do colaborador de produção incluem quantidade -1
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-022 - Rejeitar valor por unidade negativo

**Requisitos:** RF004, RN007, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `valorPorUnidade=-0.01`.

**Variáveis sugeridas:** `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = -0.01`.

```gherkin
Scenario: rejeitar valor por unidade negativo
	Given que os dados do colaborador de produção incluem valor unitário -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-023 - Listar colaboradores das três categorias

**Requisitos:** RF005, RF009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar com `POST /colaboradores`; consultar a lista em `GET /`.

**Variáveis sugeridas:** `matriculaPadrao = "L123"`; `nomePadrao = "Lia Dias"`; `salarioBasePadrao = 1500.00`; `matriculaComissionado = "L124"`; `nomeComissionado = "Luca Reis"`; `salarioBaseComissionado = 1800.00`; `valorVendas = 4000.00`; `percentualComissao = 5.00`; `matriculaProducao = "L125"`; `nomeProducao = "Mia Sol"`; `salarioBaseProducao = 1600.00`; `quantidadeProduzida = 20.00`; `valorPorUnidade = 3.00`; `quantidadeEsperada = 3`.

```gherkin
Scenario: listar colaboradores de todas as categorias
	Given que o repositório contém um colaborador padrão, um comissionado e um de produção com matrículas distintas
	When consultar a lista por `GET /`
	Then os três colaboradores devem aparecer uma vez cada, com identificação, categoria e dados remuneratórios correspondentes
```

### CT-024 - Atualizar colaborador

**Requisitos:** RF009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A201`; conferir em `GET /folha/A201`.

**Variáveis sugeridas:** `matricula = "A201"`; `nomeOriginal = "Nina Reis"`; `nomeAtualizado = "Nina Reis Silva"`; `salarioBaseOriginal = 2000.00`; `salarioBaseAtualizado = 2300.00`.

```gherkin
Scenario: atualizar dados de colaborador existente
	Given que o repositório contém "A201" com nome "Nina Reis" e base 2000.00
	When enviar os dados atualizados por `POST /colaboradores/A201`
	Then consultar `GET /folha/A201` deve exibir os valores atualizados e apenas um registro com essa matrícula em `GET /`
```

### CT-025 - Excluir colaborador da consulta ativa

**Requisitos:** RF009, RN009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A202/excluir`; confirmar ausência em `GET /`.

**Variáveis sugeridas:** `matricula = "A202"`; `nome = "Otto Lima"`; `salarioBase = 2400.00`; `quantidadeEsperadaAposExclusao = 0`.

```gherkin
Scenario: excluir colaborador existente
	Given que o repositório contém o colaborador "A202"
	When enviar a exclusão por `POST /colaboradores/A202/excluir`
	Then consultar `GET /` não deve exibir o colaborador "A202"
```

### CT-026 - Conferir folha consolidada

**Requisitos:** RF007, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar dados por `POST /colaboradores`; consultar relatório em `GET /folha`. O endpoint atual não aceita filtro de competência.

**Variáveis sugeridas:** `matriculaPadrao = "R126"`; `nomePadrao = "Rita Alves"`; `salarioBasePadrao = 2500.00`; `salarioFinalPadraoEsperado = 2500.00`; `matriculaComissionado = "R127"`; `nomeComissionado = "Rui Melo"`; `salarioBaseComissionado = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `salarioFinalComissionadoEsperado = 2500.00`; `matriculaProducao = "R128"`; `nomeProducao = "Sonia Luz"`; `salarioBaseProducao = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 2.50`; `salarioFinalProducaoEsperado = 2100.00`.

```gherkin
Scenario: exibir colaboradores na folha consolidada
	Given que existem colaboradores padrão, comissionado e de produção com os valores esperados preparados
	When consultar a folha consolidada por `GET /folha`
	Then cada colaborador deve aparecer com matrícula, nome, tipo de colaborador e salário final corretos para a competência consultada
```

### CT-027 - Conferir folha individual

**Requisitos:** RF010, RN010, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar por `POST /colaboradores`; consultar em `GET /folha/C100`. A rota atual não aceita competência; o filtro mensal/anual é coberto separadamente por CT-036.

**Variáveis sugeridas:** `matricula = "C100"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `adicionalEsperado = 500.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: exibir folha individual de colaborador comissionado
	Given que existe o colaborador "C100" com base 2000.00, vendas 10000.00 e percentual 5.00
	When consultar `GET /folha/C100`
	Then identificação, base, comissão de 500.00 e salário final de 2500.00 devem corresponder aos dados e à fórmula aprovada
```

### CT-028 - Somar salários finais

**Requisitos:** RF008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar colaboradores por `POST /colaboradores`; conferir total em `GET /folha`. A rota atual não aceita competência; o filtro mensal/anual é coberto separadamente por CT-036.

**Variáveis sugeridas:** `salariosFinais = [2500.00, 2100.00, 1800.00]`; `totalEsperado = 6400.00`.

```gherkin
Scenario: calcular total geral da folha
	Given que a folha contém salários finais de 2500.00, 2100.00 e 1800.00
	When consultar `GET /folha`
	Then o total da folha deve ser 6400.00
```

### CT-029 - Conferir resumo por categoria

**Requisitos:** RF011 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar colaboradores por `POST /colaboradores`; conferir resumo em `GET /`.

**Variáveis sugeridas:** `salariosPadrao = [1000.00, 1500.00]`; `salarioComissionado = 2000.00`; `salarioProducao = 2200.00`; `quantidadeEsperada = 4`; `totalPadraoEsperado = 2500.00`; `totalComissionadoEsperado = 2000.00`; `totalProducaoEsperado = 2200.00`; `totalGeralEsperado = 6700.00`.

```gherkin
Scenario: calcular totais e quantidade por categoria
	Given que o repositório contém dois colaboradores padrão de 1000.00 e 1500.00, um comissionado de 2000.00 e um de produção de 2200.00
	When consultar o resumo por `GET /`
	Then a quantidade deve ser 4, o total geral 6700.00 e os totais por categoria 2500.00, 2000.00 e 2200.00 respectivamente
```

### CT-030 - Recuperar dados persistidos

**Requisitos:** RNF003 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** HTTP local com banco SQL isolado  
**Endpoint proposto:** cadastrar com `POST /colaboradores`; após reinício, consultar `GET /folha/T130?mes=3&ano=2026`. A persistência atual é PostgreSQL; a consulta por competência está disponível pela rota de folha.

**Variáveis sugeridas:** `bancoTeste = banco SQL exclusivo e descartável`; `matricula = "T130"`; `nome = "Sara Melo"`; `salarioBase = 1900.00`; `quantidadeProduzida = 80.00`; `valorPorUnidade = 3.00`; `salarioFinalEsperado = 2140.00`.

**Situação:** executado em 23/11/2026 — **aprovado**. Cenário T130 executado com reinício real da aplicação (competência 11/2026, pois a regra de elegibilidade não inclui meses anteriores ao cadastro): salário final 2140.00 antes e depois do reinício, matrícula única no banco. Evidência: `09_Homologacao/evidencias/CT-030/CT-030-A3-T130-reinicio.txt`.

```gherkin
Scenario: persistir colaborador em SQL após reiniciar a aplicação
	Given que o banco SQL de teste está vazio e a aplicação usa esse banco
	When cadastrar "T130" por `POST /colaboradores`, processar a competência 03/2026, reiniciar a aplicação e consultar `GET /folha/T130?mes=3&ano=2026`
	Then a conexão configurada deve ser SQL e os dados/folha devem ser recuperados do banco após o reinício, com salário final 2140.00 e uma única matrícula
```

### CT-031 - Gerar resumo sem colaboradores

**Requisitos:** RF008, RF011 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `GET /` com repositório vazio.

**Variáveis sugeridas:** `colaboradores = []`; `quantidadeEsperada = 0`; `totalEsperado = 0.00`; `categoriasEsperadas = {}`.

```gherkin
Scenario: gerar resumo de folha vazia
	Given que não existem colaboradores cadastrados
	When consultar o resumo por `GET /`
	Then a quantidade deve ser 0, o total deve ser 0.00 e não deve haver totais residuais por categoria
```

### CT-036 - Filtrar folha por mês e ano

**Requisitos:** RF006, RF007, RF008, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint proposto:** preparar competências via `POST /colaboradores`; consultar `GET /folha?mes=3&ano=2026`.

**Variáveis sugeridas:** `competenciaAlvo = 03/2026`; `competenciasDistratoras = [04/2026, 03/2025]`; `salarioFinalAlvoEsperado = 2500.00`; `totalAlvoEsperado = 2500.00`.

**Situação:** executado em 23/11/2026 — **aprovado** (`ct036FiltraPorCompetencia`, `CompetenciaEBordasTest` e evidências em `09_Homologacao/evidencias/CT-036/`).

```gherkin
Scenario: gerar folha somente para o mês e ano solicitados
	Given que existem lançamentos de folha em 03/2026, 04/2026 e 03/2025
	When consultar `GET /folha?mes=3&ano=2026`
	Then a folha deve incluir somente os lançamentos de 03/2026, exibir matrícula, nome, tipo e salário final de cada colaborador dessa competência e apresentar o total 2500.00 sem dados dos outros períodos
```

**Nota de contrato:** a ERS exige filtro por mês/ano, mas não define nomes de parâmetros nem URL. `mes` e `ano` são a proposta usada neste caso e precisam coincidir com a implementação antes da execução.

### CT-037 - Preservar histórico após exclusão lógica

**Requisitos:** RF005, RF007, RF009, RN009 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A237/excluir`; verificar colaborador ativo por `GET /` e folha histórica pela rota mensal/anual proposta `GET /folha?mes=2&ano=2026`.

**Variáveis sugeridas:** `matricula = "A237"`; `competenciaAnterior = 02/2026`; `salarioFinalHistoricoEsperado = 2500.00`; `visivelNaListaAtiva = false`.

**Situação:** executado em 23/11/2026 — **aprovado**. O cadastro sai da lista ativa e a folha anterior continua íntegra (`ct037PreservaSnapshotDepoisDaExclusao`, teste PostgreSQL `congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete` e evidência visual em `09_Homologacao/evidencias/CT-038/`).

```gherkin
Scenario: excluir cadastro sem apagar a folha já processada
	Given que "A237" consta no cadastro ativo e na folha processada de 02/2026
	When excluir "A237" por `POST /colaboradores/A237/excluir`
	Then `GET /` não deve listar o cadastro ativo, mas `GET /folha?mes=2&ano=2026` deve preservar a matrícula, os dados históricos e o salário final de 2500.00
```

**Nota de contrato:** nomes dos parâmetros de competência são proposta, pois a ERS não especifica a URL do filtro.

### CT-038 - Exibir adicionais zerados em relatório e recibo

**Requisitos:** RF010, RN010, critério de aceitação | **Nível:** Sistema/Integração HTTP | **Prioridade:** P0 | **Execução:** navegador e HTTP local  
**Endpoint:** consultar os demonstrativos individuais `GET /folha/A238`, `GET /folha/A239`, `GET /folha/P238` e `GET /folha/P239`. A impressão atual é o botão da própria página; não há endpoint de recibo separado.

**Variáveis sugeridas:** colaboradores comissionados `A238` (vendas nulas/em branco) e `A239` (vendas zero); colaboradores de produção `P238` (quantidade nula/em branco) e `P239` (quantidade zero); `adicionalEsperado = R$ 0,00` em todos os relatórios.

```gherkin
Scenario: apresentar valores variáveis zero como zero no relatório
	Given que "A239" tem vendas zero e "P239" tem quantidade produzida zero
	When consultar `GET /folha/A239` e `GET /folha/P239`
	Then cada página deve exibir matrícula, nome, tipo, salário base, adicional e salário final, com o adicional explicitamente formatado como R$ 0,00
```

```gherkin
Scenario: tratar valores variáveis em branco como zero no relatório
	Given que o formulário de colaborador comissionado ou de produção é enviado sem o respectivo valor variável
	When o servidor processar o cadastro e abrir o demonstrativo individual
	Then a ausência deve ser tratada como zero e o adicional deve aparecer como R$ 0,00, conforme RN010
```

**Resolvida:** campos variáveis em branco são tratados como R$ 0,00 (RN010). Verificar no navegador antes de registrar o resultado.

### CT-039 - Resumir folha por categoria e competência

**Requisitos:** RF008, RF011, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint proposto:** preparar lançamentos via `POST /colaboradores`; consultar `GET /?mes=3&ano=2026`. A rota atual `/` não aceita nem processa filtro de competência.

**Variáveis sugeridas:** `quantidadeEsperada = 3`; `totalPadraoEsperado = 2500.00`; `totalComissionadoEsperado = 2500.00`; `totalProducaoEsperado = 2100.00`; `totalGeralEsperado = 7100.00`; `competenciasDistratoras = [04/2026, 03/2025]`.

**Situação:** executado em 23/11/2026 — **aprovado** (`ct039ResumePorCategoriaECompetencia`, via `GET /folha?mes=3&ano=2099` com competências distratoras 04/2099 e 03/2098).

```gherkin
Scenario: exibir totais e quantidade apenas da competência selecionada
	Given que existem colaboradores das três categorias em 03/2026 e lançamentos de outras competências
	When consultar o resumo por `GET /?mes=3&ano=2026`
	Then exibir quantidade 3, totais por categoria de 2500.00, 2500.00 e 2100.00 e total geral 7100.00, excluindo outros meses e anos
```

**Nota de contrato:** a ERS exige filtro mensal/anual, mas não define nomes de parâmetros nem URL. `mes` e `ano` são a proposta usada neste caso.

## 3. Frontend - testes pelo navegador

Os cenários desta seção verificam conteúdo, interação e comportamento observável da interface em navegador. Eles não substituem os testes backend por endpoint da seção anterior. Registrar navegador, versão, viewport e evidências; usar dados fictícios.

### CT-032 - Avaliar fluxo de usabilidade

**Requisitos:** RNF001 | **Nível:** Sistema/Aceitação | **Prioridade:** P1 | **Execução:** Manual no navegador

**Variáveis sugeridas:** `colaboradorPadrao = {matricula: "U132", nome: "Tina Reis", salarioBase: 2500.00}`; `colaboradorComissionado = {matricula: "U133", nome: "Ugo Dias", salarioBase: 2000.00, valorVendas: 10000.00, percentualComissao: 5.00}`; `colaboradorProducao = {matricula: "U134", nome: "Vera Luz", salarioBase: 1800.00, quantidadeProduzida: 120.00, valorPorUnidade: 2.50}`; `formularioInvalido = colaboradorPadrao com nome vazio`; `tarefas = [cadastrar padrão, cadastrar comissionado, cadastrar produção, corrigir formulário inválido, abrir relatório]`; `navegador = registrar nome e versão`; `observacoes = registrar por tarefa`.

```gherkin
Scenario: executar roteiro de tarefas de interface
	Given que a pessoa participante recebe o roteiro e dados fictícios sem instruções que induzam respostas
	When ela cadastra cada categoria, corrige um formulário inválido e consulta os relatórios
	Then a conclusão de cada tarefa, as dificuldades observadas e as evidências devem ser registradas para avaliação
```

### CT-034 - Exibir campos conforme categoria selecionada

**Requisitos:** RF003, RF004, RNF001 | **Nível:** Sistema/UI | **Prioridade:** P1 | **Execução:** Manual no navegador
**Tela:** `GET /colaboradores/novo`.

**Variáveis sugeridas:** `categorias = [PADRAO, COMISSIONADO, PRODUCAO]`; `camposComissionado = [valorVendas, percentualComissao]`; `camposProducao = [quantidadeProduzida, valorPorUnidade]`.

```gherkin
Scenario: atualizar campos variáveis ao selecionar categoria
	Given que a tela `GET /colaboradores/novo` está aberta no navegador
	When selecionar PADRAO, COMISSIONADO e PRODUCAO no campo Tipo de remuneração
	Then devem aparecer apenas os campos e a orientação correspondentes à categoria selecionada, e somente os campos visíveis aplicáveis devem ser obrigatórios
```

### CT-035 - Apresentar validação compreensível

**Requisitos:** RNF001, RNF002 | **Nível:** Sistema/UI | **Prioridade:** P1 | **Execução:** Manual no navegador
**Tela:** `GET /colaboradores/novo`.

**Variáveis sugeridas:** enviar nome em branco e salário base ausente; observar mensagens gerais e junto aos campos.

```gherkin
Scenario: explicar erros de campos obrigatórios
	Given que a tela `GET /colaboradores/novo` está aberta no navegador
	When enviar o formulário sem nome e salário base
	Then a validação nativa do navegador deve impedir o envio, indicar os controles obrigatórios e manter a pessoa na tela do formulário
```

### CT-040 - Imprimir demonstrativo individual

**Requisitos:** RF010, RN010 | **Nível:** Sistema/UI | **Prioridade:** P1 | **Execução:** Manual no navegador
**Tela:** `GET /folha/C239` ou `GET /folha/P239`; comando `Imprimir demonstrativo`.

**Variáveis sugeridas:** matrícula existente `C239`; adicional `R$ 0,00`; visualizar a impressão do navegador.

```gherkin
Scenario: imprimir demonstrativo individual como recibo
	Given que o demonstrativo individual exibe todos os campos requeridos e adicional R$ 0,00
	When acionar `Imprimir demonstrativo` na página individual
	Then a visualização de impressão deve conter matrícula, nome, tipo, salário base, adicionais e salário final, incluindo explicitamente o valor zerado
```

**Nota:** a aplicação oferece impressão do demonstrativo; a ERS não define rota nem formato de arquivo próprio para recibo.

## 4. Avaliação complementar

O caso abaixo avalia uma característica arquitetural por inspeção, não é um teste de backend por endpoint nem um teste de frontend.

### CT-033 - Avaliar extensibilidade do domínio

**Requisitos:** RNF004 | **Nível:** I | **Prioridade:** P1 | **Execução:** Inspeção manual

**Variáveis sugeridas:** `tipoAtual1 = "PADRAO"`; `tipoAtual2 = "COMISSIONADO"`; `tipoAtual3 = "PRODUCAO"`; `novoTipoExemplo = "BONUS_FIXO"`; `evidencia = registrar classes e mudanças necessárias`.

```gherkin
Scenario: avaliar inclusão de novo tipo de remuneração
	Given que o domínio possui os tipos padrão, comissionado e produção
	When a pessoa revisora analisa como incluir o tipo de exemplo "BONUS_FIXO"
	Then a análise deve registrar classes e fórmulas afetadas e verificar se os cálculos existentes podem permanecer isolados
```

## 5. Rastreabilidade requisito a requisito

| Requisito/grupo | Casos |
|---|---|
| RF001, cadastro com matrícula, nome, salário base e tipo | CT-001 a CT-006, CT-009 a CT-016, CT-019 a CT-020 |
| RF002, cadastro padrão | CT-001, CT-002, CT-014 |
| RF003, cadastro comissionado | CT-003 a CT-005, CT-015 a CT-018 |
| RF004, cadastro por produção | CT-006 a CT-008, CT-019 a CT-022 |
| RF005, consulta de colaboradores | CT-023, CT-024, CT-025, CT-037 |
| RF006, cálculo automático por categoria e competência | CT-001 a CT-008, CT-036 |
| RF007, folha consolidada filtrada por mês/ano | CT-026, CT-036, CT-037 |
| RF008, total da folha para o período | CT-028, CT-031, CT-036, CT-039 |
| RF009, inclusão, alteração, exclusão e consulta com histórico íntegro | CT-001 a CT-003, CT-006, CT-009, CT-023 a CT-025, CT-037 |
| RF010, relatório individual com campos completos e adicionais zerados | CT-027, CT-038 |
| RF011, quantidade, total e total por categoria no mês/ano | CT-029, CT-031, CT-039 |
| RNF001, interface amigável/intuitiva | CT-032, CT-034, CT-035, CT-040 |
| RNF002, validar entradas antes do cálculo | CT-010 a CT-022, CT-035 |
| RNF003, persistência SQL, relatórios confiáveis e integridade histórica | CT-030, CT-037 |
| RNF004, permitir novos tipos sem alterar cálculos existentes | CT-033 |
| RN001, matrícula única | CT-009 |
| RN002, nome obrigatório | CT-012 |
| RN003, salário base não negativo | CT-002, CT-014 |
| RN004, vendas não negativas | CT-004, CT-017 |
| RN005, percentual de comissão não negativo | CT-005, CT-018 |
| RN006, quantidade produzida não negativa | CT-007, CT-021 |
| RN007, valor por unidade não negativo | CT-008, CT-022 |
| RN008, salário final calculado automaticamente | CT-001 a CT-008, CT-036 |
| RN009, exclusão lógica e preservação de folhas retroativas | CT-025, CT-037 |
| RN010, valores variáveis nulos apresentados como R$ 0,00 | CT-004, CT-005, CT-007, CT-008, CT-027, CT-038, CT-040 |
| Critério: cadastro dos três tipos, validações e cálculos corretos | CT-001 a CT-022 |
| Critério: folhas individual/consolidada com filtro mês/ano | CT-026, CT-027, CT-036 a CT-038, CT-040 |
| Critério: total geral da competência | CT-028, CT-036, CT-039 |
| Critério: possibilidade de expansão de remuneração | CT-033 |

### Correspondência com os IDs da matriz da ERS

Os IDs `CT001`–`CT010` da matriz de rastreabilidade incluída na ERS são agrupadores de alto nível. A tabela liga-os aos casos detalhados deste documento; ela não substitui os requisitos associados nem indica que os testes foram executados.

| ID da ERS | Requisitos associados na matriz | Casos detalhados |
|---|---|---|
| CT001 | RF001, RF009, RN001, RN002, RN009 | CT-009, CT-012, CT-023 a CT-025, CT-037 |
| CT002 | RF002, RN003 | CT-001, CT-002, CT-014 |
| CT003 | RF003, RN004, RN005 | CT-003 a CT-005, CT-017, CT-018 |
| CT004 | RF004, RN006, RN007 | CT-006 a CT-008, CT-021, CT-022 |
| CT005 | RF005, RF009, RN009 | CT-023 a CT-025, CT-037 |
| CT006 | RF006, RF007, RF010, RN008, RN010 | CT-001 a CT-008, CT-026, CT-027, CT-036, CT-038, CT-040 |
| CT007 | RF008, RF011 | CT-028, CT-029, CT-031, CT-039 |
| CT008 | RNF002 | CT-010 a CT-022, CT-035 |
| CT009 | RNF003 | CT-030, CT-037 |
| CT010 | RNF004 | CT-033 |

## 6. Registro de execução

Execução de referência: `.\mvnw.cmd clean verify` em 23/11/2026 (105 testes, 0 falhas, 0 erros, 0 ignorados — [log](log-verify.txt)) e execuções manuais registradas no [roteiro](../09_Homologacao/Roteiro_Testes_Manuais.md). Os nomes dos testes são os métodos JUnit cujo `@DisplayName` cita o caso.

| ID do caso | Versão | Data | Executor | Ambiente | Resultado | Evidência |
|---|---|---|---|---|---|---|
| CT-001 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.formularioPadraoValido`, `ColaboradorTest.colaboradorPadraoRecebeSalarioBaseSemAdicional`, `FolhaPagamentoControllerTest.formularioNovo`, `FolhaPagamentoControllerTest.cadastraColaboradorValido` (+1) |
| CT-002 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorTest.aceitaSalariosEAdicionaisZero` |
| CT-003 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.construiComissionado`, `ColaboradorTest.colaboradorComissionadoCalculaAdicionalSobreVendas`, `FolhaPagamentoWebCasosDeTesteTest.ct003CalculaComissaoOuZero` |
| CT-004 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorTest.aceitaSalariosEAdicionaisZero`, `FolhaPagamentoWebCasosDeTesteTest.ct003CalculaComissaoOuZero` |
| CT-005 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorTest.validaLimitesDeComissao`, `FolhaPagamentoWebCasosDeTesteTest.ct003CalculaComissaoOuZero` |
| CT-006 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.construiProducao`, `ColaboradorTest.colaboradorProducaoCalculaAdicionalPelaQuantidadeProduzida`, `FolhaPagamentoWebCasosDeTesteTest.ct006CalculaProducaoOuZero` |
| CT-007 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorTest.aceitaSalariosEAdicionaisZero`, `FolhaPagamentoWebCasosDeTesteTest.ct006CalculaProducaoOuZero` |
| CT-008 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorTest.aceitaSalariosEAdicionaisZero`, `FolhaPagamentoWebCasosDeTesteTest.ct006CalculaProducaoOuZero` |
| CT-009 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoControllerTest.cadastroDuplicado`, `FolhaPagamentoServiceTest.cadastrarPropagaDuplicidade`, `FolhaPagamentoWebCasosDeTesteTest.ct009RejeitaMatriculaDuplicada`, `RepositorioColaboradoresPostgresTest.persisteCadastroERejeitaMatriculaDuplicada` (+1) |
| CT-010 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaCamposCadastrais`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct010RejeitaMatriculaComPontuacao` |
| CT-011 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaCamposCadastrais`, `ColaboradorTest.rejeitaMatriculaOuNomeEmBranco`, `ColaboradorTest.rejeitaMatriculaNulaOuVazia`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario` (+1) |
| CT-012 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaCamposCadastrais`, `ColaboradorTest.rejeitaMatriculaOuNomeEmBranco`, `ColaboradorTest.rejeitaNomeNuloOuVazio`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario` (+1) |
| CT-013 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaCamposCadastrais`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct013ValidaSalarioBaseObrigatorio` |
| CT-014 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaValoresVariaveisNegativos`, `ColaboradorTest.rejeitaSalarioBaseNegativoOuNaoFinito`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct014E018RejeitaValoresNegativos` |
| CT-015 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct015E016AceitaCamposComissionadosEmBranco` |
| CT-016 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct015E016AceitaCamposComissionadosEmBranco` |
| CT-017 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaValoresVariaveisNegativos`, `ColaboradorTest.rejeitaValoresAdicionaisNegativos`, `FolhaPagamentoControllerTest.errosDeValidacaoDoFormulario`, `FolhaPagamentoWebCasosDeTesteTest.ct014E018RejeitaValoresNegativos` |
| CT-018 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaValoresVariaveisNegativos`, `ColaboradorTest.rejeitaValoresAdicionaisNegativos`, `FolhaPagamentoWebCasosDeTesteTest.ct014E018RejeitaValoresNegativos` |
| CT-019 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoWebCasosDeTesteTest.ct019E020AceitaCamposProducaoEmBranco` |
| CT-020 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoWebCasosDeTesteTest.ct019E020AceitaCamposProducaoEmBranco` |
| CT-021 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaValoresVariaveisNegativos`, `ColaboradorTest.rejeitaValoresAdicionaisNegativos`, `FolhaPagamentoWebCasosDeTesteTest.ct014E018RejeitaValoresNegativos` |
| CT-022 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `ColaboradorFormTest.validaValoresVariaveisNegativos`, `ColaboradorTest.rejeitaValoresAdicionaisNegativos`, `FolhaPagamentoWebCasosDeTesteTest.ct014E018RejeitaValoresNegativos` |
| CT-023 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `FolhaPagamentoControllerTest.paginaInicial`, `FolhaPagamentoServiceTest.buscaEListaColaboradores`, `FolhaPagamentoWebCasosDeTesteTest.ct023ListaTresCategorias`, `RepositorioColaboradoresTest.persisteTiposDeColaboradorERejeitaDuplicidade`; [captura](../09_Homologacao/evidencias/CT-023/) |
| CT-024 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoControllerTest.editarColaborador`, `FolhaPagamentoControllerTest.atualizarColaborador`, `FolhaPagamentoWebCasosDeTesteTest.ct024AtualizaColaborador`, `RepositorioColaboradoresTest.atualizaCadastroEExcluiColaboradorAtivo` |
| CT-025 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoControllerTest.excluirColaboradorExistente`, `FolhaPagamentoControllerTest.excluirColaboradorInexistente`, `FolhaPagamentoWebCasosDeTesteTest.ct025ExcluiColaboradorAtivo`, `RepositorioColaboradoresTest.atualizaCadastroEExcluiColaboradorAtivo` |
| CT-026 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `FolhaPagamentoControllerTest.folhaConsolidada`, `FolhaPagamentoWebCasosDeTesteTest.ct026CalculaResumoPorCategoria`; [folha 11/2026](../09_Homologacao/evidencias/CT-026/) |
| CT-027 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `CompetenciaEBordasTest.demonstrativoDeFolhaNaoProcessada`, `FolhaPagamentoControllerTest.folhaIndividualExistente`, `FolhaPagamentoControllerTest.folhaIndividualInexistente`, `FolhaPagamentoWebCasosDeTesteTest.ct027ExibeFolhaIndividualComissionada`; [demonstrativo E002](../09_Homologacao/evidencias/CT-027/) |
| CT-028 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoServiceTest.gerarResumoCalculaTotalEValoresPorCategoria`, `FolhaPagamentoWebCasosDeTesteTest.ct026CalculaResumoPorCategoria` |
| CT-029 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoServiceTest.gerarResumoCalculaTotalEValoresPorCategoria`, `FolhaPagamentoWebCasosDeTesteTest.ct026CalculaResumoPorCategoria` |
| CT-030 | 2.0.0 | 23/11/2026 | Suíte automatizada, equipe (vídeos A/B/C) e revisão A3 (T130 e CT-030-D) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `RepositorioColaboradoresPostgresTest.persisteCadastroERejeitaMatriculaDuplicada`; [vídeos, logs e capturas](../09_Homologacao/evidencias/CT-030/) |
| CT-031 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoServiceTest.gerarResumoVazio`, `FolhaPagamentoWebCasosDeTesteTest.ct031ResumoVazio` |
| CT-032 | 2.0.0 | 23/11/2026 | Equipe (observação de usabilidade) | Opera; aplicação local :8081 | **Aprovado** | [vídeos CT-032-01 a 05](../09_Homologacao/evidencias/CT-032/), tabela no [roteiro](../09_Homologacao/Roteiro_Testes_Manuais.md) |
| CT-033 | 2.0.0 | 23/11/2026 | Revisão A3 (inspeção) + suíte automatizada | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `ConformidadeRequisitosTest.novoTipoDeRemuneracaoNaoAlteraCalculosExistentes`; [inspeção no roteiro](../09_Homologacao/Roteiro_Testes_Manuais.md); assinatura da revisora pendente |
| CT-034 | 2.0.0 | 23/11/2026 | Amanda Ruwer | Opera; aplicação local :8081 | **Aprovado** | [capturas passo 1–3](../09_Homologacao/evidencias/CT-034/) |
| CT-035 | 2.0.0 | 23/11/2026 | Amanda Ruwer | Opera; aplicação local :8081 | **Aprovado** | [captura](../09_Homologacao/evidencias/CT-035/) |
| CT-036 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `CompetenciaEBordasTest.consultaComCompetenciaInvalida`, `CompetenciaEBordasTest.processarComCompetenciaInvalida`, `CompetenciaEBordasTest.demonstrativoComCompetenciaInvalida`, `FolhaPagamentoControllerTest.folhaComCompetenciaInvalida` (+4); [folha 12/2026 e competência não processada](../09_Homologacao/evidencias/CT-036/) |
| CT-037 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `FolhaPagamentoWebCasosDeTesteTest.ct037PreservaSnapshotDepoisDaExclusao`, `RepositorioColaboradoresPostgresTest.congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete`, `RepositorioColaboradoresTest.preservaSnapshotDaFolhaDepoisDeAtualizarEExcluir`; [demonstrativo do excluído E004](../09_Homologacao/evidencias/CT-038/) |
| CT-038 | 2.0.0 | 23/11/2026 | Suíte automatizada + revisão A3 | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers); PostgreSQL 17 (Docker Compose) + navegador | **Aprovado** | `FolhaPagamentoWebCasosDeTesteTest.ct038ExibeAdicionaisZerados`, `FolhaPagamentoWebCasosDeTesteTest.ct038BrancoAceitaVendasEmBranco`; [demonstrativo com R$ 0,00](../09_Homologacao/evidencias/CT-038/) |
| CT-039 | 2.0.0 | 23/11/2026 | Suíte automatizada (`mvnw clean verify`) | Windows 11, JDK 23.0.1, Maven 3.9.9; H2 + MockMvc e PostgreSQL 17 (Testcontainers) | **Aprovado** | `FolhaPagamentoWebCasosDeTesteTest.ct039ResumePorCategoriaECompetencia` |
| CT-040 | 2.0.0 | 23/11/2026 | Revisão A3 | Edge 158 headless; PostgreSQL 17 (Docker Compose) | **Aprovado** | [tela, PDF e página impressa](../09_Homologacao/evidencias/CT-040/) |

**Resumo:** 40 casos executados; 40 aprovados; 0 reprovados; 0 bloqueados.

## 7. Revisão

Casos revisados contra a [ERS](../01_Requisitos/Documento_Visao_e_ERS.md) no fechamento da A3. Decisões registradas:

- O contrato HTTP de competência adotado é `mes`/`ano` nas rotas `/folha`, `/folha/processar` e `/folha/{matricula}`.
- Não há rota própria de recibo: o recibo é a impressão do demonstrativo individual pelo navegador (CT-040).
- CT-015, CT-016, CT-019 e CT-020 foram corrigidos para tratar valores variáveis em branco como zero, conforme RN010 (BUG-001).
- Os resultados acima foram associados aos IDs dos casos e aos defeitos do [Registro de Defeitos](../10_Qualidade/Registro_Defeitos.md).
