# Relatório Final de Qualidade

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0  
**Data da execução automatizada:** 23/11/2026  
**Commit do código testado:** `d8db957`

## 1. Situação dos requisitos

Os estados “Implementado” abaixo reproduzem a avaliação de implementação da [Matriz de Conformidades](Matriz_Conformidades_Requisitos.md); não equivalem a homologação de todos os requisitos.

| Grupo | Situação segundo a matriz | Observação de verificação |
| :--- | :--- | :--- |
| RF001–RF011 | Implementados | 84 execuções automatizadas passaram; ACE-3 e ACE-4 ainda aguardam revisão de evidências manuais. |
| RNF001 | Implementado com ressalva | Revisão dos vídeos de usabilidade CT-032 pendente. |
| RNF002 | Implementado | Formulários/testes automatizados passaram; evidência CT-035 revisada. |
| RNF003 | Implementado com ressalva | Os três testes PostgreSQL/Testcontainers foram ignorados e estão bloqueados pelo acesso Docker. |
| RNF004 | Implementado com ressalva | CT-033 precisa de conclusão de revisor humano. |
| RN001–RN010 | Implementadas | CT-015/016/019/020 foram alinhados à RN010; CT-030 real continua pendente. |

## 2. Matriz de rastreabilidade resumida

| Requisito | Casos relacionados na ERS/especificação | Evidência automatizada ou manual desta revisão |
| :--- | :--- | :--- |
| RF001 | CT-001, CT-009–CT-013, CT-023 | `ct001CalculaSalarioPadrao`, `ct009RejeitaMatriculaDuplicada`, `ct010RejeitaMatriculaComPontuacao`, `ct011E012ValidaMatriculaENomeObrigatorios`, `ct013ValidaSalarioBaseObrigatorio`, `ct023ListaTresCategorias` |
| RF002 | CT-001, CT-014 | `ct001CalculaSalarioPadrao`; validação negativa |
| RF003 | CT-003–CT-005, CT-015–CT-018, CT-027 | `ct003CalculaComissaoOuZero`, `ct015E016AceitaCamposComissionadosEmBranco`, `ct027ExibeFolhaIndividualComissionada` |
| RF004 | CT-006–CT-008, CT-019–CT-022 | `ct006CalculaProducaoOuZero`, `ct019E020AceitaCamposProducaoEmBranco` |
| RF005 | CT-023 | `ct023ListaTresCategorias` |
| RF006 | CT-001, CT-003, CT-006, CT-026 | testes de cálculo e `ct026CalculaResumoPorCategoria` |
| RF007 | CT-026, CT-036 | `ct036FiltraPorCompetencia` |
| RF008 | CT-026, CT-028, CT-029, CT-031 | `ct026CalculaResumoPorCategoria`, `ct031ResumoVazio` |
| RF009 | CT-009, CT-023–CT-025, CT-037 | `ct009RejeitaMatriculaDuplicada`, `ct024AtualizaColaborador`, `ct025ExcluiColaboradorAtivo`, `ct037PreservaSnapshotDepoisDaExclusao` |
| RF010 | CT-027, CT-038, CT-040 | `ct027ExibeFolhaIndividualComissionada`, `ct038ExibeAdicionaisZerados`; CT-040 pendente por divergência de evidência |
| RF011 | CT-026, CT-028, CT-029, CT-031 | `ct026CalculaResumoPorCategoria`, `ct031ResumoVazio` |
| RNF001–RNF004 | CT-032–CT-035, CT-030 | CT-034/CT-035 revisados; CT-030/032 em revisão; CT-033 aguarda revisor; CT-030 PostgreSQL bloqueado |
| RN001–RN010 | CT-009–CT-022, CT-025, CT-036–CT-038 | Testes de duplicidade, validação, competência, histórico e valores zerados no relatório de execução |

A matriz detalhada requisito/componente/tipo de teste está em [Matriz_Conformidades_Requisitos.md](Matriz_Conformidades_Requisitos.md), e os cenários em [Casos_Teste_Rastreaveis_A1.md](Casos_Teste_Rastreaveis_A1.md).

