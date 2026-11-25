# 07 — Executável

O executável é um JAR Spring Boot autocontido (Tomcat embutido): `folha-pagamento-2.0.0.jar` (56 MB).

| Arquivo | Para quê |
| :--- | :--- |
| `folha-pagamento-2.0.0.jar` | O executável. **Não é versionado no git** (56 MB): está anexo à [release QDS_2.0.0](https://github.com/aruwer/FolhaPagamento/releases) e na cópia de entrega deste projeto |
| [`SHA256.txt`](SHA256.txt) | Soma de verificação do JAR (`Get-FileHash .\folha-pagamento-2.0.0.jar -Algorithm SHA256`) |
| [`executar.ps1`](executar.ps1) | Inicia o PostgreSQL (Docker Compose) e executa o JAR |
| [`baixar-jar.ps1`](baixar-jar.ps1) | Baixa o JAR da release e confere o SHA-256 |

## Executar (forma mais simples)

Pré-requisitos: **Java 17+** e **Docker Desktop em execução**.

```powershell
cd 07_Executavel
.\baixar-jar.ps1      # só se o JAR não estiver nesta pasta
.\executar.ps1        # sobe o banco e a aplicação em http://localhost:8081
```

O script procura o JAR nesta pasta e depois em `target\`, cria o `.env` a partir de `.env.example` se não existir, lê a senha do banco dele e aguarda o PostgreSQL ficar saudável. Use `.\executar.ps1 -Porta 8082` para outra porta. Para encerrar, `Ctrl+C`; o banco continua (`docker compose down` na raiz o para, sem apagar os dados).

## Executar manualmente

```powershell
docker compose up -d                       # na raiz do projeto
$env:DATABASE_PASSWORD = "<mesma senha de POSTGRES_PASSWORD no .env>"
java -jar 07_Executavel\folha-pagamento-2.0.0.jar
```

### Configuração

| Variável / argumento | Padrão | Uso |
| :--- | :--- | :--- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5433/folha_pagamento` | Outro servidor ou banco (se definida, o `executar.ps1` não inicia o Docker) |
| `DATABASE_USERNAME` | `folha_app` | Usuário do banco |
| `DATABASE_PASSWORD` | *(vazio)* | Senha do banco (obrigatória) |
| `--server.port=XXXX` | `8081` | Outra porta HTTP |

## Gerar o JAR a partir do código

```powershell
.\mvnw.cmd clean package          # executa os testes e gera target\folha-pagamento-2.0.0.jar
.\mvnw.cmd clean package -DskipTests
```

O build com `verify` falha se a cobertura ficar abaixo de 80%. O hash de um JAR gerado de novo pode diferir do de `SHA256.txt` (carimbo de data no empacotamento); o do arquivo da release é o de referência.

## Verificação do artefato

Em 24/11/2026, `executar.ps1` iniciou o JAR desta pasta em cerca de 7 s, o Flyway validou as migrations V1 e V2 e a aplicação respondeu HTTP 200 (verificação feita com o JAR da release original). O JAR atual desta pasta foi recompilado em 25/11/2026 com a migração V3 (BUG-007) e o hash em `SHA256.txt` é o dele. Em 25/11/2026, `executar.ps1` o iniciou em um banco isolado (`folha_reteste`): o Flyway validou 3 migrations (versão 3) e a aplicação respondeu HTTP 200. Ver também [CT-030-D](../09_Homologacao/evidencias/CT-030/CT-030-D-A3-persistencia-apos-reinicio.txt).
