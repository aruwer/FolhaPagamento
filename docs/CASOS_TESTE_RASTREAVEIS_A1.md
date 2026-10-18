# Casos de Teste Rastreáveis - A1

**Projeto:** Sistema de Folha de Pagamento  
**Versão-alvo:** QDS_1.1.0  
**Tarefa:** [Testes][A1] Especificar no mínimo 20 casos de teste rastreáveis  
**Status:** Casos especificados; resultados pendentes de execução

## 1. Convenções

- Os IDs `CT-A1-NNN` são estáveis e devem ser usados no registro de execução e nas issues.
- Prioridades: **P0** crítica para aceitação; **P1** alta; **P2** complementar/recomendação.
- Níveis: **U** unitário; **I** integração; **S** sistema; **A** aceitação.
- **Automação:** `Sim` indica candidato a JUnit/Spring Boot Test; `Manual` indica navegador ou validação/aceitação manual. A ferramenta final depende da arquitetura implementada.
- Onde o cliente classifica o item como recomendação ou o escopo ainda não foi confirmado, o caso está marcado **condicional**. A classificação não substitui a decisão formal de escopo.
- `RF`, `RN` e `RNF` referem-se aos identificadores de [Visão de Sistema e ERS Rastreável](../VisaoDeSistema+ERSRastreavel.pdf), fonte única dos requisitos para estes casos. O antigo `T1 - Requisitos do Cliente.pdf` não é usado como referência.
- Pré-condição geral: versão/commit identificado, ambiente de teste isolado e uso de dados fictícios. Para casos de UI, aplicação iniciada e navegador registrado.
- Em cada cenário, `Scenario`, `Given`, `When` e `Then` são palavras-chave Gherkin em inglês; nomes, contexto, ação e resultado estão em português.
- Variáveis como `matricula`, `salarioBase` e `salarioFinalEsperado` são sugestões de nomes para dados e asserções. Para casos automatizados, criar novos dados por teste e usar arquivo temporário quando houver persistência.
- Valores monetários estão expressos com ponto decimal para facilitar leitura e automação; adaptar ao formato de entrada da interface quando necessário.

## 2. Backend - testes por endpoint

Os cenários desta seção exercitam o backend por requisições HTTP aos endpoints MVC reais, usando formulário `application/x-www-form-urlencoded` e verificando redirect, página HTML, dados exibidos e ausência de alteração indevida. O método e o caminho constam no campo **Endpoint** de cada caso e devem ser usados na execução; não substituir a requisição por chamada direta ao service ou ao domínio. Para automação, usar MockMvc com o contexto Spring; alternativamente, executar contra a aplicação local isolada. Os endpoints retornam páginas Thymeleaf, não JSON.

Em cada caso está indicado o método e caminho HTTP usado. Nos cadastros válidos, o POST deve redirecionar para `/`; o teste pode então consultar o endpoint GET indicado para conferir o resultado. Nos casos inválidos, espera-se retorno à tela do formulário com erros e sem persistência; resposta 5xx é falha do endpoint.

### CT-A1-001 - Calcular salário padrão

**Requisitos:** RF001, RF002, RF006 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/A100`.

**Variáveis sugeridas:** `matricula = "A100"`; `nome = "Ana Lima"`; `salarioBase = 2500.00`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: calcular salário de colaborador padrão
	Given que existe um colaborador padrão com matrícula "A100", nome "Ana Lima" e salário base 2500.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/A100` deve exibir adicional 0.00 e salário final 2500.00
```

### CT-A1-002 - Aceitar salário base zero

**Requisitos:** RF002, RN003, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/A102`.

**Variáveis sugeridas:** `matricula = "A102"`; `salarioBase = 0.00`; `salarioFinalEsperado = 0.00`.

```gherkin
Scenario: calcular salário padrão com base zero
	Given que existe um colaborador padrão com salário base 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/A102` deve exibir salário final 0.00 sem adicional
```

### CT-A1-003 - Calcular comissão positiva

**Requisitos:** RF003, RF006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C100`.

**Variáveis sugeridas:** `matricula = "C100"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `comissaoEsperada = 500.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: calcular remuneração de colaborador comissionado
	Given que existe um colaborador com base 2000.00, vendas de 10000.00 e comissão de 5.00 por cento
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C100` deve exibir comissão 500.00 e salário final 2500.00
```

### CT-A1-004 - Aceitar vendas zero

