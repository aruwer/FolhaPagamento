# Roteiro da Apresentação — A3

**Slides:** [Apresentacao_A3_FolhaPagamento.pptx](Apresentacao_A3_FolhaPagamento.pptx) (13 slides, com notas do apresentador)  
**Duração sugerida:** 12 a 15 minutos + perguntas

## Divisão sugerida

| Bloco | Slides | Tempo | Conteúdo |
| :--- | :---: | :---: | :--- |
| Abertura e requisitos | 1–3 | 2 min | Equipe, objetivo, requisitos do cliente |
| Arquitetura | 4 | 1,5 min | Camadas, snapshots por competência, extensibilidade |
| Demonstração ao vivo | 5–6 | 4 min | Ver passo a passo abaixo |
| Testes e métricas | 7–8 | 2,5 min | Níveis de teste, cobertura, metas do PGQ |
| Defeitos e revisão técnica | 9–10 | 2 min | 6 defeitos, não conformidades, lições aprendidas |
| Homologação e entrega | 11–13 | 1,5 min | Critérios de aceitação, parecer, estrutura 01–11 |

A divisão entre as integrantes fica a critério da equipe; uma opção alinhada à EAP é: requisitos e homologação (Dev 1), arquitetura e demonstração (Dev 2), testes, métricas e qualidade (Dev 3).

## Preparação (antes de apresentar)

```powershell
docker compose up -d
$env:DATABASE_PASSWORD = "<senha do .env>"
.\mvnw.cmd clean verify          # mostrar BUILD SUCCESS e "All coverage checks have been met" se pedirem
java -jar target\folha-pagamento-2.0.0.jar
```

Deixar abertos: <http://localhost:8081>, o relatório `10_Qualidade/evidencias/jacoco/index.html` e o [Termo de Homologação](../09_Homologacao/Termo_Homologacao.md).

## Demonstração ao vivo (4 min)

1. **Painel** — mostrar colaboradores ativos e custo por categoria.
2. **Cadastrar comissionado** — "Novo colaborador" → tipo "Salário + comissão" (os campos mudam: CT-034) → matrícula `DEMO1`, salário 2000, vendas 10000, comissão 5 → cadastrar.
3. **Validação** — tentar cadastrar de novo `DEMO1` → mensagem de matrícula duplicada (RN001).
4. **Processar competência** — "Folha de pagamento" → mês/ano atual → "Processar folha desta competência" → total geral e totais por categoria (RF008, RF011).
5. **Demonstrativo** — clicar em `DEMO1` → base 2.000,00 + adicional 500,00 = 2.500,00 → "Imprimir demonstrativo" (CT-040).
6. **Histórico** — voltar ao painel, excluir `DEMO1`, reabrir a folha do mês → o snapshot continua lá (RN009).

**Plano B:** se o ambiente falhar, usar as capturas reais dos slides 5 e 6 e os arquivos de `09_Homologacao/evidencias/`.

## Perguntas prováveis

| Pergunta | Resposta curta |
| :--- | :--- |
| Por que snapshot em vez de recalcular a folha? | Para que alterações e exclusões não mudem folhas já fechadas (RN009); o relatório reflete o que foi processado. |
| Como incluir um novo tipo de remuneração? | Nova subclasse de `Colaborador` com seu `getAdicional()`; o resto é aditivo (enum, formulário, migration). Demonstrado em CT-033 com `BONUS_FIXO`. |
| Por que Testcontainers se já há H2? | O H2 não reproduz o PostgreSQL: o BUG-005 (fuso de `data_processamento`) só apareceu no banco real. |
| Por que "homologado com ressalvas"? | O produto atende a 100% dos requisitos; as ressalvas são de processo (revisões de PR e defeitos fora das Issues), com ações corretivas definidas. |
| Como a cobertura é garantida? | `jacoco:check` no `pom.xml`: o `mvnw verify` falha abaixo de 80% de linhas ou de branches (o `mvnw test` não aplica o limite). |
