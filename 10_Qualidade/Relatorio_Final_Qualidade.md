# Relatório Final de Qualidade

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Entrega:** A3  
**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta  
**Data de referência:** 23/11/2026 · **Branch:** `feat/QDS_2.0.0/fechamento-a3`

---

## 1. Situação dos requisitos

| Grupo | Total | Verificados e aprovados | Situação |
| :--- | :---: | :---: | :--- |
| Requisitos funcionais (RF001–RF011) | 11 | 11 | **100% atendidos** |
| Requisitos não funcionais (RNF001–RNF004) | 4 | 4 | **100% atendidos** |
| Regras de negócio (RN001–RN010) | 10 | 10 | **100% atendidas** |
| Critérios gerais de aceitação (ERS 2.4) | 6 | 6 | **100% aprovados** |

"Verificado e aprovado" exige caso de teste executado com resultado aprovado e evidência registrada (regra do PGQ 6.4-3).

## 2. Matriz de rastreabilidade

| Requisito | Casos de teste | Testes automatizados (principais) | Evidência manual | Situação |
| :--- | :--- | :--- | :--- | :---: |
| RF001 Cadastro | CT-001, CT-009–CT-013 | `ct001*`, `ct009*`–`ct013*`, `ColaboradorFormTest` | [CT-034](../09_Homologacao/evidencias/CT-034/) | ✔ |
| RF002 Padrão | CT-001, CT-002, CT-014 | `ColaboradorTest`, `ct001*` | [CT-034](../09_Homologacao/evidencias/CT-034/) | ✔ |
| RF003 Comissionado | CT-003–CT-005, CT-015–CT-018 | `ct003*`, `ct015E016*` | [CT-027](../09_Homologacao/evidencias/CT-027/) | ✔ |
| RF004 Produção | CT-006–CT-008, CT-019–CT-022 | `ct006*`, `ct019E020*` | [CT-040](../09_Homologacao/evidencias/CT-040/) | ✔ |
| RF005 Consulta | CT-023–CT-025 | `ct023*`, `paginaInicial` | [CT-023](../09_Homologacao/evidencias/CT-023/) | ✔ |
| RF006 Cálculo por competência | CT-001–CT-008, CT-036 | `ct036*`, `RepositorioColaboradoresPostgresTest` | [CT-026](../09_Homologacao/evidencias/CT-026/) | ✔ |
| RF007 Folha por mês/ano | CT-026, CT-036, CT-037 | `ct026*`, `ct036*`, `CompetenciaEBordasTest.Web` | [CT-036](../09_Homologacao/evidencias/CT-036/) | ✔ |
| RF008 Total da folha | CT-028, CT-031, CT-039 | `ct026*`, `ct031*`, `ct039*` | [CT-026](../09_Homologacao/evidencias/CT-026/) | ✔ |
| RF009 Manutenção com histórico | CT-009, CT-023–CT-025, CT-037 | `ct024*`, `ct025*`, `ct037*` | [CT-038 (E004 excluído)](../09_Homologacao/evidencias/CT-038/) | ✔ |
| RF010 Demonstrativo individual | CT-027, CT-038, CT-040 | `ct027*`, `ct038*` | [CT-038](../09_Homologacao/evidencias/CT-038/), [CT-040](../09_Homologacao/evidencias/CT-040/) | ✔ |
| RF011 Resumo por categoria | CT-029, CT-031, CT-039 | `ct026*`, `ct039*` | [CT-026](../09_Homologacao/evidencias/CT-026/) | ✔ |
| RNF001 Usabilidade | CT-032, CT-034, CT-035, CT-040 | — | [CT-032](../09_Homologacao/evidencias/CT-032/) | ✔ |
| RNF002 Validação | CT-010–CT-022, CT-035 | `ColaboradorFormTest`, `ct010*`–`ct020*` | [CT-035](../09_Homologacao/evidencias/CT-035/) | ✔ |
| RNF003 Persistência SQL | CT-030, CT-037 | `RepositorioColaboradoresPostgresTest` (PostgreSQL 17) | [CT-030](../09_Homologacao/evidencias/CT-030/) | ✔ |
| RNF004 Extensibilidade | CT-033 | `novoTipoDeRemuneracaoNaoAlteraCalculosExistentes` | [inspeção](../09_Homologacao/Roteiro_Testes_Manuais.md#ct-033--avaliar-extensibilidade-do-domínio-inspeção-de-código) | ✔ |
| RN001–RN010 | CT-002, CT-004–CT-022, CT-025, CT-036–CT-038 | ver [matriz de conformidade](../01_Requisitos/Matriz_Conformidade_Requisitos.md#regras-de-negócio) | — | ✔ |

Rastreabilidade completa: requisito × caso de uso × classe na [ERS, seção 3](../01_Requisitos/Documento_Visao_e_ERS.md); requisito × implementação × teste na [Matriz de conformidade](../01_Requisitos/Matriz_Conformidade_Requisitos.md); requisito × caso na [seção 5 dos casos de teste](../04_Testes/Casos_Teste_Rastreaveis.md#5-rastreabilidade-requisito-a-requisito).

## 3. Resultados dos testes

| Nível | Executados | Aprovados | Reprovados / erros | Ignorados | Relatório |
| :--- | :---: | :---: | :---: | :---: | :--- |
| Unitário | 64 | 64 | 0 | 0 | [Relatorio_Execucao_Testes.md](../04_Testes/Relatorio_Execucao_Testes.md) |
| Integração (H2, MockMvc e PostgreSQL real) | 41 | 41 | 0 | 0 | [Relatorio_Testes_Integracao.md](../04_Testes/Relatorio_Testes_Integracao.md) |
| Casos de teste especificados (CT-001–CT-040) | 40 | 40 | 0 | 0 | [registro de execução](../04_Testes/Casos_Teste_Rastreaveis.md#6-registro-de-execução) |
| Aceitação (cenários) | 15 | 15 | 0 | 0 | [Relatorio_Testes_Aceitacao.md](../09_Homologacao/Relatorio_Testes_Aceitacao.md) |

Build: `.\mvnw.cmd clean verify` → **BUILD SUCCESS**, "All coverage checks have been met" ([log](../04_Testes/log-verify.txt)).

## 4. Métricas

| Métrica | Resultado | Meta PGQ | Situação |
| :--- | :---: | :---: | :---: |
| Cobertura de linhas | 96,98% | ≥ 80% | ✔ |
| Cobertura de branches | 95,96% | ≥ 80% | ✔ |
| Defeitos encontrados / corrigidos | 6 / 6 | — | ✔ |
| Defeitos críticos (S1) e altos (S2) abertos | 0 / 0 | 0 | ✔ |
| Requisitos funcionais atendidos | 100% | 100% | ✔ |
| Casos de teste executados e aprovados | 40 / 40 | 100% | ✔ |
| PRs integrados com revisão registrada (até o #36) | 21,4% (3/14) | 100% | ✘ (NC-01) |

Detalhamento e fórmulas: [Metricas_Qualidade.md](Metricas_Qualidade.md).

## 5. Evidências

| Tipo | Local |
| :--- | :--- |
| Log integral da suíte | [04_Testes/log-verify.txt](../04_Testes/log-verify.txt) |
| Relatórios Surefire (TXT/XML) | [04_Testes/surefire-reports/](../04_Testes/surefire-reports/) |
| Relatório JaCoCo (HTML, CSV, XML) | [10_Qualidade/evidencias/jacoco/](evidencias/jacoco/index.html) |
| Capturas, vídeos e PDF por caso de teste | [09_Homologacao/evidencias/](../09_Homologacao/evidencias/) (CT-023, 026, 027, 030, 032, 034, 035, 036, 038, 040) |
| Persistência após reinício (passos, HTTP, SQL) | [CT-030-A3-T130-reinicio.txt](../09_Homologacao/evidencias/CT-030/CT-030-A3-T130-reinicio.txt), [CT-030-D-A3-persistencia-apos-reinicio.txt](../09_Homologacao/evidencias/CT-030/CT-030-D-A3-persistencia-apos-reinicio.txt) |
| Execução E2E automatizada (Bruno CLI 31/31 e Cypress 9/9), validada em 24/11/2026 | [evidencias-auto/](../09_Homologacao/evidencias-auto/) (relatório HTML, respostas HTTP, capturas, vídeos e PDF de impressão) |
| Histórico de commits, PRs e Issues | <https://github.com/aruwer/FolhaPagamentoA3> |

## 6. Conclusões

1. **O produto está conforme.** Todos os 11 RF, 4 RNF e 10 RN estão implementados e verificados por casos executados, com evidência objetiva; os 6 critérios de aceitação da ERS foram aprovados.
2. **A suíte é completa e confiável.** 105 testes sem falhas e sem testes ignorados, incluindo persistência no mesmo SGBD de produção. A cobertura (96,98% / 95,96%) supera a meta e passou a ser verificada automaticamente a cada build.
3. **Os defeitos foram tratados.** Seis defeitos encontrados, todos corrigidos e validados, nenhum S1/S2. A execução em PostgreSQL real e a revisão visual das evidências encontraram problemas que a suíte em H2 não mostrava (BUG-005 e BUG-006).
4. **Há ressalvas de processo, não de produto.** O registro formal de revisão dos PRs (NC-01) e o registro dos defeitos nas Issues (NC-06) não seguiram o PGQ; as ações corretivas estão definidas no [Relatório de Revisão Técnica](Relatorio_Revisao_Tecnica.md).
5. **Recomendação:** homologar a versão 2.0.0 **com ressalvas de processo** — parecer formal no [Termo de Homologação](../09_Homologacao/Termo_Homologacao.md).
