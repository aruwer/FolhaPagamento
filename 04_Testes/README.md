# 04 — Testes

| Artefato | Itens do enunciado atendidos |
| :--- | :--- |
| [Plano_Testes.md](Plano_Testes.md) | 5.6 Plano de testes: escopo, estratégia, níveis, ambiente, ferramentas, critérios de entrada/saída e de aprovação |
| [Casos_Teste_Rastreaveis.md](Casos_Teste_Rastreaveis.md) | 5.7 Especificação de 41 casos de teste (mínimo exigido: 20), rastreabilidade requisito × caso e registro de execução da A3 |
| [Relatorio_Execucao_Testes.md](Relatorio_Execucao_Testes.md) | 6.3 Testes unitários: relatório de execução e cobertura (96,96% de linhas, 95,88% de branches) |
| [Relatorio_Testes_Integracao.md](Relatorio_Testes_Integracao.md) | 6.4 Testes de integração: persistência, fluxos de negócio, relatórios e integração entre componentes |
| [log-verify.txt](log-verify.txt) | Saída integral de `.\mvnw.cmd clean verify` |
| [surefire-reports/](surefire-reports/) | Relatórios Surefire (TXT e XML) por classe de teste |

Testes de backend (Bruno CLI) e de frontend (Cypress) contra a aplicação real, com evidências em arquivos: [tests-e2e/](../tests-e2e/README.md) e [09_Homologacao/evidencias-auto/](../09_Homologacao/evidencias-auto/).

O código dos testes está em [`src/test/java/folhapagamento/`](../src/test/java/folhapagamento/). Os testes de aceitação (6.5) estão em [09_Homologacao/Relatorio_Testes_Aceitacao.md](../09_Homologacao/Relatorio_Testes_Aceitacao.md).
