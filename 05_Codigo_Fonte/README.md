# 05 — Código-Fonte

O projeto Maven está na **raiz do repositório**, para que `mvnw.cmd`, o Docker Compose e as IDEs funcionem sem configuração adicional. Esta pasta funciona como índice (seção 6.2 do enunciado).

| Item do enunciado | Onde está |
| :--- | :--- |
| Código completo | [`src/main/java/folhapagamento/`](../src/main/java/folhapagamento/) (17 classes), [`src/main/resources/templates/`](../src/main/resources/templates/) (Thymeleaf), [`src/main/resources/static/`](../src/main/resources/static/) (CSS/JS) |
| Código dos testes | [`src/test/java/folhapagamento/`](../src/test/java/folhapagamento/) (11 classes, 106 testes) |
| Scripts de banco de dados | [`src/main/resources/db/migration/`](../src/main/resources/db/migration/) (Flyway V1, V2, V3) e [06_Banco_Dados/seed-colaboradores.sql](../06_Banco_Dados/seed-colaboradores.sql) |
| Arquivos de configuração | [`pom.xml`](../pom.xml), [`application.properties`](../src/main/resources/application.properties), [`docker-compose.yml`](../docker-compose.yml), [`.env.example`](../.env.example) |
| Instruções de instalação | [README principal](../README.md#instalação-e-execução) e [Manual do Usuário](../08_Manuais/Manual_Usuario.md) |

## Organização das classes

| Camada | Classes |
| :--- | :--- |
| Apresentação | `FolhaPagamentoController`, `ColaboradorForm`, `FormatadorMoeda` |
| Aplicação | `FolhaPagamentoService`, `ResumoFolha` |
| Domínio | `Colaborador` (abstrata), `ColaboradorPadrao`, `ColaboradorComissionado`, `ColaboradorProducao`, `FolhaProcessada` |
| Persistência | `RepositorioColaboradores` (contrato), `RepositorioColaboradoresJpa`, `ColaboradorJpaEntity`, `ColaboradorJpaRepository`, `FolhaProcessadaJpaEntity`, `FolhaProcessadaJpaRepository` |
| Inicialização | `FolhaPagamentoWebApplication` |

Descrição detalhada no [Manual Técnico](../08_Manuais/Manual_Tecnico.md) e no [Documento de Arquitetura](../03_Projeto/Documento_Arquitetura.md).

## Padrões aplicados (PGQ 6.1)

- `BigDecimal` com duas casas e `RoundingMode.HALF_UP` em todos os valores monetários.
- Validação em duas camadas: Bean Validation no formulário e invariantes no construtor do domínio.
- `getSalarioFinal()` é `final` na classe base; cada subtipo só define `getAdicional()`.
- Commits semânticos e branches `feat|fix|docs|test/QDS_2.0.0/...` — histórico em <https://github.com/aruwer/FolhaPagamentoA3/commits>.
