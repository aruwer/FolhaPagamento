# Manual Técnico — Sistema de Folha de Pagamento

**Versão:** 2.0.0 · **Público:** desenvolvedores e responsáveis pela manutenção

---

## 1. Arquitetura

Aplicação web monolítica em **MVC com camadas**, renderizada no servidor (Spring MVC + Thymeleaf). Diagramas UML em [02_Analise/Modelagem_UML.md](../02_Analise/Modelagem_UML.md) e justificativas em [03_Projeto/Documento_Arquitetura.md](../03_Projeto/Documento_Arquitetura.md).

```text
Navegador ──HTTP──► FolhaPagamentoController (+ ColaboradorForm, FormatadorMoeda)   [apresentação]
                         │
                         ▼
                 FolhaPagamentoService (+ ResumoFolha)                             [aplicação]
                         │
             Colaborador ◄┘ (Padrao | Comissionado | Producao), FolhaProcessada    [domínio]
                         │
                         ▼
     RepositorioColaboradores ◄── RepositorioColaboradoresJpa                     [persistência]
                                   ├─ ColaboradorJpaRepository  → colaboradores
                                   └─ FolhaProcessadaJpaRepository → folhas_processadas
                                                     │
                                                     ▼
                                          PostgreSQL 17 (Flyway V1, V2)
```

**Decisões principais**

- **Polimorfismo para remuneração (RNF004):** `Colaborador.getSalarioFinal()` é `final` e soma `salarioBase + getAdicional()`; cada subclasse implementa só o seu adicional.
- **Snapshots por competência:** processar uma competência grava valores em `folhas_processadas`; relatórios nunca recalculam a partir do cadastro atual. Isso garante histórico íntegro após alteração ou exclusão (RN009).
- **Processamento idempotente:** `processarFolha` só cria snapshot para colaborador elegível sem snapshot no mês; a restrição `UNIQUE (colaborador_id, mes, ano)` reforça no banco.
- **Elegibilidade:** participa da competência quem foi cadastrado até o último dia do mês e não foi excluído antes do primeiro dia (fuso `America/Sao_Paulo`).
- **Contrato de repositório:** o serviço depende da interface `RepositorioColaboradores`, o que permite testá-lo com Mockito.

### Rotas HTTP

| Método | Rota | Ação |
| :--- | :--- | :--- |
| GET | `/` | Painel com colaboradores ativos e resumo |
| GET | `/colaboradores/novo` | Formulário de cadastro |
| POST | `/colaboradores` | Cadastrar |
| GET | `/colaboradores/{matricula}/editar` | Formulário de edição |
| POST | `/colaboradores/{matricula}` | Atualizar |
| POST | `/colaboradores/{matricula}/excluir` | Exclusão lógica |
| GET | `/folha?mes=&ano=` | Folha consolidada da competência (sem parâmetros: mês atual) |
| POST | `/folha/processar` (`mes`, `ano`) | Processar competência |
| GET | `/folha/{matricula}?mes=&ano=` | Demonstrativo individual |

---

## 2. Modelo de dados

Detalhado, com diagrama ER e regras do banco, em [06_Banco_Dados/README.md](../06_Banco_Dados/README.md).

- `colaboradores`: matrícula única, nome, `tipo_colaborador`, salário base e campos variáveis por tipo (restrição `ck_colaborador_campos_por_tipo`), `ativo`, `criado_em`, `atualizado_em`, `excluido_em`.
- `folhas_processadas`: snapshot (matrícula, nome, tipo, base, adicional, final, `data_processamento`) por colaborador/mês/ano, com FK para `colaboradores`.
- Valores monetários em `NUMERIC(12,2)` no banco e `BigDecimal` com escala 2 no Java; datas em `TIMESTAMP WITH TIME ZONE`. Ao ler, `FolhaProcessadaJpaEntity` normaliza a data para o fuso da folha (America/Sao_Paulo) (correção do BUG-005).

---

## 3. Estrutura do código

```text
pom.xml                         build Maven, dependências, JaCoCo (check ≥ 80%)
docker-compose.yml              PostgreSQL 17 + pgAdmin
.env.example                    modelo de variáveis locais
src/main/java/folhapagamento/   17 classes (ver tabela)
src/main/resources/
  application.properties        porta, datasource, JPA validate, Flyway
  db/migration/                 V1__schema.sql, V2__colaboradores_data_exclusao.sql
  templates/                    index, colaboradores/form, folha, folha-individual, fragments/nav
  static/css/app.css            estilos (inclui @media print)
  static/js/colaborador-form.js exibe campos conforme o tipo selecionado
src/test/java/folhapagamento/   11 classes, 105 testes (unitários e integração)
```

| Classe | Responsabilidade |
| :--- | :--- |
| `FolhaPagamentoWebApplication` | Ponto de entrada Spring Boot |
| `FolhaPagamentoController` | Rotas, validação de competência, mensagens *flash*, escolha de template |
| `ColaboradorForm` | DTO do formulário com Bean Validation; converte para/de domínio |
| `FormatadorMoeda` | Bean usado nos templates para formatar BRL (nulo → R$ 0,00) |
| `FolhaPagamentoService` | Orquestra cadastro, consulta, processamento e resumo |
| `ResumoFolha` | *Record* com quantidade, total e totais/quantidades por categoria |
| `Colaborador` e subclasses | Regras de cálculo e invariantes (não negativos, 2 casas decimais) |
| `FolhaProcessada` | *Record* de leitura do snapshot |
| `RepositorioColaboradores` / `RepositorioColaboradoresJpa` | Contrato e implementação transacional da persistência |
| `ColaboradorJpaEntity`, `FolhaProcessadaJpaEntity` | Mapeamento JPA e conversão para domínio |
| `ColaboradorJpaRepository`, `FolhaProcessadaJpaRepository` | Consultas Spring Data |

