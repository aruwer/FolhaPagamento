# Critérios de Aceitação (ERS, seção 2.4)

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0
**Regra:** um critério só recebe "Aprovado" quando o teste automatizado ou o caso manual correspondente foi executado e registrado. Enquanto isso, a situação é "Pendente".

Os critérios abaixo foram aceitos com base nos testes automatizados que passaram e nos casos manuais registrados. Isso não altera o resultado da suíte Testcontainers: `RepositorioColaboradoresPostgresTest` continua com 3 testes ignorados por incompatibilidade de conexão com o Docker Desktop.

| ID | Critério (ERS 2.4) | Testes automatizados | Casos manuais | Situação | Evidência |
| :--- | :--- | :--- | :--- | :---: | :--- |
| ACE-1 | Permitir o cadastro dos três tipos de colaboradores | `ColaboradorFormTest`, `ColaboradorTest`, `FolhaPagamentoControllerTest`, `RepositorioColaboradoresTest` | CT-032, CT-034 | **Aprovado** | [CT-032 — vídeos](evidencias/CT-032/); [CT-034 — capturas](evidencias/CT-034/); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-2 | Validar os dados obrigatórios conforme as regras de negócio | `ColaboradorFormTest`, `ConformidadeRequisitosTest`, `FolhaPagamentoControllerTest` | CT-035 | **Aprovado** | [CT-035 — captura](evidencias/CT-035/); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-3 | Calcular corretamente os salários de cada categoria | `ColaboradorTest`, `ConformidadeRequisitosTest`, `FolhaPagamentoServiceTest` | CT-030-A, CT-030-B, CT-030-C | **Aprovado** | [CT-030 — vídeos dos três cenários](evidencias/CT-030/); suíte automatizada registrada em [Relatorio_Execucao.md](Relatorio_Execucao.md) |
| ACE-4 | Gerar folhas individuais e consolidadas respeitando os filtros de mês e ano | `FolhaPagamentoControllerTest`, `FolhaPagamentoServiceTest`, `RepositorioColaboradoresTest` (H2) | CT-030-B | **Aprovado** | [CT-030-B — evidência](evidencias/CT-030/CT-030-B.mp4); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-5 | Exibir o total geral da folha de pagamento do período | `FolhaPagamentoControllerTest`, `FolhaPagamentoServiceTest` | CT-038 (visual) | **Aprovado** | [CT-038 — captura](evidencias/CT-038/CT-038-passo1.png); [roteiro](Roteiro_Testes_Manuais.md) |
| ACE-6 | Possibilitar expansão para novos tipos de remuneração | `ColaboradorTest` (cálculos polimórficos) | CT-033 (inspeção de código) | **Aprovado** | [resultado da inspeção CT-033](Roteiro_Testes_Manuais.md#ct-033--avaliar-extensibilidade-do-domínio-inspeção-de-código) |

## Registro da execução

| ID | Executor | Data | Commit | Resultado | Observações |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ACE-1 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Três categorias cobertas pela suíte automatizada e CT-032/CT-034 |
| ACE-2 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Validações automatizadas e CT-035 |
| ACE-3 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Cálculos automatizados e CT-030-A/B/C manual |
| ACE-4 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Competências de fevereiro/março de 2099 e snapshots preservados no CT-030-B |
| ACE-5 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Total e adicionais zerados observados em CT-038 |
| ACE-6 | Amanda Ruwer | 23/11/2026 | `da9cabf` | Aprovado | Inspeção de extensibilidade registrada em CT-033 |
