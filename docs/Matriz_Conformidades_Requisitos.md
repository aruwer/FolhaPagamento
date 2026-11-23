# Matriz de Conformidade de Requisitos

**Base:** `docs/ERS.md` (RF001–RF011, RN001–RN010, RNF001–RNF004).
**Versão do software:** 2.0.0 (`pom.xml`).

## Como ler o status

- **Implementado:** a funcionalidade existe no código e há classe/template que a sustenta.
- **Implementado com ressalva:** existe, mas depende de decisão ou de uma verificação que ainda não foi feita.
- **Não implementado:** ausente no código.

Nenhum requisito está marcado como "verificado". A verificação exige execução registrada dos testes e dos casos de teste, o que ainda não aconteceu (ver `Relatorio_Execucao_Testes_Unitarios.md`).

## Requisitos funcionais

| Requisito | Descrição resumida | Onde está implementado | Testes existentes | Status |
| :--- | :--- | :--- | :--- | :--- |
| RF001 | Cadastro com matrícula, nome, salário base e tipo | `ColaboradorForm`, `FolhaPagamentoController.cadastrar`, `colaboradores/form.html` | `ColaboradorFormTest`, `FolhaPagamentoControllerTest` | Implementado |
| RF002 | Cadastro de colaborador padrão | `ColaboradorPadrao` | `ColaboradorTest` | Implementado |
| RF003 | Cadastro de comissionado: vendas e percentual | `ColaboradorComissionado` | `ColaboradorTest` | Implementado |
| RF004 | Cadastro de produção: quantidade e valor unitário | `ColaboradorProducao` | `ColaboradorTest` | Implementado |
| RF005 | Lista de colaboradores ativos com dados cadastrais e remuneratórios | `index.html` (coluna de detalhes) | `FolhaPagamentoControllerTest` | Implementado |
| RF006 | Cálculo do salário final por competência | `FolhaPagamentoService.processarFolha`, `RepositorioColaboradoresJpa.processarFolha` | `FolhaPagamentoServiceTest`, `RepositorioColaboradoresPostgresTest` | Implementado |
| RF007 | Folha por mês/ano com matrícula, nome, tipo e salário final | `folha.html`, `FolhaPagamentoController.folha` | `FolhaPagamentoControllerTest` | Implementado |
| RF008 | Total da folha do período | `ResumoFolha.totalFolha`, `FolhaPagamentoService.gerarResumo` | `FolhaPagamentoServiceTest` | Implementado |
| RF009 | Inclusão, alteração, exclusão lógica e consulta | `FolhaPagamentoController`, `RepositorioColaboradoresJpa.excluir` (`excluido_em`) | `RepositorioColaboradoresTest` | Implementado |
| RF010 | Demonstrativo individual com adicionais, zerados quando ausentes | `folha-individual.html`, `Colaborador.valorOuZero` | `ColaboradorTest` | Implementado |
| RF011 | Resumo com quantidade, total e subtotal por categoria | `ResumoFolha.quantidadePorCategoria`, `index.html`, `folha.html` | `FolhaPagamentoServiceTest` | Implementado |

## Requisitos não funcionais

| Requisito | Descrição resumida | Onde está implementado | Testes existentes | Status |
| :--- | :--- | :--- | :--- | :--- |
| RNF001 | Interface amigável e intuitiva | Templates Thymeleaf e `app.css` | Nenhum automatizado; caso manual CT-032 | Implementado com ressalva (usabilidade depende do roteiro manual) |
| RNF002 | Validação antes de qualquer cálculo | Bean Validation em `ColaboradorForm`, `BindingResult` e erros de domínio no formulário | `ColaboradorFormTest` | Implementado |
| RNF003 | Persistência em banco SQL | `RepositorioColaboradoresJpa`, migrações V1 e V2 (Flyway), `docker-compose.yml` | `RepositorioColaboradoresPostgresTest` (Testcontainers; ignorado sem Docker) | Implementado com ressalva (execução em PostgreSQL pendente) |
| RNF004 | Novos tipos sem alterar cálculos existentes | Subclasses de `Colaborador` com `getAdicional()` | `ColaboradorTest` | Implementado com ressalva (novo tipo ainda exige um `case` no mapeamento de persistência e no formulário) |

## Regras de negócio

| Regra | Descrição resumida | Onde está implementado | Testes existentes | Status |
| :--- | :--- | :--- | :--- | :--- |
| RN001 | Matrícula única | Validação em `FolhaPagamentoService.cadastrar` e restrição `UNIQUE` em `V1__schema.sql` | `RepositorioColaboradoresTest` | Implementado |
| RN002 | Nome obrigatório | `@NotBlank` em `ColaboradorForm` e validação em `Colaborador` | `ColaboradorFormTest` | Implementado |
| RN003 | Salário base não negativo | `@DecimalMin` e `Colaborador.validarNaoNegativo` | `ColaboradorFormTest`, `ColaboradorTest` | Implementado |
| RN004 | Valor de vendas não negativo | `ColaboradorComissionado` | `ColaboradorTest` | Implementado |
| RN005 | Percentual de comissão não negativo | `ColaboradorComissionado` | `ColaboradorTest` | Implementado |
| RN006 | Quantidade produzida não negativa | `ColaboradorProducao` | `ColaboradorTest` | Implementado |
| RN007 | Valor por unidade não negativo | `ColaboradorProducao` | `ColaboradorTest` | Implementado |
| RN008 | Salário final calculado pelo sistema | `Colaborador.getSalarioFinal()` | `ColaboradorTest` | Implementado |
| RN009 | Exclusão lógica preserva histórico | `ColaboradorJpaEntity.excluirLogicamente`, coluna `excluido_em` (V2), snapshots em `folhas_processadas` | `RepositorioColaboradoresTest` | Implementado |
| RN010 | Valores variáveis ausentes exibidos como R$ 0,00 | `Colaborador.valorOuZero`, `FormatadorMoeda`, `folha-individual.html` | `ColaboradorFormTest`, `FormatadorMoedaTest` | Implementado |

## Pontos fora da ERS

- O percentual de comissão é limitado a 100% por `@DecimalMax("100.0")` em `ColaboradorForm` e pela validação equivalente no domínio. A ERS exige apenas que o percentual não seja negativo; o limite máximo adicional não é descrito nela e deve ser mantido somente se a equipe considerar o requisito aprovado.
- Os status desta matriz representam a implementação atual do código, não a aprovação de casos manuais ainda pendentes.

## Limitações atuais

- Nenhum caso de teste (CT-001 a CT-040) está associado a uma execução registrada.
- Os três testes `RepositorioColaboradoresPostgresTest` permanecem ignorados sem Docker/Testcontainers disponível.
