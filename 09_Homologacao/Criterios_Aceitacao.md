# Critérios de Aceitação (ERS, seção 2.4)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0  
**Regra:** um critério só recebe "Aprovado" quando há resultado automatizado registrado em log **e** evidência manual correspondente revisada.

| ID | Critério (ERS 2.4) | Testes automatizados | Casos manuais / sistema | Situação | Evidência |
| :--- | :--- | :--- | :--- | :---: | :--- |
| ACE-1 | Permitir o cadastro dos três tipos de colaboradores | `ct001CalculaSalarioPadrao`, `ct003CalculaComissaoOuZero`, `ct006CalculaProducaoOuZero`, `ct023ListaTresCategorias`, `ColaboradorTest` | CT-034, CT-023 | **Aprovado** | [CT-034](evidencias/CT-034/), [CT-023](evidencias/CT-023/), [log](../04_Testes/log-verify.txt) |
| ACE-2 | Validar os dados obrigatórios conforme as regras de negócio | `ct009*`, `ct010*`, `ct011E012*`, `ct013*`, `ct014E018*`, `ct015E016*`, `ct019E020*`, `ColaboradorFormTest`, `CompetenciaEBordasTest` | CT-035 | **Aprovado** | [CT-035](evidencias/CT-035/CT-035-passo1.png), [log](../04_Testes/log-verify.txt) |
| ACE-3 | Calcular corretamente os salários de cada categoria | `ct001*`, `ct003*`, `ct006*`, `ct027*`, `ColaboradorTest`, `RepositorioColaboradoresPostgresTest` | CT-026, CT-027, CT-030 (T130) | **Aprovado** | [folha 11/2026](evidencias/CT-026/), [demonstrativo E002](evidencias/CT-027/), [T130](evidencias/CT-030/CT-030-A3-T130-reinicio.txt) |
| ACE-4 | Gerar folhas individuais e consolidadas respeitando os filtros de mês e ano | `ct036FiltraPorCompetencia`, `ct039ResumePorCategoriaECompetencia`, `CompetenciaEBordasTest.Web`, `RepositorioColaboradoresTest` | CT-036, CT-027, CT-038 | **Aprovado** | [11/2026 × 12/2026 × 01/2027](evidencias/CT-036/), [demonstrativos](evidencias/CT-038/) |
| ACE-5 | Exibir o total geral da folha de pagamento do período | `ct026CalculaResumoPorCategoria`, `ct039*`, `ct031ResumoVazio`, `FolhaPagamentoServiceTest` | CT-026, CT-036 | **Aprovado** | Total R$ 9.600,00 em [11/2026](evidencias/CT-026/) e R$ 7.800,00 em [12/2026](evidencias/CT-036/) |
| ACE-6 | Possibilitar expansão para novos tipos de remuneração | `ConformidadeRequisitosTest.novoTipoDeRemuneracaoNaoAlteraCalculosExistentes` | CT-033 (inspeção) | **Aprovado** | [inspeção CT-033](Roteiro_Testes_Manuais.md#ct-033--avaliar-extensibilidade-do-domínio-inspeção-de-código) (assinatura da revisora no roteiro) |

**Resultado: 6 de 6 critérios aprovados.**

## Registro da execução

| ID | Executor | Data | Versão | Resultado | Observações |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ACE-1 | Amanda Ruwer (CT-034) + suíte automatizada | 23/11/2026 | 2.0.0 | Aprovado | Capturas CT-034 revisadas; 105/105 testes aprovados |
| ACE-2 | Amanda Ruwer (CT-035) + suíte automatizada | 23/11/2026 | 2.0.0 | Aprovado | Captura CT-035 revisada |
| ACE-3 | Revisão de fechamento A3 + suíte automatizada + vídeos da equipe (CT-030-A/B/C) | 23/11/2026 | 2.0.0 | Aprovado | Valores conferidos: 3.000 / 2.500 / 2.100 / 2.000 / 2.140 |
| ACE-4 | Revisão de fechamento A3 + suíte automatizada | 23/11/2026 | 2.0.0 | Aprovado | Competências 11/2026 e 12/2026 com conteúdos diferentes; 01/2027 não processada |
| ACE-5 | Revisão de fechamento A3 + suíte automatizada | 23/11/2026 | 2.0.0 | Aprovado | Totais por categoria e geral conferidos |
| ACE-6 | Revisão de fechamento A3 (inspeção) + teste automatizado | 23/11/2026 | 2.0.0 | Aprovado | Assinatura da revisora técnica no CT-033 |

Consolidação por cenário (resultado esperado × obtido): [Relatorio_Testes_Aceitacao.md](Relatorio_Testes_Aceitacao.md).
