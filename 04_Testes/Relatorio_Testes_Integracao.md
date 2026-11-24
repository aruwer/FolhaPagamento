# Relatório de Testes de Integração

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Execução:** 23/11/2026 ([log](log-verify.txt))  
**Resultado:** 41 testes de integração executados, **41 aprovados**, 0 falhas, 0 erros, 0 ignorados.

## 1. Estratégia

| Camada integrada | Infraestrutura usada | Por quê |
| :--- | :--- | :--- |
| HTTP → Controller → Service → Repositório JPA → banco | `@SpringBootTest` + `MockMvc` + H2 (modo PostgreSQL), transação revertida a cada teste | Exercita as rotas reais com formulário `application/x-www-form-urlencoded`, sem mocks, de forma rápida e isolada |
| Repositório JPA → banco | `@SpringBootTest` + H2 | Verifica mapeamento JPA, restrições e consultas |
| Repositório JPA → **PostgreSQL 17 real** com migrations Flyway | Testcontainers 1.21.4 (`postgres:17-alpine`) | Garante que o esquema versionado (V1, V2) e os tipos `NUMERIC`/`TIMESTAMPTZ` se comportam como em produção |
| Contexto completo + Thymeleaf | `@SpringBootTest` + `MockMvc` | Confirma que a aplicação inicializa e renderiza as telas |

## 2. Persistência

| Teste | Cenário | Resultado esperado | Situação |
| :--- | :--- | :--- | :---: |
| `RepositorioColaboradoresPostgresTest.persisteCadastroERejeitaMatriculaDuplicada` | Cadastrar comissionado no PostgreSQL real e repetir a matrícula | Salário final 2.050,00 recuperado do banco; segunda inclusão rejeitada (RN001) | Aprovado |
| `RepositorioColaboradoresPostgresTest.congelaFolhaPorCompetenciaEPreservaHistoricoNoSoftDelete` | Processar 02/2099, alterar salário, processar 03/2099, excluir | Snapshot de 02 inalterado e idêntico ao gravado; 03 com o novo salário; histórico disponível após exclusão lógica (RN009) | Aprovado |
| `RepositorioColaboradoresPostgresTest.validaCompetenciaEPrecisaoMonetaria` | Comissão de meio centavo; competência 13/2099 | Adicional 0,01 (HALF_UP); competência inválida rejeitada | Aprovado |
| `RepositorioColaboradoresTest.persisteTiposDeColaboradorERejeitaDuplicidade` | Persistir os três tipos e repetir matrícula | Três tipos recuperados corretamente; duplicidade rejeitada | Aprovado |
| `RepositorioColaboradoresTest.atualizaCadastroEExcluiColaboradorAtivo` | Atualizar e excluir | Alteração persistida; excluído some da lista ativa | Aprovado |
| `RepositorioColaboradoresTest.preservaSnapshotDaFolhaDepoisDeAtualizarEExcluir` | Snapshot após alteração e exclusão | Valores históricos preservados | Aprovado |
| `RepositorioColaboradoresTest.processamentoELeituraSaoIdempotentesERejeitamCompetenciaInvalida` | Processar a mesma competência duas vezes | Nenhum snapshot duplicado; competência inválida rejeitada | Aprovado |
| `FolhaPagamentoWebApplicationTest.iniciaRepositorioJPA` | Inicializar o contexto completo | Contexto sobe com JPA e injeta `RepositorioColaboradores` | Aprovado |

**Defeito encontrado:** a primeira execução real em PostgreSQL revelou o **BUG-005** — o `data_processamento` voltava do banco em UTC e o snapshot lido deixava de ser igual ao gravado. Corrigido em `FolhaProcessadaJpaEntity.noFusoDoSistema` e coberto por `CompetenciaEBordasTest.Dominio.normalizaDataDeProcessamento`. Esse defeito não aparecia com H2, o que justifica a execução em PostgreSQL real.

## 3. Fluxos de negócio (HTTP ponta a ponta)

