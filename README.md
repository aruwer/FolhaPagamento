# Sistema de Folha de Pagamento - Grupo 11

**Versão Atual:** `1.1.0` 

**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta

Aplicação web Java para cadastrar colaboradores, calcular remunerações e emitir relatórios individuais e consolidados.

## 🔗 Repositório e Rastreabilidade

- **URL do Repositório (GitHub):** `https://github.com/aruwer/folhapagamento`
- Todo o histórico de commits, revisões de código via Pull Requests e gestão de tarefas (Quadro KANBAN/Issues) pode ser verificado integralmente no link acima, garantindo a rastreabilidade da EAP e do cronograma.

## 📂 Índice de Equivalência Documental

Para manter a integridade dos links internos do repositório, os artefatos foram mantidos na pasta `docs/`. Abaixo encontra-se o índice inequívoco mapeando nossos arquivos para a estrutura de pastas documentais exigida (01 a 11):

- **01 - Visão do Sistema e ERS:** `docs/VisaoDeSistema+ERSRastreavel.pdf`
- **02 - Matriz de Rastreabilidade:** `docs/VisaoDeSistema+ERSRastreavel.pdf` (pág. 4) e detalhamento na Seção 5 de `docs/CASOS_TESTE_RASTREAVEIS_A1.md`
- **03 - Modelagem UML e Arquitetura:** `docs/Arquitetura.md`
- **04 - Plano de Garantia da Qualidade (PGQ):** `docs/PGQ.md`
- **05 - Plano de Testes:** `docs/PLANO_ESTRATEGIA_TESTES_A1.md`
- **06 - Casos de Teste (Especificação):** `docs/CASOS_TESTE_RASTREAVEIS_A1.md`
- **07 - EAP, Cronograma e Responsabilidades:** `docs/Consolidar EAP, cronograma e responsabilidades.pdf`
- **08 a 11 - (Artefatos da Fase A3):** Evidências de execução de testes, manuais e termo de homologação serão adicionados na entrega final.

## Requisitos

- JDK 17 ou superior
- PowerShell no Windows para usar o Maven Wrapper incluído

## Executar

O PostgreSQL é a persistência padrão. Prepare as variáveis locais e inicie o banco:

```powershell
Copy-Item .env.example .env
$env:DATABASE_PASSWORD = "replace_with_local_password"
docker compose up -d
docker compose ps
.\mvnw.cmd spring-boot:run
```

O Docker Compose publica o banco em `localhost:5433` e o pgAdmin em `http://localhost:5050`. O Flyway aplica as migrações e o Hibernate valida o esquema ao iniciar a aplicação. Acesse [http://localhost:8081](http://localhost:8081).

Use a mesma senha local em `POSTGRES_PASSWORD` no `.env` e em `DATABASE_PASSWORD` no PowerShell. Os valores do `.env.example` são apenas exemplos locais; não use essas senhas em ambientes compartilhados ou de produção.

Para gerar um JAR executável:

```powershell
.\mvnw.cmd clean package
java -jar target\folha-pagamento-1.1.0.jar
```

## Testes

Os testes usam JUnit 5, Mockito e H2. Para executar os testes unitários e de integração:

```powershell
.\mvnw.cmd test
```

Os testes de integração PostgreSQL usam Testcontainers e são executados quando o Docker está disponível; sem Docker, eles são ignorados.

Para executar os testes e gerar o relatório de cobertura JaCoCo:

```powershell
.\mvnw.cmd verify
```

O relatório HTML é gerado em `target/site/jacoco/index.html`.

## Banco de dados

O esquema é versionado em `src/main/resources/db/migration` e aplicado pelo Flyway. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` para usar outra instância. A porta local padrão do Compose é `5433`.

Para parar os serviços sem apagar o volume de dados:

```powershell
docker compose down
```

O arquivo legado `dados/colaboradores.tsv` não é importado automaticamente para o PostgreSQL.

## Funcionalidades

- Cadastro, consulta, alteração e exclusão de colaboradores.
- Remuneração padrão, comissionada e por produção, calculada por tipo.
- Folha individual e consolidada, com opção de impressão pelo navegador.
- Processamento idempotente da folha por competência (mês/ano), com snapshots históricos.
- Resumo com quantidade de colaboradores, custo total e valores por categoria.
- Exclusão lógica que mantém colaboradores e folhas processadas no banco.
- Validação de dados e matrícula única.
- Páginas Thymeleaf e backend Spring Boot no mesmo repositório.