**Requisitos:** RF003, RN004, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C104`.

**Variáveis sugeridas:** `matricula = "C104"`; `salarioBase = 2000.00`; `valorVendas = 0.00`; `percentualComissao = 5.00`; `comissaoEsperada = 0.00`; `salarioFinalEsperado = 2000.00`.

```gherkin
Scenario: calcular comissão com vendas zero
	Given que existe um colaborador comissionado com base 2000.00, vendas 0.00 e percentual 5.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C104` deve exibir comissão 0.00 e salário final 2000.00
```

### CT-A1-005 - Aceitar percentual de comissão zero

**Requisitos:** RF003, RN005, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/C105`.

**Variáveis sugeridas:** `matricula = "C105"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 0.00`; `comissaoEsperada = 0.00`; `salarioFinalEsperado = 2000.00`.

```gherkin
Scenario: calcular comissão com percentual zero
	Given que existe um colaborador comissionado com base 2000.00, vendas 10000.00 e percentual 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/C105` deve exibir comissão 0.00 e salário final 2000.00
```

### CT-A1-006 - Calcular remuneração por produção

**Requisitos:** RF004, RF006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P100`.

**Variáveis sugeridas:** `matricula = "P100"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 2.50`; `adicionalEsperado = 300.00`; `salarioFinalEsperado = 2100.00`.

```gherkin
Scenario: calcular remuneração por produção
	Given que existe um colaborador com base 1800.00, produção de 120 unidades e valor unitário 2.50
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P100` deve exibir adicional 300.00 e salário final 2100.00
```

### CT-A1-007 - Aceitar quantidade produzida zero

**Requisitos:** RF004, RN006, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P107`.

**Variáveis sugeridas:** `matricula = "P107"`; `salarioBase = 1800.00`; `quantidadeProduzida = 0.00`; `valorPorUnidade = 2.50`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 1800.00`.

```gherkin
Scenario: calcular produção com quantidade zero
	Given que existe um colaborador de produção com base 1800.00, quantidade 0 e valor unitário 2.50
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P107` deve exibir adicional 0.00 e salário final 1800.00
```

### CT-A1-008 - Aceitar valor por unidade zero

**Requisitos:** RF004, RN007, RN008 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores`; conferir em `GET /folha/P108`.

**Variáveis sugeridas:** `matricula = "P108"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 0.00`; `adicionalEsperado = 0.00`; `salarioFinalEsperado = 1800.00`.

```gherkin
Scenario: calcular produção com valor unitário zero
	Given que existe um colaborador de produção com base 1800.00, quantidade 120 e valor unitário 0.00
	When enviar os dados por `POST /colaboradores`
	Then consultar `GET /folha/P108` deve exibir adicional 0.00 e salário final 1800.00
```

### CT-A1-009 - Rejeitar matrícula duplicada

**Requisitos:** RF001, RF009, RN001 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` duas vezes com a mesma matrícula; conferir em `GET /`.

**Variáveis sugeridas:** `matricula = "A101"`; `nomeOriginal = "Bruno Alves"`; `nomeDuplicado = "Bruna Alves"`; `salarioBase = 2100.00`; `quantidadeInicialEsperada = 1`.

```gherkin
Scenario: rejeitar cadastro com matrícula já utilizada
	Given que o repositório contém o colaborador "A101" com nome "Bruno Alves"
	When enviar os dados duplicados por `POST /colaboradores`
	Then o cadastro duplicado deve ser rejeitado e o registro original deve permanecer inalterado
```

### CT-A1-010 - Filtrar caractere não permitido na matrícula (complementar)

**Requisitos:** RF001, RNF001 | **Nível:** Integração HTTP/UI | **Prioridade:** P2 | **Execução:** navegador e HTTP local  
**Endpoint:** abrir `GET /colaboradores/novo`; verificar o campo após digitação. RN001 exige unicidade, não restringe formato alfanumérico.

**Variáveis sugeridas:** `matriculaInvalida = "A-102"`; `nome = "Caio Reis"`; `tipo = "PADRAO"`; `salarioBase = 2200.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: remover pontuação da matrícula na interface
	Given que o formulário de cadastro está aberto
	When digitar "A-102" no campo matrícula
	Then a interface deve filtrar a pontuação conforme o comportamento complementar esperado, sem atribuir essa regra à RN001