## 3. Resultado dos testes

`.\mvnw.cmd clean verify`: **BUILD SUCCESS**; 87 execuções descobertas, 84 aprovadas, 0 falhas, 0 erros e 3 ignoradas. Os três testes ignorados são `RepositorioColaboradoresPostgresTest`; o bloqueio e o diagnóstico estão descritos no [relatório de execução](Relatorio_Execucao_Testes_Unitarios.md). O log integral é [log-verify.txt](log-verify.txt).

## 4. Métricas

| Métrica | Resultado |
| :--- | :--- |
| Cobertura de linhas | 93,38% (367/393) |
| Cobertura de instruções | 94,10% (1610/1711) |
| Cobertura de branches | 77,32% (75/97) |
| Defeitos/pendências registrados | 4: 2 corrigidos, 1 bloqueado e 1 pendente |
| Requisitos funcionais aprovados segundo fórmula PGQ | [preencher após revisão humana; implementação não equivale a homologação] |

Detalhamento e fórmulas: [Metricas_Qualidade.md](Metricas_Qualidade.md).

## 5. Evidências

Os arquivos abaixo já existiam e foram preservados. Capturas CT-034, CT-035 e CT-038 foram visualmente revisadas nesta tarefa; CT-040 foi revisado, mas não corresponde ao demonstrativo individual. Os vídeos CT-030 e CT-032 estão anexados e não foram revisados.

| Caso | Arquivos existentes | Revisão nesta tarefa |
| :--- | :--- | :--- |
| CT-030 | [CT-030-A.mp4](../09_Homologacao/evidencias/CT-030/CT-030-A.mp4), [CT-030-B.mp4](../09_Homologacao/evidencias/CT-030/CT-030-B.mp4), [CT-030-C.mp4](../09_Homologacao/evidencias/CT-030/CT-030-C.mp4) | Não revisados |
| CT-032 | [CT-032-01.mp4](../09_Homologacao/evidencias/CT-032/CT-032-01.mp4), [CT-032-02.mp4](../09_Homologacao/evidencias/CT-032/CT-032-02.mp4), [CT-032-03.mp4](../09_Homologacao/evidencias/CT-032/CT-032-03.mp4), [CT-032-04.mp4](../09_Homologacao/evidencias/CT-032/CT-032-04.mp4), [CT-032-05.mp4](../09_Homologacao/evidencias/CT-032/CT-032-05.mp4) | Não revisados |
| CT-034 | [passo1](../09_Homologacao/evidencias/CT-034/CT-034-passo1.png), [passo2](../09_Homologacao/evidencias/CT-034/CT-034-passo2.png), [passo3](../09_Homologacao/evidencias/CT-034/CT-034-passo3.png) | Revisados |
| CT-035 | [CT-035-passo1.png](../09_Homologacao/evidencias/CT-035/CT-035-passo1.png) | Revisada |
| CT-038 | [CT-038-passo1.png](../09_Homologacao/evidencias/CT-038/CT-038-passo1.png) | Revisada; mostra folha consolidada, não o demonstrativo individual do caso |
| CT-040 | [CT-040-passo1.png](../09_Homologacao/evidencias/CT-040/CT-040-passo1.png), [CT-040-passo2.png](../09_Homologacao/evidencias/CT-040/CT-040-passo2.png) | Revisadas; mostram impressão consolidada, não individual |

## 6. Conclusão

A compilação e as 84 execuções automatizadas não ignoradas passaram sem falhas ou erros. A cobertura de linhas JaCoCo excedeu a meta de 80% indicada no PGQ. Isso não basta para homologar a versão: três testes PostgreSQL estão bloqueados, os vídeos CT-030/CT-032 e a inspeção CT-033 aguardam revisão humana, e as evidências de CT-038/CT-040 precisam ser refeitas para o demonstrativo individual. ACE-3, ACE-4 e ACE-6 permanecem pendentes; nenhuma opção do [Termo de Homologação](Termo_Homologacao.md) foi marcada.
