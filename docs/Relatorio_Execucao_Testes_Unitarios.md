# Relatório de Execução dos Testes

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0
**Data/hora:** 23/11/2026, 20:23 (-03:00)
**Commit do código testado:** `d8db957` (`test: adiciona rótulos de caso e requisito aos testes`)
**Comando:** `.\mvnw.cmd clean verify 2>&1 | Tee-Object -FilePath docs\log-verify.txt`
**Log integral:** [log-verify.txt](log-verify.txt)

## Resultado

- Build Maven: **SUCCESS**
- Execuções descobertas: **87**
- Executadas e aprovadas: **84**
- Falhas: **0**
- Erros: **0**
- Ignoradas: **3**, todas em `RepositorioColaboradoresPostgresTest`

O contador Maven de 87 inclui os três testes ignorados. Não foram reclassificados como aprovados.

## Resultado por classe

| Classe | Executados | Aprovados | Falhas | Erros | Ignorados |
| :--- | ---: | ---: | ---: | ---: | ---: |
| `ColaboradorFormTest` (inclui classe aninhada) | 11 | 11 | 0 | 0 | 0 |
| `ColaboradorTest` | 16 | 16 | 0 | 0 | 0 |
| `ConformidadeRequisitosTest` | 3 | 3 | 0 | 0 | 0 |
| `FolhaPagamentoControllerTest` | 17 | 17 | 0 | 0 | 0 |
| `FolhaPagamentoServiceTest` | 7 | 7 | 0 | 0 | 0 |
| `FolhaPagamentoWebApplicationTest` | 2 | 2 | 0 | 0 | 0 |
| `FolhaPagamentoWebCasosDeTesteTest` | 20 | 20 | 0 | 0 | 0 |
| `FormatadorMoedaTest` | 4 | 4 | 0 | 0 | 0 |
| `RepositorioColaboradoresPostgresTest` | 0 | 0 | 0 | 0 | 3 |
| `RepositorioColaboradoresTest` | 4 | 4 | 0 | 0 | 0 |
| **Total** | **84** | **84** | **0** | **0** | **3** |

## Cobertura JaCoCo

Valores lidos do relatório gerado por essa execução em `target/site/jacoco/`:

| Métrica | Cobertos / total | Resultado |
| :--- | ---: | ---: |
| Linhas | 367 / 393 | 93,38% |
| Instruções | 1610 / 1711 | 94,10% |
| Branches | 75 / 97 | 77,32% |

## Testes PostgreSQL / Testcontainers

O Docker Desktop estava acessível pelo CLI: `docker info`, `docker context ls` e `docker version` retornaram o servidor Docker Desktop 4.99.0, Engine 29.9.3 e contexto `desktop-linux`. A imagem `postgres:17-alpine` já estava no cache local.

Foi feita a tentativa solicitada com o contexto `desktop-linux` ativo e `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine`. O Testcontainers 1.21.3 não conseguiu validar o daemon: a estratégia de named pipe recebeu **HTTP 400** na consulta de informações do Docker, com os campos da resposta vazios. Os três testes foram ignorados por `disabledWithoutDocker = true`.

**Situação CT-030 automatizado (parcial): Bloqueado.** A causa observada é a resposta HTTP 400 do endpoint ao cliente Testcontainers; a causa subjacente dessa resposta não foi determinada. O teste não foi removido nem teve sua condição alterada. O diagnóstico de CLI e a tentativa de `DOCKER_HOST` constam no log e no registro de defeitos.

A seção manual de CT-030 em [Roteiro_Testes_Manuais.md](../09_Homologacao/Roteiro_Testes_Manuais.md) é separada da execução Testcontainers. Os vídeos de evidência estão anexados, mas ainda aguardam revisão humana.

## Reprodutibilidade

O log integral registra a execução a partir do commit `d8db957`. O trabalho posterior a esse commit atualizou somente documentação e artefatos do relatório; nenhum código ou teste foi alterado após a execução.