```

### CT-A1-011 - Rejeitar matrícula vazia

**Requisitos:** RF001, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `matricula=`.

**Variáveis sugeridas:** `matricula = ""`; `nome = "Dora Silva"`; `tipo = "PADRAO"`; `salarioBase = 2300.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar matrícula em branco
	Given que o formulário de cadastro contém matrícula vazia e os demais campos válidos
	When a pessoa envia o formulário
	Then o campo obrigatório deve ser indicado e o cadastro não deve ser concluído
```

### CT-A1-012 - Rejeitar nome vazio

**Requisitos:** RF001, RN002, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `nome=` ou `nome=   `.

**Variáveis sugeridas:** `matricula = "A112"`; `nome = "   "`; `tipo = "PADRAO"`; `salarioBase = 2300.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro sem nome
	Given que o formulário contém nome composto apenas por espaços e os demais campos válidos
	When a pessoa envia o formulário
	Then o nome obrigatório deve ser indicado e o colaborador não deve ser salvo
```

### CT-A1-013 - Rejeitar salário base ausente

**Requisitos:** RF001, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` sem o parâmetro `salarioBase`.

**Variáveis sugeridas:** `matricula = "A113"`; `nome = "Eva Nunes"`; `tipo = "PADRAO"`; `salarioBase = null`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro sem salário base
	Given que o formulário contém matrícula e nome válidos, mas não contém salário base
	When a pessoa envia o formulário
	Then o campo salário base deve ser indicado e nenhum cadastro parcial deve ser persistido
```

### CT-A1-014 - Rejeitar salário base negativo

**Requisitos:** RF002, RN003, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `salarioBase=-0.01`.

**Variáveis sugeridas:** `matricula = "A114"`; `nome = "Fabio Melo"`; `salarioBase = -0.01`; `tipo = "PADRAO"`.

```gherkin
Scenario: rejeitar salário base negativo
	Given que os dados do colaborador padrão incluem salário base -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-A1-015 - Rejeitar vendas ausentes no cadastro comissionado

**Requisitos:** RF003, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `tipo=COMISSIONADO` e sem `valorVendas`.

**Variáveis sugeridas:** `matricula = "C115"`; `nome = "Gabi Luz"`; `salarioBase = 2000.00`; `valorVendas = null`; `percentualComissao = 5.00`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro comissionado sem valor de vendas
	Given que o formulário comissionado contém base 2000.00 e percentual 5.00, mas vendas não informadas
	When a pessoa envia o formulário
	Then a ausência de vendas deve ser indicada e os dados não devem ser persistidos
```

### CT-A1-016 - Rejeitar percentual de comissão ausente

**Requisitos:** RF003, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `tipo=COMISSIONADO` e sem `percentualComissao`.

**Variáveis sugeridas:** `matricula = "C116"`; `nome = "Hugo Paz"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = null`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro comissionado sem percentual
	Given que o formulário comissionado contém base 2000.00 e vendas 10000.00, mas percentual não informado
	When a pessoa envia o formulário
	Then a ausência do percentual deve ser indicada e os dados não devem ser persistidos
```

### CT-A1-017 - Rejeitar valor de vendas negativo

**Requisitos:** RF003, RN004, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `valorVendas=-0.01`.

**Variáveis sugeridas:** `salarioBase = 2000.00`; `valorVendas = -0.01`; `percentualComissao = 5.00`.

```gherkin
Scenario: rejeitar valor de vendas negativo
	Given que os dados do colaborador comissionado incluem vendas -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-A1-018 - Rejeitar percentual de comissão negativo

**Requisitos:** RF003, RN005, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `percentualComissao=-0.01`.

**Variáveis sugeridas:** `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = -0.01`.

```gherkin
Scenario: rejeitar percentual de comissão negativo
	Given que os dados do colaborador comissionado incluem percentual -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-A1-019 - Rejeitar quantidade produzida ausente

**Requisitos:** RF004, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `tipo=PRODUCAO` e sem `quantidadeProduzida`.

**Variáveis sugeridas:** `matricula = "P119"`; `nome = "Iara Costa"`; `salarioBase = 1800.00`; `quantidadeProduzida = null`; `valorPorUnidade = 2.50`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro de produção sem quantidade
	Given que o formulário de produção contém base 1800.00 e valor unitário 2.50, mas quantidade não informada
	When a pessoa envia o formulário
	Then a quantidade obrigatória deve ser indicada e os dados não devem ser persistidos