---

## 4. Dependências

Versões resolvidas pelo Maven (`spring-boot-starter-parent` 3.5.5):

| Dependência | Versão | Escopo | Uso |
| :--- | :--- | :--- | :--- |
| `spring-boot-starter-web` | 3.5.5 | compile | Spring MVC + Tomcat embutido |
| `spring-boot-starter-thymeleaf` | 3.5.5 | compile | Templates HTML |
| `spring-boot-starter-validation` | 3.5.5 | compile | Bean Validation (Hibernate Validator) |
| `spring-boot-starter-data-jpa` | 3.5.5 | compile | JPA / Hibernate |
| `flyway-core`, `flyway-database-postgresql` | 11.7.2 | compile | Migrations |
| `postgresql` (driver JDBC) | 42.7.7 | runtime | Conexão com PostgreSQL |
| `spring-boot-starter-test` | 3.5.5 | test | JUnit 5, Mockito, AssertJ, MockMvc |
| `h2` | 2.3.232 | test | Banco em memória para testes rápidos |
| `testcontainers` `junit-jupiter` e `postgresql` | 1.21.4 | test | PostgreSQL 17 real nos testes |
| `jacoco-maven-plugin` | 0.8.13 | build | Cobertura e verificação ≥ 80% |

Ferramentas externas: JDK 17+, Docker Desktop (Compose v2), imagens `postgres:17-alpine` e `dpage/pgadmin4:8`.

> **Testcontainers e Docker Engine 29+:** use Testcontainers **1.21.4 ou superior**. A versão 1.21.3 recebe HTTP 400 do Docker Desktop recente e os testes PostgreSQL acabam ignorados (BUG-002).

---

## 5. Build, testes e implantação

```powershell
.\mvnw.cmd clean verify        # compila, testa (H2 + Testcontainers), gera JaCoCo e aplica o limite de 80%
.\mvnw.cmd test                # só os testes
.\mvnw.cmd clean package       # gera target\folha-pagamento-2.0.0.jar
docker compose up -d           # PostgreSQL (5433) e pgAdmin (5050)
.\mvnw.cmd spring-boot:run     # aplicação em http://localhost:8081
```

Configuração por variáveis de ambiente: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`. Os testes H2 sobrescrevem o datasource via `@TestPropertySource` (Flyway desligado, `create-drop`); os testes PostgreSQL usam Flyway e `validate`, como em produção.

---

## 6. Manutenção

### 6.1. Alterar o esquema do banco
1. Crie `src/main/resources/db/migration/V3__descricao.sql` (nunca edite V1/V2, que já foram aplicadas).
2. Ajuste as entidades JPA correspondentes — com `ddl-auto=validate`, a aplicação não inicia se entidade e esquema divergirem.
3. Rode `.\mvnw.cmd clean verify`: `RepositorioColaboradoresPostgresTest` aplica todas as migrations em um PostgreSQL limpo.

### 6.2. Incluir um novo tipo de remuneração
Passo a passo verificado em CT-033 ([roteiro](../09_Homologacao/Roteiro_Testes_Manuais.md)): nova subclasse de `Colaborador` → constante em `ColaboradorJpaEntity.TipoColaborador` e `case` em `paraDominio()` → `case` em `ColaboradorForm` → migration V3 ampliando os `CHECK` → opção no `form.html` e rótulo em `FolhaProcessada.tipoDescricao()`. Nenhuma fórmula existente muda.

### 6.3. Qualidade antes de integrar (PGQ)
- Branch `feat|fix|docs|test/QDS_2.0.0/<descricao>` e commit semântico.
- `.\mvnw.cmd clean verify` sem falhas; cobertura ≥ 80% (o `verify` falha abaixo disso; `mvnw test` não aplica o limite).
- Pull Request com revisão aprovada no GitHub antes do *merge* (ver não conformidade NC-01 no [Relatório de Revisão Técnica](../10_Qualidade/Relatorio_Revisao_Tecnica.md)).
- Defeitos registrados em [Registro_Defeitos.md](../10_Qualidade/Registro_Defeitos.md) e nas Issues.

### 6.4. Backup e dados
- Backup: `docker compose exec -T postgres pg_dump -U folha_app -d folha_pagamento > backup.sql`.
- Não execute `docker compose down -v` em ambiente com dados reais (apaga o volume).
- Snapshots de `folhas_processadas` são registros históricos: não os altere manualmente.
- O arquivo legado `dados/colaboradores.tsv` (versão 1.x) não é importado; uma migração desses dados exigiria script próprio validado.

### 6.5. Problemas conhecidos de ambiente
| Sintoma | Causa | Correção |
| :--- | :--- | :--- |
| Testes PostgreSQL "skipped" | Docker parado ou Testcontainers < 1.21.4 com Docker Engine 29 | Iniciar o Docker; manter Testcontainers ≥ 1.21.4 |
| `Schema-validation: missing column` | Entidade alterada sem migration | Criar migration V*n* |
| `Port 8081 was already in use` | Outra instância em execução | Encerrar a instância ou usar `--server.port` |
