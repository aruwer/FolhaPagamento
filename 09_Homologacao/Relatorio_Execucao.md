# Relatório de Execução dos Testes

## Situação

**Execução registrada em 23/11/2026.** O comando `.\mvnw.cmd clean verify` foi executado na branch `test/QDS_2.0.0/testes-finais`.

- Build: **SUCCESS**
- Testes: **68**
- Falhas: **0**
- Erros: **0**
- Ignorados: **3** (`RepositorioColaboradoresPostgresTest`)
- Cobertura JaCoCo: **93% de instruções** e **74% de branches**

## Inventário (código atual)

| Classe de teste | Casos |
| :--- | ---: |
| `ColaboradorFormTest` | 11 |
| `ColaboradorTest` | 16 |
| `ConformidadeRequisitosTest` | 3 |
| `FolhaPagamentoConfigTest` | 3 |
| `FolhaPagamentoControllerTest` | 17 |
| `FolhaPagamentoServiceTest` | 7 |
| `FormatadorMoedaTest` | 4 |
| `RepositorioColaboradoresPostgresTest` | 3 |
| `RepositorioColaboradoresTest` | 4 |
| **Total Maven** | **68** |

Contagem feita por `@Test` e `@ParameterizedTest` no código. Um método parametrizado pode gerar mais de uma execução.

## Como executar e registrar

1. Subir o ambiente PostgreSQL: `docker compose up -d`.
2. Executar a suíte: `.\mvnw.cmd clean verify`.
3. Salvar o log em `09_Homologacao/evidencias/log-verify-AAAA-MM-DD.txt`.
4. Preencher a tabela abaixo com os resultados da execução.
5. Preservar o relatório JaCoCo gerado em `target/site/jacoco/`; quando necessário para a entrega, copiar uma cópia para `09_Homologacao/evidencias/jacoco/`.

## Resultados da execução oficial

| Classe | Executados | Aprovados | Falhas | Erros | Ignorados | Data | Executor |
| :--- | ---: | ---: | ---: | ---: | ---: | :--- | :--- |
| `ColaboradorFormTest` | 11 | 11 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `ColaboradorTest` | 16 | 16 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `ConformidadeRequisitosTest` | 3 | 3 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `FolhaPagamentoConfigTest` | 3 | 3 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `FolhaPagamentoServiceTest` | 7 | 7 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `FolhaPagamentoControllerTest` | 17 | 17 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `FormatadorMoedaTest` | 4 | 4 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `RepositorioColaboradoresTest` | 4 | 4 | 0 | 0 | 0 | 23/11/2026 | Amanda Ruwer |
| `RepositorioColaboradoresPostgresTest` | 3 | 0 | 0 | 0 | 3 | 23/11/2026 | Amanda Ruwer |

## Testes que dependem de Docker

`RepositorioColaboradoresPostgresTest` usa Testcontainers e permanece ignorado neste ambiente por uma incompatibilidade de conexão Java/Testcontainers com o endpoint do Docker Desktop, embora o Docker CLI e o PostgreSQL do Compose estejam ativos. O resultado automatizado continua registrado como **ignorado**, não como aprovado. Como alternativa de homologação, execute os passos manuais descritos em [CT-030](Roteiro_Testes_Manuais.md#ct-030--alternativa-manual-aos-testes-postgresql-ignorados) contra o PostgreSQL do Compose e registre esse resultado separadamente com suas evidências. A execução manual não altera o status da suíte Testcontainers.