```

### CT-A1-020 - Rejeitar valor por unidade ausente

**Requisitos:** RF004, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `tipo=PRODUCAO` e sem `valorPorUnidade`.

**Variáveis sugeridas:** `matricula = "P120"`; `nome = "Joao Luz"`; `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = null`; `quantidadePersistidaEsperada = 0`.

```gherkin
Scenario: rejeitar cadastro de produção sem valor unitário
	Given que o formulário de produção contém base 1800.00 e quantidade 120, mas valor unitário não informado
	When a pessoa envia o formulário
	Then o valor unitário obrigatório deve ser indicado e os dados não devem ser persistidos
```

### CT-A1-021 - Rejeitar quantidade produzida negativa

**Requisitos:** RF004, RN006, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `quantidadeProduzida=-1`.

**Variáveis sugeridas:** `salarioBase = 1800.00`; `quantidadeProduzida = -1.00`; `valorPorUnidade = 2.50`.

```gherkin
Scenario: rejeitar quantidade produzida negativa
	Given que os dados do colaborador de produção incluem quantidade -1
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-A1-022 - Rejeitar valor por unidade negativo

**Requisitos:** RF004, RN007, RNF002 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores` com `valorPorUnidade=-0.01`.

**Variáveis sugeridas:** `salarioBase = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = -0.01`.

```gherkin
Scenario: rejeitar valor por unidade negativo
	Given que os dados do colaborador de produção incluem valor unitário -0.01
	When enviar o formulário inválido por `POST /colaboradores`
	Then a resposta não deve ser 5xx, deve indicar a validação do campo e não deve persistir o colaborador
```

### CT-A1-023 - Listar colaboradores das três categorias

**Requisitos:** RF005, RF009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar com `POST /colaboradores`; consultar a lista em `GET /`.

**Variáveis sugeridas:** `matriculaPadrao = "L123"`; `nomePadrao = "Lia Dias"`; `salarioBasePadrao = 1500.00`; `matriculaComissionado = "L124"`; `nomeComissionado = "Luca Reis"`; `salarioBaseComissionado = 1800.00`; `valorVendas = 4000.00`; `percentualComissao = 5.00`; `matriculaProducao = "L125"`; `nomeProducao = "Mia Sol"`; `salarioBaseProducao = 1600.00`; `quantidadeProduzida = 20.00`; `valorPorUnidade = 3.00`; `quantidadeEsperada = 3`.

```gherkin
Scenario: listar colaboradores de todas as categorias
	Given que o repositório contém um colaborador padrão, um comissionado e um de produção com matrículas distintas
	When consultar a lista por `GET /`
	Then os três colaboradores devem aparecer uma vez cada, com identificação, categoria e dados remuneratórios correspondentes
```

### CT-A1-024 - Atualizar colaborador

**Requisitos:** RF009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A201`; conferir em `GET /folha/A201`.

**Variáveis sugeridas:** `matricula = "A201"`; `nomeOriginal = "Nina Reis"`; `nomeAtualizado = "Nina Reis Silva"`; `salarioBaseOriginal = 2000.00`; `salarioBaseAtualizado = 2300.00`.

```gherkin
Scenario: atualizar dados de colaborador existente
	Given que o repositório contém "A201" com nome "Nina Reis" e base 2000.00
	When enviar os dados atualizados por `POST /colaboradores/A201`
	Then consultar `GET /folha/A201` deve exibir os valores atualizados e apenas um registro com essa matrícula em `GET /`
```

### CT-A1-025 - Excluir colaborador da consulta ativa

**Requisitos:** RF009, RN009 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A202/excluir`; confirmar ausência em `GET /`.

**Variáveis sugeridas:** `matricula = "A202"`; `nome = "Otto Lima"`; `salarioBase = 2400.00`; `quantidadeEsperadaAposExclusao = 0`.

```gherkin
Scenario: excluir colaborador existente
	Given que o repositório contém o colaborador "A202"
	When enviar a exclusão por `POST /colaboradores/A202/excluir`
	Then consultar `GET /` não deve exibir o colaborador "A202"
