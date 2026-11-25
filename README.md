# Sistema de Folha de Pagamento — Grupo 11

**Versão:** `2.0.0` · **Unidade Curricular:** Garantia da Qualidade de Software · **Entrega:** A3 (01/12/2026)

**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta

Aplicação web Java (Spring Boot + PostgreSQL) para cadastrar colaboradores, calcular remunerações (padrão, comissionada e por produção), processar a folha por competência (mês/ano) e emitir relatórios individuais e consolidados.

**Repositório:** <https://github.com/aruwer/FolhaPagamentoA3> — histórico de commits, Pull Requests e Issues (KAN-01 a KAN-16).

## Situação da entrega A3

| Indicador | Resultado | Meta (PGQ) |
| :--- | :--- | :--- |
| Build `.\mvnw.cmd clean verify` | **BUILD SUCCESS** | sucesso |
| Testes automatizados (unitários + integração) | **106 executados, 106 aprovados, 0 falhas, 0 ignorados** | 100% |
| Cobertura de linhas / branches (JaCoCo) | **96,96% / 95,88%** | ≥ 80% (verificado automaticamente no build) |
| Casos de teste especificados executados e aprovados | **41 / 41** | 100% |
| Requisitos funcionais atendidos | **11 / 11 (100%)** | 100% |
| Defeitos S1/S2 abertos | **0** | 0 |

Detalhes em [10_Qualidade/Relatorio_Final_Qualidade.md](10_Qualidade/Relatorio_Final_Qualidade.md).

## Estrutura de entrega (seção 7 do enunciado)

| Pasta | Conteúdo |
| :--- | :--- |
| [01_Requisitos](01_Requisitos/) | Documento de Visão e ERS (RF, RN, RNF, critérios de aceitação, matriz de rastreabilidade), matriz de conformidade e baseline A1 em PDF |
| [02_Analise](02_Analise/) | Modelagem UML: casos de uso, classes e sequência |
| [03_Projeto](03_Projeto/) | Documento de Arquitetura; EAP, cronograma e responsabilidades |
| [04_Testes](04_Testes/) | Plano de testes, 41 casos de teste rastreáveis, relatórios de execução (unitários e integração), log e relatórios Surefire |
| [05_Codigo_Fonte](05_Codigo_Fonte/) | Guia do código-fonte (o projeto Maven fica na raiz: `pom.xml`, `src/`) |
| [06_Banco_Dados](06_Banco_Dados/) | Modelo de dados, migrations Flyway e script de carga de demonstração |
| [07_Executavel](07_Executavel/) | Como gerar e executar o JAR |
| [08_Manuais](08_Manuais/) | Manual do Usuário e Manual Técnico |
| [09_Homologacao](09_Homologacao/) | Critérios e testes de aceitação, roteiro manual, evidências e Termo de Homologação |
| [10_Qualidade](10_Qualidade/) | PGQ, registro de defeitos, métricas, revisão técnica, relatório final e relatório JaCoCo |
| [11_Apresentacao](11_Apresentacao/) | Slides (`.pptx`) e roteiro da apresentação |

> O projeto Maven permanece na raiz do repositório (`pom.xml`, `src/`, `mvnw.cmd`, `docker-compose.yml`) para que os comandos de build funcionem sem configuração adicional. As pastas `05_Codigo_Fonte`, `06_Banco_Dados` e `07_Executavel` explicam onde está cada item.

## Requisitos de ambiente

- JDK 17 ou superior
- Docker Desktop (PostgreSQL 17 e pgAdmin via Docker Compose; também usado pelos testes Testcontainers)
- Windows PowerShell (o Maven Wrapper `mvnw.cmd` está incluído; em Linux/macOS use `./mvnw`)

## Instalação e execução

```powershell
# 1. Variáveis locais (o .env não é versionado)
if (-not (Test-Path .env)) { Copy-Item .env.example .env }

# 2. Mesma senha do banco para a aplicação
$passwordLine = Get-Content .env | Where-Object { $_ -match '^POSTGRES_PASSWORD=' } | Select-Object -First 1
$env:DATABASE_PASSWORD = $passwordLine.Substring("POSTGRES_PASSWORD=".Length)

# 3. Banco de dados e aplicação
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Acesse <http://localhost:8081>. O PostgreSQL fica em `localhost:5433` e o pgAdmin em <http://localhost:5050>. O Flyway aplica as migrations e o Hibernate valida o esquema na inicialização. Passo a passo completo no [Manual do Usuário](08_Manuais/Manual_Usuario.md).

Os valores de `.env.example` servem apenas para desenvolvimento local. Se o volume do PostgreSQL já foi inicializado, alterar a senha no `.env` não altera a senha gravada no banco.

## Testes

```powershell
.\mvnw.cmd clean verify
```

Executa os testes unitários (JUnit 5, Mockito), de integração com H2 e MockMvc, e de integração com PostgreSQL 17 real via Testcontainers (requer Docker em execução). O JaCoCo gera o relatório em `target/site/jacoco/index.html` e **o build falha se a cobertura de linhas ou de branches ficar abaixo de 80%** (regra aplicada na fase `verify`; `mvnw test` e `mvnw package -DskipTests` não a executam).

## Testes de backend e de frontend (Bruno e Cypress)

Além do JUnit, a pasta [tests-e2e/](tests-e2e/README.md) tem uma coleção Bruno CLI (33 requisições, 112 testes) e specs Cypress (11 testes) que exercitam a aplicação real e salvam evidências em [09_Homologacao/evidencias-auto/](09_Homologacao/evidencias-auto/). Execução: `cd tests-e2e; npm install; .\scripts\run-all.ps1`.

## Funcionalidades

- Cadastro, consulta, alteração e exclusão lógica de colaboradores, com matrícula única.
- Remuneração padrão, comissionada (vendas × percentual) e por produção (quantidade × valor unitário).
- Processamento idempotente da folha por competência, com snapshots históricos preservados após alteração ou exclusão do cadastro.
- Folha consolidada com total geral e totais por categoria; demonstrativo individual com adicionais zerados explícitos (R$ 0,00).
- Impressão/geração de PDF da folha e do demonstrativo pelo navegador.
