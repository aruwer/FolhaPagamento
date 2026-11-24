# Matriz de Conformidade de Requisitos

**Base:** [Documento de Visão e ERS](Documento_Visao_e_ERS.md) (RF001–RF011, RN001–RN010, RNF001–RNF004)  
**Versão do software:** 2.0.0 · **Execução de referência:** 23/11/2026 — `.\mvnw.cmd clean verify` ([log](../04_Testes/log-verify.txt))

## Como ler o status

- **Verificado — aprovado:** implementado, com caso(s) de teste executado(s) e aprovado(s) e evidência registrada (log da suíte e/ou evidência manual em `09_Homologacao/evidencias/`).
- **Implementado — não verificado:** existe no código, mas sem execução aprovada. *(Nenhum requisito nesta situação.)*
- **Não implementado:** ausente no código. *(Nenhum requisito nesta situação.)*

## Requisitos funcionais

| Requisito | Descrição resumida | Onde está implementado | Casos de teste | Testes automatizados (principais) | Evidência manual | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| RF001 | Cadastro com matrícula, nome, salário base e tipo | `ColaboradorForm`, `FolhaPagamentoController.cadastrar`, `colaboradores/form.html` | CT-001, CT-009 a CT-013 | `FolhaPagamentoWebCasosDeTesteTest.ct001*`, `ct009*`–`ct013*`; `ColaboradorFormTest` | CT-034, CT-032 | Verificado — aprovado |
| RF002 | Cadastro de colaborador padrão | `ColaboradorPadrao` | CT-001, CT-002, CT-014 | `ColaboradorTest`, `ct001CalculaSalarioPadrao` | CT-034 | Verificado — aprovado |
| RF003 | Cadastro comissionado: vendas e percentual | `ColaboradorComissionado` | CT-003 a CT-005, CT-015 a CT-018 | `ct003CalculaComissaoOuZero`, `ct015E016*`, `ColaboradorTest` | CT-034 | Verificado — aprovado |
| RF004 | Cadastro por produção: quantidade e valor unitário | `ColaboradorProducao` | CT-006 a CT-008, CT-019 a CT-022 | `ct006CalculaProducaoOuZero`, `ct019E020*`, `ColaboradorTest` | CT-034 | Verificado — aprovado |
| RF005 | Lista de colaboradores ativos com dados cadastrais e remuneratórios | `index.html`, `FolhaPagamentoController.inicio` | CT-023 a CT-025 | `ct023ListaTresCategorias`, `FolhaPagamentoControllerTest.paginaInicial` | [CT-023](../09_Homologacao/evidencias/CT-023/) | Verificado — aprovado |
| RF006 | Cálculo do salário final por competência | `Colaborador.getSalarioFinal`, `RepositorioColaboradoresJpa.processarFolha` | CT-001 a CT-008, CT-036 | `ct036FiltraPorCompetencia`, `RepositorioColaboradoresPostgresTest` | [CT-026](../09_Homologacao/evidencias/CT-026/), [CT-036](../09_Homologacao/evidencias/CT-036/) | Verificado — aprovado |
| RF007 | Folha por mês/ano com matrícula, nome, tipo e salário final | `folha.html`, `FolhaPagamentoController.folha` | CT-026, CT-036, CT-037 | `ct026*`, `ct036*`, `CompetenciaEBordasTest.Web` | [CT-026](../09_Homologacao/evidencias/CT-026/), [CT-036](../09_Homologacao/evidencias/CT-036/) | Verificado — aprovado |
| RF008 | Total da folha do período | `ResumoFolha.totalFolha`, `FolhaPagamentoService.resumir` | CT-028, CT-031, CT-039 | `ct026*`, `ct031ResumoVazio`, `ct039ResumePorCategoriaECompetencia` | [CT-026](../09_Homologacao/evidencias/CT-026/) | Verificado — aprovado |
| RF009 | Inclusão, alteração, exclusão lógica e consulta | `FolhaPagamentoController`, `RepositorioColaboradoresJpa.excluir` (`excluido_em`) | CT-009, CT-023 a CT-025, CT-037 | `ct024AtualizaColaborador`, `ct025ExcluiColaboradorAtivo`, `ct037*`, `RepositorioColaboradoresTest` | [CT-038/CT-037](../09_Homologacao/evidencias/CT-038/) | Verificado — aprovado |
| RF010 | Demonstrativo individual com adicionais, zerados quando ausentes | `folha-individual.html`, `Colaborador.valorOuZero` | CT-027, CT-038, CT-040 | `ct027*`, `ct038ExibeAdicionaisZerados`, `ct038BrancoAceitaVendasEmBranco` | [CT-027](../09_Homologacao/evidencias/CT-027/), [CT-038](../09_Homologacao/evidencias/CT-038/), [CT-040](../09_Homologacao/evidencias/CT-040/) | Verificado — aprovado |
| RF011 | Resumo com quantidade, total e subtotal por categoria | `ResumoFolha`, `index.html`, `folha.html` | CT-029, CT-031, CT-039 | `ct026*`, `ct039*`, `FolhaPagamentoServiceTest` | [CT-026](../09_Homologacao/evidencias/CT-026/) | Verificado — aprovado |