```

### CT-A1-026 - Conferir folha consolidada

**Requisitos:** RF007, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar dados por `POST /colaboradores`; consultar relatório em `GET /folha`.

**Variáveis sugeridas:** `matriculaPadrao = "R126"`; `nomePadrao = "Rita Alves"`; `salarioBasePadrao = 2500.00`; `salarioFinalPadraoEsperado = 2500.00`; `matriculaComissionado = "R127"`; `nomeComissionado = "Rui Melo"`; `salarioBaseComissionado = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `salarioFinalComissionadoEsperado = 2500.00`; `matriculaProducao = "R128"`; `nomeProducao = "Sonia Luz"`; `salarioBaseProducao = 1800.00`; `quantidadeProduzida = 120.00`; `valorPorUnidade = 2.50`; `salarioFinalProducaoEsperado = 2100.00`.

```gherkin
Scenario: exibir colaboradores na folha consolidada
	Given que existem colaboradores padrão, comissionado e de produção com os valores esperados preparados
	When consultar a folha consolidada por `GET /folha`
	Then cada colaborador deve aparecer com matrícula, nome, tipo de colaborador e salário final corretos para a competência consultada
```

### CT-A1-027 - Conferir folha individual

**Requisitos:** RF010, RN010, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** cadastrar por `POST /colaboradores`; consultar em `GET /folha/C100`.

**Variáveis sugeridas:** `matricula = "C100"`; `salarioBase = 2000.00`; `valorVendas = 10000.00`; `percentualComissao = 5.00`; `adicionalEsperado = 500.00`; `salarioFinalEsperado = 2500.00`.

```gherkin
Scenario: exibir folha individual de colaborador comissionado
	Given que existe o colaborador "C100" com base 2000.00, vendas 10000.00 e percentual 5.00
	When consultar `GET /folha/C100`
	Then identificação, base, comissão de 500.00 e salário final de 2500.00 devem corresponder aos dados e à fórmula aprovada
```

### CT-A1-028 - Somar salários finais

**Requisitos:** RF008 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar colaboradores por `POST /colaboradores`; conferir total em `GET /folha`.

**Variáveis sugeridas:** `salariosFinais = [2500.00, 2100.00, 1800.00]`; `totalEsperado = 6400.00`.

```gherkin
Scenario: calcular total geral da folha
	Given que a folha contém salários finais de 2500.00, 2100.00 e 1800.00
	When consultar `GET /folha`
	Then o total da folha deve ser 6400.00
```

### CT-A1-029 - Conferir resumo por categoria

**Requisitos:** RF011 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar colaboradores por `POST /colaboradores`; conferir resumo em `GET /`.

**Variáveis sugeridas:** `salariosPadrao = [1000.00, 1500.00]`; `salarioComissionado = 2000.00`; `salarioProducao = 2200.00`; `quantidadeEsperada = 4`; `totalPadraoEsperado = 2500.00`; `totalComissionadoEsperado = 2000.00`; `totalProducaoEsperado = 2200.00`; `totalGeralEsperado = 6700.00`.

```gherkin
Scenario: calcular totais e quantidade por categoria
	Given que o repositório contém dois colaboradores padrão de 1000.00 e 1500.00, um comissionado de 2000.00 e um de produção de 2200.00
	When consultar o resumo por `GET /`
	Then a quantidade deve ser 4, o total geral 6700.00 e os totais por categoria 2500.00, 2000.00 e 2200.00 respectivamente
```

### CT-A1-030 - Recuperar dados persistidos

**Requisitos:** RNF003 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** HTTP local com banco SQL isolado  
**Endpoint:** cadastrar com `POST /colaboradores`; após reinício, consultar `GET /folha/T130?mes=3&ano=2026`.

**Variáveis sugeridas:** `bancoTeste = banco SQL exclusivo e descartável`; `matricula = "T130"`; `nome = "Sara Melo"`; `salarioBase = 1900.00`; `quantidadeProduzida = 80.00`; `valorPorUnidade = 3.00`; `salarioFinalEsperado = 2140.00`.

```gherkin
Scenario: persistir colaborador em SQL após reiniciar a aplicação
	Given que o banco SQL de teste está vazio e a aplicação usa esse banco
	When cadastrar "T130" por `POST /colaboradores`, processar a competência 03/2026, reiniciar a aplicação e consultar `GET /folha/T130?mes=3&ano=2026`
	Then a conexão configurada deve ser SQL e os dados/folha devem ser recuperados do banco após o reinício, com salário final 2140.00 e uma única matrícula
```

**Observação de execução:** a configuração atual do projeto usa TSV; até a implementação de persistência SQL, este caso está bloqueado e RNF003 não pode ser declarado atendido.

