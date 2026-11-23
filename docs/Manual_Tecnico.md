# Manual Técnico

## Arquitetura

A aplicação segue MVC em camadas: templates Thymeleaf e `FolhaPagamentoController` na apresentação; `FolhaPagamentoService` e o domínio na aplicação; `RepositorioColaboradores` como contrato e `RepositorioColaboradoresJpa` como adaptador de persistência Spring Data JPA. A aplicação inicia em `FolhaPagamentoWebApplication`.

## Modelo de dados

As migrations `V1__schema.sql` e `V2__colaboradores_data_exclusao.sql` estão em `src/main/resources/db/migration`.

- `colaboradores`: matrícula única, nome, categoria, salário base, valores específicos da categoria, estado ativo e timestamps; V2 adiciona `excluido_em` para exclusão lógica.
- `folhas_processadas`: snapshot de matrícula, nome, categoria, competência, base, adicional e salário final. A chave única por colaborador/mês/ano impede duplicação do snapshot.
- Valores monetários são persistidos em tipos `NUMERIC`; migrations Flyway controlam a evolução do esquema.

## Estrutura principal

- `src/main/java/folhapagamento/`: domínio, formulário, controller, serviço, repositórios e entidades JPA.
- `src/main/resources/templates/`: páginas Thymeleaf; `static/` contém CSS e JavaScript.
- `src/main/resources/db/migration/`: migrations SQL.
- `src/test/java/folhapagamento/`: testes de domínio, controller, serviço, persistência H2, teste HTTP integrado e Testcontainers.
- `scripts/seed-colaboradores.sql`: dados fictícios opcionais.

## Dependências

O `pom.xml` define Spring Boot 3.5.5 e Java 17, Spring Web, Thymeleaf, Validation, Data JPA, Flyway Core e Flyway PostgreSQL. PostgreSQL é dependência runtime. Para testes usa Spring Boot Test, JUnit 5, Mockito, H2 e Testcontainers JUnit/PostgreSQL 1.21.3. JaCoCo 0.8.13 gera cobertura durante `verify`.

## Build e testes

```powershell
.\mvnw.cmd clean verify
```

O JAR executável é gerado em `target/folha-pagamento-2.0.0.jar`. Para executar testes sem construir o pacote, use `.\mvnw.cmd test`. Testes PostgreSQL requerem Docker compatível com Testcontainers; consulte [Relatorio_Execucao_Testes_Unitarios.md](Relatorio_Execucao_Testes_Unitarios.md) para o estado medido nesta revisão.

## Implantação local com Docker

O Compose provisiona PostgreSQL 17 e pgAdmin. Preencha `.env` localmente, execute `docker compose up -d` e depois inicie a aplicação com `.\mvnw.cmd spring-boot:run`. A aplicação usa a porta 8081 e o banco local a porta 5433.

## Configuração e manutenção

`application.properties` define `spring.jpa.hibernate.ddl-auto=validate` e habilita Flyway. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` por ambiente. Não altere migrations já aplicadas; adicione uma nova migration versionada e valide-a antes da implantação. A configuração H2 dos testes desabilita Flyway e usa `create-drop`.

O arquivo legado `dados/colaboradores.tsv` não é importado automaticamente. Antes de qualquer manutenção, faça cópia de segurança do banco e preserve snapshots históricos.
