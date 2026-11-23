# Relatório de Execução dos Testes Unitários

## Execução mais recente — 23/11/2026

**Branch:** `feat/QDS_2.0.0/db-postgresql`
**Comando:** `.\mvnw.cmd clean verify`
**Resultado:** BUILD SUCCESS; 68 testes descobertos, 0 falhas, 0 erros e 3 ignorados. Os ignorados são os testes de integração PostgreSQL com Testcontainers, que não puderam iniciar sem Docker.
**JaCoCo:** 93% de cobertura de instruções e 74% de cobertura de branches.
**Runtime:** Java 23.0.1; o projeto tem alvo Java 17, cuja execução ainda deve ser validada.
**Verificações incluídas:** persistência e regras com H2; inicialização do contexto Spring; renderização via MockMvc das telas inicial, cadastro e folha por competência.

A execução valida o build e os fluxos cobertos por H2, mas não confirma a aplicação das migrations nem o comportamento contra um servidor PostgreSQL real. A validação PostgreSQL permanece pendente.

---

# Registro Histórico de Execução dos Testes Unitários

> O detalhamento abaixo registra uma execução anterior (15/11/2026), antes da migração para JPA/PostgreSQL. Seus resultados não representam o estado atual do projeto; foram preservados como histórico.

**Projeto:** Folha de Pagamento  
**Branch:** `feat/QDS_2.0.0/testes-unitarios-junit`
**Data da execução:** 15/11/2026  
**Comando executado por classe:** `.\mvnw.cmd -Dtest=<ClasseDeTeste> test`  
**Fonte dos resultados:** relatórios XML do Maven Surefire em `target/surefire-reports/`.

## Legenda

- **POSITIVO** — teste executado e todas as asserções passaram.
- **NEGATIVO (FALHA)** — a asserção de requisito falhou; indica divergência observada no comportamento atual.
- **NEGATIVO (ERRO)** — a execução terminou com exceção inesperada antes de validar a expectativa.
- **SEM RESULTADO** — teste ignorado, não executado ou sem registro de resultado.

Um teste JUnit normalmente retorna `void`; o resultado verificável é produzido pelo runner a partir das asserções e exceções. O Surefire registrou o resultado de cada método abaixo. Os resultados negativos são mantidos intencionalmente para evidenciar defeitos e não representam testes removidos ou ignorados.

## Resumo por classe

| Classe de teste | Execuções | Positivas | Negativas | Sem resultado | Resultado da classe |
|---|---:|---:|---:|---:|---|
| `ColaboradorTest` | 16 | 16 | 0 | 0 | POSITIVO |
| `ConformidadeRequisitosTest` | 3 | 3 | 0 | 0 | POSITIVO |
| `ColaboradorFormTest` (inclui classe aninhada) | 11 | 11 | 0 | 0 | POSITIVO |
| `FolhaPagamentoServiceTest` | 7 | 7 | 0 | 0 | POSITIVO |
| `FormatadorMoedaTest` | 4 | 3 | 1 | 0 | NEGATIVO |
| `RepositorioColaboradoresTest` | 11 | 10 | 1 | 0 | NEGATIVO |
| `FolhaPagamentoControllerTest` | 16 | 15 | 1 | 0 | NEGATIVO |
| `FolhaPagamentoConfigTest` | 2 | 2 | 0 | 0 | POSITIVO |
| **Total** | **70** | **67** | **3** | **0** | **NEGATIVO: defeitos identificados** |

O caso `ColaboradorFormTest.ValidacaoBeanValidation.formularioPadraoValido` é executado pela classe aninhada JUnit e também foi contabilizado no total de `ColaboradorFormTest`.

## Resultados por teste

### `ColaboradorTest` — 16 execuções: 16 positivas

| Método/caso executado | Resultado |
|---|---|
| `aceitaSalariosEAdicionaisZero` | POSITIVO |
| `rejeitaSalarioBaseNegativoOuNaoFinito(double)[1]` | POSITIVO |
| `rejeitaSalarioBaseNegativoOuNaoFinito(double)[2]` | POSITIVO |
| `rejeitaSalarioBaseNegativoOuNaoFinito(double)[3]` | POSITIVO |
| `calculaValoresDecimaisEAltos` | POSITIVO |
| `colaboradorComissionadoCalculaAdicionalSobreVendas` | POSITIVO |
| `rejeitaMatriculaOuNomeEmBranco` | POSITIVO |
| `validaLimitesDeComissao` | POSITIVO |
| `rejeitaValoresAdicionaisNegativos` | POSITIVO |
| `rejeitaNomeNuloOuVazio(String)[1]` | POSITIVO |
| `rejeitaNomeNuloOuVazio(String)[2]` | POSITIVO |
| `rejeitaMatriculaNulaOuVazia(String)[1]` | POSITIVO |
| `rejeitaMatriculaNulaOuVazia(String)[2]` | POSITIVO |
| `colaboradorProducaoCalculaAdicionalPelaQuantidadeProduzida` | POSITIVO |
| `usaSemanticaDeIdentidadeHerdadaDeObject` | POSITIVO |
| `colaboradorPadraoRecebeSalarioBaseSemAdicional` | POSITIVO |