### CT-A1-031 - Gerar resumo sem colaboradores

**Requisitos:** RF008, RF011 | **Nível:** Integração HTTP | **Prioridade:** P1 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `GET /` com repositório vazio.

**Variáveis sugeridas:** `colaboradores = []`; `quantidadeEsperada = 0`; `totalEsperado = 0.00`; `categoriasEsperadas = {}`.

```gherkin
Scenario: gerar resumo de folha vazia
	Given que não existem colaboradores cadastrados
	When consultar o resumo por `GET /`
	Then a quantidade deve ser 0, o total deve ser 0.00 e não deve haver totais residuais por categoria
```

### CT-A1-036 - Filtrar folha por mês e ano

**Requisitos:** RF006, RF007, RF008, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar competências via `POST /colaboradores`; consultar `GET /folha?mes=3&ano=2026`.

**Variáveis sugeridas:** `competenciaAlvo = 03/2026`; `competenciasDistratoras = [04/2026, 03/2025]`; `salarioFinalAlvoEsperado = 2500.00`; `totalAlvoEsperado = 2500.00`.

```gherkin
Scenario: gerar folha somente para o mês e ano solicitados
	Given que existem lançamentos de folha em 03/2026, 04/2026 e 03/2025
	When consultar `GET /folha?mes=3&ano=2026`
	Then a folha deve incluir somente os lançamentos de 03/2026, exibir matrícula, nome, tipo e salário final de cada colaborador dessa competência e apresentar o total 2500.00 sem dados dos outros períodos
```

**Nota de contrato:** a ERS exige filtro por mês/ano, mas não define nomes de parâmetros nem URL. `mes` e `ano` são a proposta usada neste caso e precisam coincidir com a implementação antes da execução.

### CT-A1-037 - Preservar histórico após exclusão lógica

**Requisitos:** RF005, RF007, RF009, RN009 | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** `POST /colaboradores/A237/excluir`; verificar colaborador ativo por `GET /` e folha histórica por `GET /folha?mes=2&ano=2026`.

**Variáveis sugeridas:** `matricula = "A237"`; `competenciaAnterior = 02/2026`; `salarioFinalHistoricoEsperado = 2500.00`; `visivelNaListaAtiva = false`.

```gherkin
Scenario: excluir cadastro sem apagar a folha já processada
	Given que "A237" consta no cadastro ativo e na folha processada de 02/2026
	When excluir "A237" por `POST /colaboradores/A237/excluir`
	Then `GET /` não deve listar o cadastro ativo, mas `GET /folha?mes=2&ano=2026` deve preservar a matrícula, os dados históricos e o salário final de 2500.00
```

**Nota de contrato:** nomes dos parâmetros de competência são proposta, pois a ERS não especifica a URL do filtro.

### CT-A1-038 - Exibir adicionais zerados em relatório e recibo

**Requisitos:** RF010, RN010, critério de aceitação | **Nível:** Sistema/Integração HTTP | **Prioridade:** P0 | **Execução:** navegador e HTTP local  
**Endpoint:** consultar `GET /folha/A238?mes=3&ano=2026`; gerar/visualizar o recibo correspondente pela ação de impressão da interface.

**Variáveis sugeridas:** colaboradores comissionados `A238` (vendas nulas/em branco) e `A239` (vendas zero); colaboradores de produção `P238` (quantidade nula/em branco) e `P239` (quantidade zero); `adicionalEsperado = R$ 0,00` em todos os relatórios e recibos.

```gherkin
Scenario: apresentar remuneração variável nula como zero
	Given que os dados variáveis dos colaboradores "A238" e "P238" estão nulos/em branco e os de "A239" e "P239" são zero na competência 03/2026
	When abrir os relatórios individuais e gerar/visualizar os recibos
	Then cada relatório e recibo deve exibir matrícula, nome, tipo, salário base, adicional e salário final, com adicional explicitamente formatado como R$ 0,00 nos quatro casos
```

**Nota de contrato:** a ERS não define rota própria para recibo; validar a ação/tela de emissão disponível antes da execução.

### CT-A1-039 - Resumir folha por categoria e competência

**Requisitos:** RF008, RF011, critério de aceitação | **Nível:** Integração HTTP | **Prioridade:** P0 | **Execução:** MockMvc ou HTTP local  
**Endpoint:** preparar lançamentos via `POST /colaboradores`; consultar `GET /?mes=3&ano=2026`.

