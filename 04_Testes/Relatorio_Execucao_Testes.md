# Relatório de Execução dos Testes Automatizados

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Branch:** `feat/QDS_2.0.0/fechamento-a3`  
**Data/hora:** 23/11/2026, 21:49 (-03:00)  
**Comando:** `.\mvnw.cmd clean verify` (saída integral em [log-verify.txt](log-verify.txt))  
**Ambiente:** Windows 11 Home, JDK 23.0.1 (compilação com `release 17`), Maven 3.9.9 (wrapper), Docker Desktop (Engine 29.9.3), Testcontainers 1.21.4, PostgreSQL 17-alpine  
**Relatórios brutos:** [surefire-reports/](surefire-reports/) (TXT/XML por classe) e [relatório JaCoCo](../10_Qualidade/evidencias/jacoco/index.html)

## 1. Resultado geral

| Indicador | Resultado |
| :--- | ---: |
| Build Maven | **BUILD SUCCESS** |
| Testes executados | **105** |
| Aprovados | **105** |
| Falhas / erros | **0 / 0** |
| Ignorados | **0** |
| Verificação de cobertura (`jacoco:check`, mínimo 80%) | **All coverage checks have been met** |
| Tempo total do build | 36,9 s |

## 2. Testes unitários (seção 6.3 do enunciado)

Testes sem contexto Spring nem banco de dados; dependências substituídas por *mocks* (Mockito) quando necessário.

| Classe | O que verifica | Requisitos | Testes | Aprov. | Falhas | Erros | Ign. |
| :--- | :--- | :--- | ---: | ---: | ---: | ---: | ---: |
| `ColaboradorTest` | Fórmulas das três categorias, limites, valores zero, arredondamento, validações de domínio | RF002–RF004, RN002–RN008 | 16 | 16 | 0 | 0 | 0 |
| `ColaboradorFormTest` (+ classe aninhada `ValidacaoBeanValidation`) | Bean Validation do formulário, conversão formulário ↔ domínio, campos em branco tratados como zero | RF001, RNF002, RN010 | 11 | 11 | 0 | 0 | 0 |
| `ConformidadeRequisitosTest` | Fórmulas de cálculo, bloqueio de dados inválidos, conversão e extensibilidade com o tipo `BONUS_FIXO` | RF002–RF004, RNF002, RNF004 | 4 | 4 | 0 | 0 | 0 |
| `FormatadorMoedaTest` | Formatação BRL, valores altos, negativos, nulos como R$ 0,00 | RN008, RN010 | 4 | 4 | 0 | 0 | 0 |
| `FolhaPagamentoServiceTest` | Regras do serviço com repositório *mock*: duplicidade, resumo por categoria, resumo vazio | RF008, RF011, RN001 | 7 | 7 | 0 | 0 | 0 |
| `FolhaPagamentoControllerTest` | Controller com serviço *mock*: rotas, redirecionamentos, mensagens e validação | RF001, RF005, RF007, RF009, RF010 | 17 | 17 | 0 | 0 | 0 |
| `CompetenciaEBordasTest.Dominio` | Formatador com nulos, validação numérica, casas decimais, tipo desconhecido, normalização de fuso (BUG-005) | RN003, RN008, RN010 | 5 | 5 | 0 | 0 | 0 |
| **Subtotal unitários** | | | **64** | **64** | **0** | **0** | **0** |

## 3. Testes de integração (seção 6.4 do enunciado)

Detalhados por categoria (persistência, fluxos de negócio, relatórios e integração entre componentes) em [Relatorio_Testes_Integracao.md](Relatorio_Testes_Integracao.md).

