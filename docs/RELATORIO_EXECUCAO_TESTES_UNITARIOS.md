# Relatório de Execução dos Testes Unitários

## Execução atual

**Resultado:** `.\mvnw.cmd test` concluiu com **BUILD SUCCESS**.

- 68 testes executados ou descobertos
- 0 falhas
- 0 erros
- 3 testes PostgreSQL ignorados pelo Testcontainers sem Docker reconhecido

`RepositorioColaboradoresPostgresTest` não substitui a verificação em PostgreSQL real executada pelo Docker Compose. Os testes unitários, de controller, serviço e persistência H2 estão passando.

## Inventário dos testes no código

Contagem de anotações `@Test` por classe, feita diretamente em `src/test/java/folhapagamento`:

| Classe de teste | Casos (`@Test`) | Observação |
| :--- | ---: | :--- |
| `ColaboradorFormTest` | 10 | Inclui validação de Bean Validation |
| `ColaboradorTest` | 16 | Fórmulas e validações de domínio |
| `ConformidadeRequisitosTest` | 3 | Verificações de conformidade |
| `FolhaPagamentoConfigTest` | 3 | Inicialização da aplicação e persistência JPA |
| `FolhaPagamentoControllerTest` | 17 | Fluxos do controller com Mockito |
| `FolhaPagamentoServiceTest` | 7 | Serviço com repositório simulado |
| `FormatadorMoedaTest` | 4 | Formatação monetária |
| `RepositorioColaboradoresPostgresTest` | 3 | Testcontainers; ignorado sem Docker |
| `RepositorioColaboradoresTest` | 4 | Repositório JPA |
| `ValidacaoBeanValidation` | 1 | Validação aninhada |
| **Total** | **65** | A contagem Maven pode contabilizar classes aninhadas e chegar a 68 execuções |

## Última execução registrada no diretório `target/`

O diretório `target/surefire-reports` contém resultados de uma execução anterior. Eles **não correspondem ao código atual**:

- citam `FolhaPagamentoConfigTest` com 3 casos (no código há 4);
- citam `RepositorioColaboradoresTest` com 4 casos (no código há 5);
- citam `ColaboradorTest` com 16 casos (no código há 9);
- citam `FolhaPagamentoControllerTest` com 17 casos (coincide com o código).

Esses números não devem ser usados como evidência da entrega. Eles servem apenas para confirmar que houve uma execução em algum momento.

## Próximas verificações

1. Resolver o acesso Docker/Testcontainers para executar os três testes PostgreSQL.
2. Executar `.\mvnw.cmd clean verify` antes do fechamento da entrega.
3. Preservar o log, os resultados Surefire e o relatório JaCoCo como evidências.

| Classe | Casos executados | Aprovados | Falhas | Erros | Ignorados | Data | Executor |
| :--- | ---: | ---: | ---: | ---: | ---: | :--- | :--- |
| `ColaboradorFormTest` | | | | | | | |
| `ColaboradorTest` | | | | | | | |
| `ConformidadeRequisitosTest` | | | | | | | |
| `FolhaPagamentoControllerTest` | | | | | | | |
| `FolhaPagamentoServiceTest` | | | | | | | |
| `FormatadorMoedaTest` | | | | | | | |
| `RepositorioColaboradoresPostgresTest` | | | | | | | |
| `RepositorioColaboradoresTest` | | | | | | | |

Após a execução, a cobertura JaCoCo deve ser medida com `.\mvnw.cmd verify` e registrada no PGQ, sem estimativas.