**Variáveis sugeridas:** `quantidadeEsperada = 3`; `totalPadraoEsperado = 2500.00`; `totalComissionadoEsperado = 2500.00`; `totalProducaoEsperado = 2100.00`; `totalGeralEsperado = 7100.00`; `competenciasDistratoras = [04/2026, 03/2025]`.

```gherkin
Scenario: exibir totais e quantidade apenas da competência selecionada
	Given que existem colaboradores das três categorias em 03/2026 e lançamentos de outras competências
	When consultar o resumo por `GET /?mes=3&ano=2026`
	Then exibir quantidade 3, totais por categoria de 2500.00, 2500.00 e 2100.00 e total geral 7100.00, excluindo outros meses e anos
```

**Nota de contrato:** a ERS exige filtro mensal/anual, mas não define nomes de parâmetros nem URL. `mes` e `ano` são a proposta usada neste caso.

## 3. Frontend - testes pelo navegador

Os cenários desta seção verificam conteúdo, interação e comportamento observável da interface em navegador. Eles não substituem os testes backend por endpoint da seção anterior. Registrar navegador, versão, viewport e evidências; usar dados fictícios.

### CT-A1-032 - Avaliar fluxo de usabilidade

**Requisitos:** RNF001 | **Nível:** Sistema/Aceitação | **Prioridade:** P1 | **Execução:** Manual no navegador

**Variáveis sugeridas:** `colaboradorPadrao = {matricula: "U132", nome: "Tina Reis", salarioBase: 2500.00}`; `colaboradorComissionado = {matricula: "U133", nome: "Ugo Dias", salarioBase: 2000.00, valorVendas: 10000.00, percentualComissao: 5.00}`; `colaboradorProducao = {matricula: "U134", nome: "Vera Luz", salarioBase: 1800.00, quantidadeProduzida: 120.00, valorPorUnidade: 2.50}`; `formularioInvalido = colaboradorPadrao com nome vazio`; `tarefas = [cadastrar padrão, cadastrar comissionado, cadastrar produção, corrigir formulário inválido, abrir relatório]`; `navegador = registrar nome e versão`; `observacoes = registrar por tarefa`.

```gherkin
Scenario: executar roteiro de tarefas de interface
	Given que a pessoa participante recebe o roteiro e dados fictícios sem instruções que induzam respostas
	When ela cadastra cada categoria, corrige um formulário inválido e consulta os relatórios
	Then a conclusão de cada tarefa, as dificuldades observadas e as evidências devem ser registradas para avaliação
```

### CT-A1-034 - Exibir campos conforme categoria selecionada

**Requisitos:** RF003, RF004, RNF001 | **Nível:** Sistema/UI | **Prioridade:** P1 | **Execução:** Manual no navegador
**Tela:** `GET /colaboradores/novo`.

**Variáveis sugeridas:** `categorias = [PADRAO, COMISSIONADO, PRODUCAO]`; `camposComissionado = [valorVendas, percentualComissao]`; `camposProducao = [quantidadeProduzida, valorPorUnidade]`.

```gherkin
Scenario: atualizar campos variáveis ao selecionar categoria
	Given que a tela `GET /colaboradores/novo` está aberta no navegador
	When selecionar PADRAO, COMISSIONADO e PRODUCAO no campo Tipo de remuneração
	Then devem aparecer apenas os campos e a orientação correspondentes à categoria selecionada, e somente os campos visíveis aplicáveis devem ser obrigatórios
```

### CT-A1-035 - Filtrar caracteres da matrícula na interface

**Requisitos:** RNF001 | **Nível:** Sistema/UI | **Prioridade:** P1 | **Execução:** Manual no navegador
**Tela:** `GET /colaboradores/novo`.

**Variáveis sugeridas:** `entrada = "A-10 2!"`; `valorExibidoEsperado = "A102"`.

```gherkin
Scenario: remover caracteres não alfanuméricos da matrícula durante a digitação
	Given que a tela `GET /colaboradores/novo` está aberta no navegador
	When digitar "A-10 2!" no campo Matrícula
	Then o campo deve exibir "A102" sem pontuação ou espaços
```

## 4. Avaliação complementar

O caso abaixo avalia uma característica arquitetural por inspeção, não é um teste de backend por endpoint nem um teste de frontend.

