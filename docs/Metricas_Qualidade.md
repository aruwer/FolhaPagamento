# Métricas de Qualidade

**Fonte da execução:** [Relatorio_Execucao_Testes_Unitarios.md](Relatorio_Execucao_Testes_Unitarios.md) e [log-verify.txt](log-verify.txt), gerados em 23/11/2026 no commit de código `d8db957`.

## Cobertura

Conforme a fórmula do [PGQ](PGQ.md):

> Cobertura de linhas (%) = (linhas cobertas / total de linhas executáveis) × 100

| Medida JaCoCo | Resultado real |
| :--- | ---: |
| Linhas | 367 / 393 = **93,38%** |
| Instruções | 1610 / 1711 = **94,10%** |
| Branches | 75 / 97 = **77,32%** |

A meta do PGQ para cobertura é 80%; a cobertura de linhas medida excedeu essa meta. O build não impõe automaticamente um limite JaCoCo.

## Testes

| Indicador | Resultado |
| :--- | ---: |
| Execuções descobertas pelo Maven | 87 |
| Executadas e aprovadas | 84 |
| Falhas / erros | 0 / 0 |
| Ignoradas (PostgreSQL/Testcontainers) | 3 |
| Taxa de aprovação entre as executadas | 84 / 84 = 100% |

As três execuções ignoradas permanecem bloqueadas pela conexão de Testcontainers ao daemon Docker; não entram no denominador da taxa acima.

## Defeitos e critérios

- Defeitos/pendências documentados nesta revisão: **4**.
- Corrigidos: **2** (especificação RN010 e ausência do teste HTTP integrado).
- Bloqueados por ambiente: **1** (Testcontainers/PostgreSQL).
- Pendentes de correção de evidência: **1** (capturas do CT-040).
- Capturas revisadas com resultado observável: CT-034, CT-035 e CT-038.
- Casos manuais com vídeos pendentes de revisão: CT-030 e CT-032.
- Requisitos aprovados em casos, segundo a fórmula “RFs implementados e aprovados / total de RFs” do PGQ: **[preencher após revisão das evidências e validação da equipe]**. A matriz lista 11 requisitos funcionais implementados, mas isso não significa que todos estejam homologados.
