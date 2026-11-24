# Relatório de Testes de Aceitação

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0 · **Período de execução:** 23/11/2026  
**Ambiente:** aplicação `folha-pagamento-2.0.0.jar` + PostgreSQL 17 (Docker Compose); navegadores Opera (equipe) e Edge 158 (capturas da revisão de fechamento)  
**Base:** critérios gerais de aceitação da ERS (2.4) e casos de sistema/aceitação de [Casos_Teste_Rastreaveis.md](../04_Testes/Casos_Teste_Rastreaveis.md); passos detalhados em [Roteiro_Testes_Manuais.md](Roteiro_Testes_Manuais.md)

## Massa de dados usada na revisão de fechamento

Database dedicada `folha_evidencias`, alimentada pela própria interface HTTP:

| Matrícula | Nome | Tipo | Dados | Salário final esperado |
| :--- | :--- | :--- | :--- | ---: |
| E001 | Ana Souza | Padrão | base 3.000,00 (alterada para 3.200,00 após 11/2026) | 3.000,00 (11) / 3.200,00 (12) |
| E002 | Bruno Lima | Comissionado | base 2.000,00; vendas 10.000,00; 5% | 2.500,00 |
| E003 | Carla Dias | Produção | base 1.800,00; 120 × 2,50 | 2.100,00 |
| E004 | Diego Rocha | Comissionado | base 2.000,00; vendas e percentual em branco; excluído após 11/2026 | 2.000,00 (só em 11) |
| T130 | Sara Melo | Produção | base 1.900,00; 80 × 3,00 | 2.140,00 |

## Resultados