### `ConformidadeRequisitosTest` — 3 execuções: 3 positivas

| Método/caso executado | Resultado |
|---|---|
| `deveBloquearDadosVaziosNegativosOuAcimaDoLimite` | POSITIVO |
| `deveConstruirEConverterColaboradorFormCorretamente` | POSITIVO |
| `deveValidarFormulasDeCalculoRN001aRN003` | POSITIVO |

### `ColaboradorFormTest` — 11 execuções: 11 positivas

| Método/caso executado | Resultado |
|---|---|
| `valoresPadraoEGettersSetters` | POSITIVO |
| `validaComissaoAcimaDoLimite` | POSITIVO |
| `validaCamposCadastrais` | POSITIVO |
| `exigeDadosDeComissionadoNaConstrucao` | POSITIVO |
| `rejeitaSalarioAusenteETipoDesconhecidoNaConstrucao` | POSITIVO |
| `converteSubtiposParaForm` | POSITIVO |
| `exigeDadosDeProducaoNaConstrucao` | POSITIVO |
| `construiComissionado` | POSITIVO |
| `construiProducao` | POSITIVO |
| `validaValoresVariaveisNegativos` | POSITIVO |
| `ValidacaoBeanValidation.formularioPadraoValido` (classe aninhada) | POSITIVO |

### `FolhaPagamentoServiceTest` — 7 execuções: 7 positivas

| Método/caso executado | Resultado |
|---|---|
| `totaisPorCategoriaNaoPodeSerAlterado` | POSITIVO |
| `integraServicoComRepositorioTemporario(Path)` | POSITIVO |
| `atualizaEExcluiColaboradores` | POSITIVO |
| `gerarResumoVazio` | POSITIVO |
| `gerarResumoCalculaTotalEValoresPorCategoria` | POSITIVO |
| `buscaEListaColaboradores` | POSITIVO |
| `cadastrarPropagaDuplicidade` | POSITIVO |

### `FormatadorMoedaTest` — 4 execuções: 3 positivas, 1 negativa

| Método/caso executado | Resultado | Evidência |
|---|---|---|
| `formataValorNuloComoZero` | **NEGATIVO (ERRO)** | `NullPointerException`: `Cannot invoke "java.lang.Double.doubleValue()" because "null" is null`. |
| `formataValorComMoedaBrasileira` | POSITIVO | — |
| `formataZeroNegativoEArredonda` | POSITIVO | — |
| `formataValoresAltos` | POSITIVO | — |

### `RepositorioColaboradoresTest` — 11 execuções: 10 positivas, 1 negativa

| Método/caso executado | Resultado | Evidência |
|---|---|---|
| `listaEImutavel` | POSITIVO | — |
| `exclusaoLogicaDevePreservarRegistroHistorico` | **NEGATIVO (FALHA)** | `RN009 exige manter o colaborador disponível para histórico após excluir da consulta ativa ==> expected: <true> but was: <false>`. |
| `arquivoInexistenteIniciaVazio` | POSITIVO | — |
| `atualizaCadastro` | POSITIVO | — |
| `ignoraVaziasEInformaLinhaCorrompida` | POSITIVO | — |
| `rejeitaCadastroDuplicado` | POSITIVO | — |
| `arquivoVazioIniciaVazio` | POSITIVO | — |
| `persisteTiposEmNovasInstancias` | POSITIVO | — |
| `rejeitaDuplicidadePersistida` | POSITIVO | — |
| `rejeitaTiposOuQuantidadeDeCamposIncorretos` | POSITIVO | — |
| `exclusaoFisicaDoCadastroAtivo` | POSITIVO | — |

### `FolhaPagamentoControllerTest` — 16 execuções: 15 positivas, 1 negativa

