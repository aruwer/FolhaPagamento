# 07 — Executável

O executável é um JAR Spring Boot autocontido (Tomcat embutido): `folha-pagamento-2.0.0.jar` (~56 MB). Ele **não é versionado no Git** (a pasta `target/` é ignorada e o arquivo é grande); é gerado a partir do código e publicado como anexo da *release* `QDS_2.0.0` no GitHub.

## Gerar

```powershell
.\mvnw.cmd clean package          # executa os testes e gera target\folha-pagamento-2.0.0.jar
# ou, sem testes (apenas para gerar rapidamente o artefato já validado):
.\mvnw.cmd clean package -DskipTests
```

## Executar

Pré-requisitos: JDK 17+ e o PostgreSQL do Docker Compose em execução (`docker compose up -d`).

```powershell
$env:DATABASE_PASSWORD = "<mesma senha de POSTGRES_PASSWORD no .env>"
java -jar target\folha-pagamento-2.0.0.jar
```

Abra <http://localhost:8081>.

### Parâmetros de configuração

| Variável / argumento | Padrão | Uso |
| :--- | :--- | :--- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5433/folha_pagamento` | Outro servidor ou banco |
| `DATABASE_USERNAME` | `folha_app` | Usuário do banco |
| `DATABASE_PASSWORD` | *(vazio)* | Senha do banco (obrigatória) |
| `--server.port=XXXX` | `8081` | Outra porta HTTP |

Exemplo usado na geração das evidências de homologação (banco dedicado, porta separada):

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5433/folha_evidencias"
java -jar target\folha-pagamento-2.0.0.jar --server.port=8090
```

## Verificação do artefato

Na geração de 23/11/2026, o JAR iniciou em ~5 s, o Flyway aplicou/validou as migrations V1 e V2 e todas as telas responderam HTTP 200 (ver [CT-030-D](../09_Homologacao/evidencias/CT-030/CT-030-D-A3-persistencia-apos-reinicio.txt)).