| # | Cenário testado | Resultado esperado | Resultado obtido | Situação | Evidência |
| :---: | :--- | :--- | :--- | :---: | :--- |
| 1 | **ACE-1** — Cadastrar os três tipos (CT-034, CT-023) | Formulário exibe os campos do tipo escolhido; os três tipos aparecem na lista com detalhes e salário final | Campos condicionais corretos para Padrão, Comissionado e Produção; painel lista E001–E003 com R$ 3.200,00, R$ 2.500,00 e R$ 2.100,00 | **Aprovado** | [CT-034](evidencias/CT-034/), [CT-023](evidencias/CT-023/CT-023-A3-lista-colaboradores.png) |
| 2 | **ACE-2** — Enviar formulário com nome e salário em branco (CT-035) | Envio bloqueado com mensagem compreensível; nada é gravado | Navegador destaca o campo com "Preencha este campo."; nenhum registro criado | **Aprovado** | [CT-035](evidencias/CT-035/CT-035-passo1.png) |
| 3 | **ACE-2** — Matrícula duplicada, valores negativos, percentual > 100, campos em branco (CT-009 a CT-022) | Rejeição com erro no campo; campos variáveis em branco viram zero | 105 testes automatizados aprovados, incluindo os 10 cenários HTTP de validação | **Aprovado** | [log](../04_Testes/log-verify.txt) |
| 4 | **ACE-3** — Folha consolidada 11/2026 (CT-026) | Adicionais 0 / 500 / 300 / 0; finais 3.000 / 2.500 / 2.100 / 2.000 | Exatamente os valores esperados | **Aprovado** | [CT-026](evidencias/CT-026/CT-026-A3-folha-consolidada-11-2026.png) |
| 5 | **ACE-3** — Demonstrativo comissionado E002 (CT-027) | Base 2.000,00; adicional 500,00; final 2.500,00 | Conforme esperado | **Aprovado** | [CT-027](evidencias/CT-027/CT-027-A3-individual-comissionado-E002.png) |
| 6 | **ACE-3 / RNF003** — Persistir T130 e reiniciar a aplicação (CT-030) | Mesmo salário final (2.140,00) antes e depois do reinício; matrícula única | 2.140,00 antes e depois; 1 linha `T130` no banco | **Aprovado** | [CT-030 T130](evidencias/CT-030/CT-030-A3-T130-reinicio.txt) |
| 7 | **RNF003** — Reiniciar com 4 colaboradores e 2 competências processadas (CT-030-D) | Cadastros, exclusão lógica e 7 snapshots preservados; Flyway não reaplica migrations | "Schema is up to date"; painel com 3 ativos; folha 11/2026 com total 9.600,00 | **Aprovado** | [CT-030-D](evidencias/CT-030/CT-030-D-A3-persistencia-apos-reinicio.txt) |
| 8 | **ACE-4** — Consultar 12/2026 após alterar E001 e excluir E004 (CT-036) | 12/2026 com 3 colaboradores, E001 com 3.200,00; 11/2026 inalterada | 12/2026: 3 colaboradores, total 7.800,00, E001 = 3.200,00; 11/2026 continua 9.600,00 | **Aprovado** | [CT-036](evidencias/CT-036/CT-036-A3-folha-12-2026.png) |
| 9 | **ACE-4** — Consultar competência não processada 01/2027 (CT-036) | Aviso de folha não processada, sem erro | Aviso exibido e botão "Processar folha desta competência" | **Aprovado** | [CT-036](evidencias/CT-036/CT-036-A3-competencia-nao-processada-01-2027.png) |
| 10 | **ACE-4 / RN009** — Demonstrativo do colaborador excluído (CT-037) | Snapshot 11/2026 de E004 disponível após a exclusão | Demonstrativo exibido; botão "Editar cadastro" ausente (inativo) | **Aprovado** | [CT-038/E004](evidencias/CT-038/CT-038-A3-individual-adicional-zero-E004.png) |
| 11 | **ACE-5** — Total geral e totais por categoria (CT-026, CT-039) | 11/2026: 9.600,00 (Padrão 3.000, Comissionado 4.500, Produção 2.100) | Conforme esperado | **Aprovado** | [CT-026](evidencias/CT-026/CT-026-A3-folha-consolidada-11-2026.png) |
| 12 | **RF010 / RN010** — Adicional zerado no demonstrativo (CT-038) | Adicional exibido como R$ 0,00, não vazio | "Adicionais R$ 0,00" para E004 | **Aprovado** | [CT-038](evidencias/CT-038/CT-038-A3-individual-adicional-zero-E004.png) |
| 13 | **RF010 / RNF001** — Imprimir demonstrativo (CT-040) | Impressão com dados do demonstrativo, sem navegação/botões | PDF com E003, 1.800 + 300 = 2.100,00, sem menu, botões ou rodapé | **Aprovado** | [CT-040 PDF](evidencias/CT-040/CT-040-A3-impressao-demonstrativo-E003.pdf) |
| 14 | **RNF001** — Usabilidade com 5 tarefas (CT-032) | Usuário conclui as tarefas sem ajuda | 5/5 tarefas concluídas sem ajuda (20–30 s cada) | **Aprovado** | [vídeos CT-032](evidencias/CT-032/), [roteiro](Roteiro_Testes_Manuais.md#ct-032--fluxo-de-tarefas-de-usabilidade) |
| 15 | **ACE-6 / RNF004** — Incluir tipo `BONUS_FIXO` (CT-033) | Novo tipo sem alterar cálculos existentes | Subclasse calcula 2.350,00; tipos existentes inalterados; mudanças necessárias só aditivas | **Aprovado** | [inspeção CT-033](Roteiro_Testes_Manuais.md#ct-033--avaliar-extensibilidade-do-domínio-inspeção-de-código) |

## Resumo

| Situação | Cenários |
| :--- | :---: |
| Aprovado | **15** |
| Reprovado | 0 |
| Bloqueado | 0 |

Critérios gerais de aceitação da ERS: **6/6 aprovados** ([Criterios_Aceitacao.md](Criterios_Aceitacao.md)). Nenhum defeito aberto. Parecer formal no [Termo de Homologação](Termo_Homologacao.md).