| Classe | Infraestrutura | Testes | Aprov. | Falhas | Erros | Ign. |
| :--- | :--- | ---: | ---: | ---: | ---: | ---: |
| `FolhaPagamentoWebCasosDeTesteTest` | Spring Boot + MockMvc + JPA + H2 | 21 | 21 | 0 | 0 | 0 |
| `CompetenciaEBordasTest.Web` | Spring Boot + MockMvc + JPA + H2 | 11 | 11 | 0 | 0 | 0 |
| `RepositorioColaboradoresTest` | Spring Boot + JPA + H2 | 4 | 4 | 0 | 0 | 0 |
| `RepositorioColaboradoresPostgresTest` | Spring Boot + JPA + Flyway + **PostgreSQL 17 real (Testcontainers)** | 3 | 3 | 0 | 0 | 0 |
| `FolhaPagamentoWebApplicationTest` | Contexto completo + Thymeleaf + JPA | 2 | 2 | 0 | 0 | 0 |
| **Subtotal integração** | | **41** | **41** | **0** | **0** | **0** |

**Total geral: 105 testes, 105 aprovados.**

## 4. Cobertura de código (JaCoCo 0.8.13)

Escopo: todas as classes de produção do pacote `folhapagamento` (15 classes), sem exclusões.

| Métrica | Cobertos / total | Resultado | Meta PGQ |
| :--- | ---: | ---: | :---: |
| Linhas | 384 / 396 | **96,97%** | ≥ 80% ✔ |
| Branches | 95 / 99 | **95,96%** | ≥ 80% ✔ |
| Instruções | 1676 / 1721 | **97,39%** | — |
| Métodos | 123 / 125 | **98,40%** | — |

### Por classe

| Classe | Linhas | Branches | Instruções |
| :--- | :---: | :---: | :---: |
| `Colaborador` | 27/27 | 19/20 | 113/113 |
| `ColaboradorPadrao` | 8/8 | — | 22/22 |
| `ColaboradorComissionado` | 21/21 | 2/2 | 76/76 |
| `ColaboradorProducao` | 21/21 | 2/2 | 73/73 |
| `ColaboradorForm` | 39/39 | 10/10 | 180/180 |
| `ColaboradorJpaEntity` (+ enum `TipoColaborador`) | 48/49 | 7/7 | 194/197 |
| `FolhaProcessada` | 7/7 | 4/4 | 56/56 |
| `FolhaProcessadaJpaEntity` | 19/19 | 2/2 | 85/85 |
| `FolhaPagamentoController` | 94/100 | 26/26 | 436/462 |
| `FolhaPagamentoService` | 36/36 | 4/4 | 169/169 |
| `FormatadorMoeda` | 5/5 | 4/4 | 28/28 |
| `RepositorioColaboradoresJpa` | 55/58 | 15/18 | 219/230 |
| `ResumoFolha` | 3/3 | — | 22/22 |
| `FolhaPagamentoWebApplication` | 1/3 | — | 3/8 |

As linhas não cobertas são: o método `main` (a aplicação é iniciada pelo contexto de teste, não por `main`), dois blocos `catch` do controller que só ocorrem se o domínio rejeitar um formulário já validado pelo Bean Validation, e o tratamento de `DataIntegrityViolationException` concorrente no repositório.

### Evolução em relação à execução anterior (commit `d8db957`)

| Indicador | Antes | Agora |
| :--- | :---: | :---: |
| Testes executados / ignorados | 84 / 3 | 105 / 0 |
| Linhas | 93,38% | 96,97% |
| Branches | 77,32% (abaixo da meta) | 95,96% |
| Limite automático de cobertura | não havia | `jacoco:check` ≥ 80% |

## 5. Reprodutibilidade

```powershell
docker compose up -d          # opcional para a aplicação; Testcontainers cria o próprio PostgreSQL
.\mvnw.cmd clean verify
start target\site\jacoco\index.html
```

Os testes PostgreSQL usam `@Testcontainers(disabledWithoutDocker = true)`: em uma máquina sem Docker eles aparecem como ignorados. Nesta execução o Docker estava disponível e os três foram executados e aprovados.