### CT-A1-033 - Avaliar extensibilidade do domínio

**Requisitos:** RNF004 | **Nível:** I | **Prioridade:** P2 | **Execução:** Inspeção manual

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
| RF001, cadastro com matrícula, nome, salário base e tipo | CT-A1-001 a CT-A1-006, CT-A1-009, CT-A1-011 a CT-A1-016, CT-A1-019 a CT-A1-020 |
| RF002, cadastro padrão | CT-A1-001, CT-A1-002, CT-A1-014 |
| RF003, cadastro comissionado | CT-A1-003 a CT-A1-005, CT-A1-015 a CT-A1-018 |
| RF004, cadastro por produção | CT-A1-006 a CT-A1-008, CT-A1-019 a CT-A1-022 |
| RF005, consulta de colaboradores | CT-A1-023, CT-A1-024, CT-A1-025, CT-A1-037 |
| RF006, cálculo automático por categoria e competência | CT-A1-001 a CT-A1-008, CT-A1-036 |
| RF007, folha consolidada filtrada por mês/ano | CT-A1-026, CT-A1-036, CT-A1-037 |
| RF008, total da folha para o período | CT-A1-028, CT-A1-031, CT-A1-036, CT-A1-039 |
| RF009, inclusão, alteração, exclusão e consulta com histórico íntegro | CT-A1-001 a CT-A1-003, CT-A1-006, CT-A1-009, CT-A1-023 a CT-A1-025, CT-A1-037 |
| RF010, relatório individual com campos completos e adicionais zerados | CT-A1-027, CT-A1-038 |
| RF011, quantidade, total e total por categoria no mês/ano | CT-A1-029, CT-A1-031, CT-A1-039 |
| RNF001, interface amigável/intuitiva | CT-A1-032, CT-A1-034, CT-A1-035 |
| RNF002, validar entradas antes do cálculo | CT-A1-011 a CT-A1-022 |
| RNF003, persistência SQL, relatórios confiáveis e integridade histórica | CT-A1-030, CT-A1-037 |
| RNF004, permitir novos tipos sem alterar cálculos existentes | CT-A1-033 |
| RN001, matrícula única | CT-A1-009 |
| RN002, nome obrigatório | CT-A1-012 |
| RN003, salário base não negativo | CT-A1-002, CT-A1-014 |
| RN004, vendas não negativas | CT-A1-004, CT-A1-017 |
| RN005, percentual de comissão não negativo | CT-A1-005, CT-A1-018 |
| RN006, quantidade produzida não negativa | CT-A1-007, CT-A1-021 |
| RN007, valor por unidade não negativo | CT-A1-008, CT-A1-022 |
| RN008, salário final calculado automaticamente | CT-A1-001 a CT-A1-008, CT-A1-036 |
| RN009, exclusão lógica e preservação de folhas retroativas | CT-A1-025, CT-A1-037 |
| RN010, valores variáveis nulos apresentados como R$ 0,00 | CT-A1-004, CT-A1-005, CT-A1-007, CT-A1-008, CT-A1-038 |
| Critério: cadastro dos três tipos, validações e cálculos corretos | CT-A1-001 a CT-A1-022 |
| Critério: folhas individual/consolidada com filtro mês/ano | CT-A1-026, CT-A1-027, CT-A1-036 a CT-A1-038 |
| Critério: total geral da competência | CT-A1-028, CT-A1-036, CT-A1-039 |
| Critério: possibilidade de expansão de remuneração | CT-A1-033 |

## 6. Registro de execução

Preencher uma linha por execução. Não antecipar resultado nem evidência.

| ID do caso | Versão/commit | Data | Executor | Ambiente | Resultado (aprovado/reprovado/bloqueado) | Evidência/issue |
|---|---|---|---|---|---|---|
| A preencher | A preencher | A preencher | A preencher | A preencher | A preencher | A preencher |

## 7. Revisão

Antes da execução oficial, revisar os casos exclusivamente contra [Visão de Sistema e ERS Rastreável](../VisaoDeSistema+ERSRastreavel.pdf). Confirmar com a equipe o contrato dos filtros HTTP mês/ano e a rota/ação de recibo, que a ERS não especifica. Manter RNF003 como bloqueado enquanto o projeto não usar SQL; revisar dados e resultados esperados com uma pessoa diferente da autora e associar evidências/defeitos aos IDs.