**Requisitos funcionais verificados: 11 / 11 = 100%.**

## Requisitos não funcionais

| Requisito | Descrição resumida | Onde está implementado | Casos | Verificação | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RNF001 | Interface amigável e intuitiva | Templates Thymeleaf, `app.css`, `colaborador-form.js` | CT-032, CT-034, CT-035, CT-040 | Avaliação de usabilidade gravada pela equipe (vídeos CT-032-01 a 05) e capturas CT-034/035/040 | Verificado — aprovado |
| RNF002 | Validação antes de qualquer cálculo | Bean Validation em `ColaboradorForm`, `BindingResult`, validações do domínio | CT-010 a CT-022, CT-035 | `ColaboradorFormTest`, `ct010*`–`ct020*`, `CompetenciaEBordasTest` | Verificado — aprovado |
| RNF003 | Persistência em banco SQL | `RepositorioColaboradoresJpa`, migrations V1/V2 (Flyway), `docker-compose.yml` | CT-030, CT-037 | 3 testes em PostgreSQL 17 real (Testcontainers) + CT-030-D (reinício da aplicação) | Verificado — aprovado |
| RNF004 | Novos tipos sem alterar cálculos existentes | Hierarquia polimórfica `Colaborador` → `getAdicional()` | CT-033 | `ConformidadeRequisitosTest.novoTipoDeRemuneracaoNaoAlteraCalculosExistentes` + inspeção registrada em CT-033 | Verificado — aprovado |

## Regras de negócio

| Regra | Descrição resumida | Onde está implementado | Casos | Testes automatizados | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RN001 | Matrícula única | `RepositorioColaboradoresJpa.cadastrar`, `UNIQUE` em `V1__schema.sql` | CT-009 | `ct009*`, `RepositorioColaboradoresPostgresTest` | Verificado — aprovado |
| RN002 | Nome obrigatório | `@NotBlank` em `ColaboradorForm`, validação em `Colaborador` | CT-012 | `ct011E012*`, `ColaboradorTest` | Verificado — aprovado |
| RN003 | Salário base não negativo | `@DecimalMin`, `Colaborador.validarNaoNegativo` | CT-002, CT-014 | `ct014E018*`, `ColaboradorTest`, `CompetenciaEBordasTest.Dominio` | Verificado — aprovado |
| RN004 | Valor de vendas não negativo | `ColaboradorComissionado` | CT-004, CT-017 | `ct014E018*`, `ColaboradorTest` | Verificado — aprovado |
| RN005 | Percentual de comissão não negativo | `ColaboradorComissionado` | CT-005, CT-018 | `ct014E018*`, `ColaboradorTest` | Verificado — aprovado |
| RN006 | Quantidade produzida não negativa | `ColaboradorProducao` | CT-007, CT-021 | `ct014E018*`, `ColaboradorTest` | Verificado — aprovado |
| RN007 | Valor por unidade não negativo | `ColaboradorProducao` | CT-008, CT-022 | `ct014E018*`, `ColaboradorTest` | Verificado — aprovado |
| RN008 | Salário final calculado pelo sistema | `Colaborador.getSalarioFinal()` (método `final`) | CT-001 a CT-008, CT-036 | `ColaboradorTest`, `ConformidadeRequisitosTest` | Verificado — aprovado |
| RN009 | Exclusão lógica preserva histórico | `ColaboradorJpaEntity.excluirLogicamente`, `excluido_em` (V2), snapshots em `folhas_processadas` | CT-025, CT-037 | `ct037*`, `RepositorioColaboradoresPostgresTest.congelaFolha*` | Verificado — aprovado |
| RN010 | Valores variáveis ausentes exibidos como R$ 0,00 | `Colaborador.valorOuZero`, `FormatadorMoeda`, `folha-individual.html` | CT-004, CT-005, CT-007, CT-008, CT-027, CT-038, CT-040 | `ct038*`, `ct015E016*`, `ct019E020*`, `FormatadorMoedaTest` | Verificado — aprovado |

## Pontos fora da ERS (decisões registradas)

- O percentual de comissão é limitado a 100% (`@DecimalMax("100.0")` em `ColaboradorForm` e validação equivalente no domínio). A ERS exige apenas que não seja negativo; o limite superior foi mantido por coerência de negócio (comissão acima do valor vendido não faz sentido) e está coberto por `ColaboradorTest.validaLimitesDeComissao`.
- A elegibilidade de um colaborador para uma competência considera a data de cadastro e a data de exclusão: quem foi cadastrado depois do fim do mês não entra na folha daquele mês, e quem foi excluído durante o mês ainda entra na folha do próprio mês. Comportamento demonstrado nas evidências de [CT-036](../09_Homologacao/evidencias/CT-036/).
- Para incluir um novo tipo de remuneração basta criar uma subclasse de `Colaborador`; para que ele seja **cadastrável pela interface e persistido**, é necessário também acrescentar o valor ao enum `TipoColaborador`, ao formulário e a uma nova migration. Nenhum cálculo existente é alterado (ver CT-033).
