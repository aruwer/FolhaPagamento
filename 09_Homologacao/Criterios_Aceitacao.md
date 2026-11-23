# Critérios de Aceitação (ERS, seção 2.4)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0
**Regra:** um critério só recebe "Aprovado" quando há resultado registrado em log e evidência manual correspondente revisada. Enquanto isso, a situação é "Pendente".

Os estados abaixo refletem o log da suíte e a revisão das evidências disponíveis nesta entrega. Os vídeos de CT-030 e CT-032 ainda não foram revisados. A suíte Testcontainers registrou 3 testes ignorados; consulte o relatório consolidado para o diagnóstico.

| ID | Critério (ERS 2.4) | Testes automatizados | Casos manuais | Situação | Evidência |
| :--- | :--- | :--- | :--- | :---: | :--- |
| ACE-1 | Permitir o cadastro dos três tipos de colaboradores | `FolhaPagamentoWebCasosDeTesteTest` (`ct001CalculaSalarioPadrao`, `ct003CalculaComissaoOuZero`, `ct006CalculaProducaoOuZero`, `ct023ListaTresCategorias`) e testes de domínio | CT-034 | **Aprovado** | [capturas CT-034, revisadas](evidencias/CT-034/); [roteiro](Roteiro_Testes_Manuais.md); [log da suíte](../docs/log-verify.txt) |
| ACE-2 | Validar os dados obrigatórios conforme as regras de negócio | `FolhaPagamentoWebCasosDeTesteTest` (`ct009RejeitaMatriculaDuplicada`, `ct010RejeitaMatriculaComPontuacao`, `ct011E012ValidaMatriculaENomeObrigatorios`, `ct013ValidaSalarioBaseObrigatorio`, `ct014E018RejeitaValoresNegativos`, `ct015E016AceitaCamposComissionadosEmBranco`, `ct019E020AceitaCamposProducaoEmBranco`) e testes de formulário | CT-035 | **Aprovado** | [captura CT-035, revisada](evidencias/CT-035/CT-035-passo1.png); [roteiro](Roteiro_Testes_Manuais.md); [log da suíte](../docs/log-verify.txt) |
| ACE-3 | Calcular corretamente os salários de cada categoria | `FolhaPagamentoWebCasosDeTesteTest` (`ct001CalculaSalarioPadrao`, `ct003CalculaComissaoOuZero`, `ct006CalculaProducaoOuZero`, `ct027ExibeFolhaIndividualComissionada`) e testes de domínio | CT-030-A, CT-030-B, CT-030-C | **Pendente** | [vídeos CT-030 — anexados, não revisados](evidencias/CT-030/); [log da suíte](../docs/log-verify.txt); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-4 | Gerar folhas individuais e consolidadas respeitando os filtros de mês e ano | `FolhaPagamentoWebCasosDeTesteTest.ct036FiltraPorCompetencia`, `RepositorioColaboradoresTest` | Revisão de evidência manual adicional pendente | **Pendente** | [teste automatizado ct036FiltraPorCompetencia](../src/test/java/folhapagamento/FolhaPagamentoWebCasosDeTesteTest.java); [log da suíte](../docs/log-verify.txt); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-5 | Exibir o total geral da folha de pagamento do período | `FolhaPagamentoWebCasosDeTesteTest` (`ct026CalculaResumoPorCategoria`, `ct031ResumoVazio`) e `FolhaPagamentoServiceTest` | CT-038 (visual) | **Aprovado** | [captura CT-038, revisada](evidencias/CT-038/CT-038-passo1.png); [roteiro](Roteiro_Testes_Manuais.md); [log da suíte](../docs/log-verify.txt) |
| ACE-6 | Possibilitar expansão para novos tipos de remuneração | `ColaboradorTest` (cálculos polimórficos) | CT-033 (inspeção; conclusão do revisor pendente) | **Pendente** | [roteiro CT-033](Roteiro_Testes_Manuais.md#ct-033--avaliar-extensibilidade-do-domínio-inspeção-de-código) |

## Registro da execução

| ID | Executor | Data | Commit | Resultado | Observações |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ACE-1 | Amanda Ruwer | 23/11/2026 | `d8db957` | Aprovado | Suíte real e capturas CT-034 revisadas |
| ACE-2 | Amanda Ruwer | 23/11/2026 | `d8db957` | Aprovado | Suíte real e captura CT-035 revisada |
| ACE-3 |  |  |  | Pendente | Vídeos CT-030 anexados, mas ainda não revisados |
| ACE-4 |  |  |  | Pendente | Teste `ct036FiltraPorCompetencia` passou; revisão de evidência manual adicional pendente |
| ACE-5 | Amanda Ruwer | 23/11/2026 | `d8db957` | Aprovado | Suíte real e captura CT-038 revisada |
| ACE-6 |  |  |  | Pendente | Aguardando conclusão e assinatura de revisor humano em CT-033 |