| Teste | Fluxo | Situação |
| :--- | :--- | :---: |
| `ct001CalculaSalarioPadrao` | Cadastrar padrão → processar → demonstrativo com adicional 0,00 e final 2.500,00 | Aprovado |
| `ct003CalculaComissaoOuZero` | Comissionados com vendas, sem vendas e sem percentual → 2.500 / 2.000 / 2.000 | Aprovado |
| `ct006CalculaProducaoOuZero` | Produção com quantidade, sem quantidade e sem valor → 2.100 / 1.800 / 1.800 | Aprovado |
| `ct009RejeitaMatriculaDuplicada` | Segundo cadastro com a mesma matrícula volta ao formulário com erro e não sobrescreve | Aprovado |
| `ct010RejeitaMatriculaComPontuacao`, `ct011E012*`, `ct013*`, `ct014E018*` | Validações de formulário (RNF002, RN002–RN007) sem persistir dados inválidos | Aprovado |
| `ct015E016AceitaCamposComissionadosEmBranco`, `ct019E020AceitaCamposProducaoEmBranco` | Campos variáveis em branco tratados como zero (RN010) | Aprovado |
| `ct023ListaTresCategorias`, `ct024AtualizaColaborador`, `ct025ExcluiColaboradorAtivo` | Listar, alterar e excluir logicamente (RF005, RF009) | Aprovado |
| `ct036FiltraPorCompetencia` | Folha processada em 03 não aparece em 04 nem em 03 de outro ano | Aprovado |
| `ct037PreservaSnapshotDepoisDaExclusao` | Demonstrativo continua disponível após exclusão | Aprovado |
| `CompetenciaEBordasTest.Web` (11 testes) | Competência inválida (mês/ano ausentes, ano < 2000 ou > 9999, mês 0 ou 13) na consulta, no processamento e no demonstrativo; demonstrativo de folha ainda não processada; mensagem orientando o processamento (BUG-006); competência corrente por padrão | Aprovado |

## 4. Relatórios

| Teste | Relatório | Verificação | Situação |
| :--- | :--- | :--- | :---: |
| `ct026CalculaResumoPorCategoria` | Folha consolidada | Quantidade 3, total 7.100,00, totais por categoria 2.500 / 2.500 / 2.100 | Aprovado |
| `ct039ResumePorCategoriaECompetencia` | Resumo por competência | Mesmos totais apenas para 03/2099, ignorando 04/2099 (16.100,00) e 03/2098 | Aprovado |
| `ct031ResumoVazio` | Resumo sem colaboradores | Quantidade 0, total 0,00, sem categorias | Aprovado |
| `ct027ExibeFolhaIndividualComissionada` | Demonstrativo individual | Base, comissão e salário final do snapshot | Aprovado |
| `ct038ExibeAdicionaisZerados`, `ct038BrancoAceitaVendasEmBranco` | Demonstrativo individual | Adicional exibido como zero quando não há vendas/produção (RF010, RN010) | Aprovado |
| `FolhaPagamentoWebApplicationTest.renderizaTelasPrincipais` | Painel, formulário e folha | Templates Thymeleaf renderizados (HTTP 200) com o contexto completo | Aprovado |

## 5. Integração entre componentes

```text
Navegador/MockMvc ──HTTP──► FolhaPagamentoController ──► ColaboradorForm (Bean Validation)
                                     │                          │
                                     ▼                          ▼
                           FolhaPagamentoService ◄──── Colaborador (domínio polimórfico)
                                     │
                                     ▼
                      RepositorioColaboradoresJpa ──► Spring Data JPA ──► H2 / PostgreSQL 17 (Flyway)
                                     │
                                     ▼
                         FolhaProcessadaJpaEntity (snapshots) ──► folha.html / folha-individual.html (FormatadorMoeda)
```

Todos os testes da seção 3 percorrem essa cadeia inteira sem *mocks*: a requisição HTTP passa pelo bind/validação do formulário, pela construção do objeto de domínio, pelo serviço, pela persistência JPA e volta como modelo para o template. Os testes com `@Mock` (controller e serviço isolados) estão classificados como unitários no [relatório de execução](Relatorio_Execucao_Testes.md).

## 6. Complemento manual em ambiente real

A integração com o PostgreSQL do Docker Compose, incluindo o **reinício da aplicação**, foi verificada manualmente em CT-030 (cenário T130 e CT-030-D). Evidências em [09_Homologacao/evidencias/CT-030](../09_Homologacao/evidencias/CT-030/).