| Método/caso executado | Resultado | Evidência |
|---|---|---|
| `atualizarColaborador` | POSITIVO | — |
| `excluirColaboradorExistente` | POSITIVO | — |
| `erroDeCampoVariavelAusenteDeveApontarParaVendas` | **NEGATIVO (FALHA)** | `No errors for field 'valorVendas' of attribute 'form'`. |
| `folhaIndividualExistente` | POSITIVO | — |
| `erroDeNegocioAoAtualizar` | POSITIVO | — |
| `editarColaborador` | POSITIVO | — |
| `folhaConsolidada` | POSITIVO | — |
| `cadastroDuplicado` | POSITIVO | — |
| `erroDeCampoVariavelAusenteAssociadoIncorretamenteAMatricula` | POSITIVO | Confirma/documenta o campo incorreto associado pelo comportamento atual. |
| `errosDeValidacaoDoFormulario` | POSITIVO | — |
| `formularioNovo` | POSITIVO | — |
| `excluirColaboradorInexistente` | POSITIVO | — |
| `atualizarComErroDeValidacao` | POSITIVO | — |
| `cadastraColaboradorValido` | POSITIVO | — |
| `paginaInicial` | POSITIVO | — |
| `folhaIndividualInexistente` | POSITIVO | — |

### `FolhaPagamentoConfigTest` — 2 execuções: 2 positivas

| Método/caso executado | Resultado |
|---|---|
| `criaRepositorioComCaminhoConfigurado(Path)` | POSITIVO |
| `iniciaAplicacao` | POSITIVO |

## Bugs e melhorias identificados

### BUG-001 — Exclusão lógica não preserva o histórico

- **Área:** `RepositorioColaboradores.excluir`
- **Caso/Requisito:** `CT-037` / `RN009`
- **Comportamento atual:** o colaborador é removido do mapa e do arquivo atual.
- **Esperado:** remover da consulta ativa sem apagar os dados necessários ao histórico.
- **Evidência:** falha de `exclusaoLogicaDevePreservarRegistroHistorico`; JUnit recebeu `false` ao verificar a existência após a exclusão.

### BUG-002 — Formatação de valor nulo causa erro

- **Área:** `FormatadorMoeda.formatar`
- **Caso/Requisito:** `CT-038` / `RN010`
- **Comportamento atual:** a API aceita `double` primitivo e a chamada com `null` causa `NullPointerException`.
- **Esperado:** representar valor variável ausente como `R$ 0,00`.
- **Evidência:** erro de `formataValorNuloComoZero`: `Cannot invoke "java.lang.Double.doubleValue()" because "null" is null`.

### BUG-003 — Erro de vendas ausentes não é associado ao campo de vendas

- **Área:** `FolhaPagamentoController.cadastrar`
- **Caso/Requisito:** `CT-015` / `RF006`
- **Comportamento atual:** não há erro de validação de campo em `valorVendas`; o caminho de construção trata o valor ausente e o controller registra a falha em `matricula`.
- **Esperado:** apontar a ausência no campo `valorVendas` (e de forma equivalente no campo variável correspondente para cada tipo).
- **Evidência:** falha de `erroDeCampoVariavelAusenteDeveApontarParaVendas`: `No errors for field 'valorVendas' of attribute 'form'`. O teste `erroDeCampoVariavelAusenteAssociadoIncorretamenteAMatricula` passou ao confirmar o comportamento atual.

### Melhorias recomendadas

1. Corrigir os três defeitos e manter os testes de requisito como regressão, alterando-os para **POSITIVO** após a correção.
2. Adicionar `@DisplayName` aos três testes antigos de `ConformidadeRequisitosTest` para melhorar a leitura dos relatórios.
3. Associar cada teste parametrizado a um caso rastreável específico quando aplicável; cada parâmetro aparece como execução separada no Surefire.
4. Executar a suíte completa após a correção com `.\mvnw.cmd test jacoco:report` e confirmar que não há falhas, erros ou testes sem resultado.
5. Os relatórios Surefire XML e TXT permanecem em `target/surefire-reports/`; este documento registra uma versão legível e consolidada dos resultados desta execução.

## Observações da execução

- As oito classes foram executadas separadamente usando Maven Surefire.
- **Sem resultado:** nenhum caso foi ignorado ou ficou sem registro (`skipped=0` em todas as classes).
- A suíte contém expectativas negativas intencionais para expor divergências; portanto o resultado geral do Maven é negativo até os bugs serem corrigidos.
- Os testes do repositório usam `@TempDir`; a execução não deve depender da pasta `dados/` do projeto.
- O `pom.xml` define alvo Java 17; o XML do Surefire registra `java.specification.version=23` para o runtime desta execução. Recomenda-se também validar a suíte no JDK 17 previsto pelo projeto.
