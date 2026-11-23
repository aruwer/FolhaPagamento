# Registro de Defeitos e Pendências

Registro restrito a inconsistências observadas nesta revisão. Evidências da equipe em `09_Homologacao/evidencias/` não foram modificadas.

| ID | Descrição | Severidade | Prioridade | Status | Solução |
| :--- | :--- | :--- | :--- | :--- | :--- |
| BUG-001 | Os cenários CT-015, CT-016, CT-019 e CT-020 descreviam rejeição de valores variáveis em branco, em contradição com RN010 e com a conversão do formulário. | S3 | P1 | Corrigido | Especificação atualizada para tratar branco como zero e concluir o cadastro. |
| BUG-002 | Os três testes de `RepositorioColaboradoresPostgresTest` foram ignorados: Testcontainers recebe HTTP 400 ao consultar o Docker Desktop, mesmo com CLI funcional. | S3 (ambiente de teste) | P1 | Bloqueado | Diagnóstico feito com `docker info`, `docker context ls`, `docker version` e uma tentativa com `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine` no contexto `desktop-linux`. Investigar a causa do HTTP 400 sem remover `disabledWithoutDocker`. |
| BUG-003 | Não havia teste integrado HTTP de ponta a ponta cobrindo os casos especificados para as rotas web. | S3 | P1 | Corrigido | Criada `FolhaPagamentoWebCasosDeTesteTest`; seus 20 testes passaram em `clean verify`. |
| BUG-004 | As capturas CT-038 e CT-040 mostram a folha consolidada; os casos descrevem o demonstrativo individual. CT-040-passo1.png ainda exibe o botão “Imprimir folha”. | S4 (evidência) | P2 | Pendente | Reexecutar CT-038 e CT-040 no demonstrativo individual, capturar as telas correspondentes e solicitar revisão humana. As capturas existentes foram preservadas. |

**Nota:** BUG-002 é uma pendência de infraestrutura/teste, não evidência de falha nas regras de negócio da aplicação. Os três casos estão bloqueados, não aprovados.
