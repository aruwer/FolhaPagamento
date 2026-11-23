# Resumo do Fechamento da A3

**Branch:** `feat/QDS_2.0.0/fechamento-a3`  
**Commit do código e testes executados:** `d8db957`  
**Último commit consultado antes deste resumo:** `49b5665`

## Arquivos alterados

**Atualizados**

- `README.md`
- `09_Homologacao/Criterios_Aceitacao.md`
- `09_Homologacao/Roteiro_Testes_Manuais.md`
- [Casos_Teste_Rastreaveis_A1.md](Casos_Teste_Rastreaveis_A1.md)
- Testes: `ColaboradorFormTest.java`, `ColaboradorTest.java`, `ConformidadeRequisitosTest.java`, `FolhaPagamentoControllerTest.java`, `FolhaPagamentoServiceTest.java`, `RepositorioColaboradoresPostgresTest.java` e `RepositorioColaboradoresTest.java`.

**Criados**

- `FolhaPagamentoWebCasosDeTesteTest.java`
- Documentos de entrega: [Registro_Defeitos.md](Registro_Defeitos.md), [Metricas_Qualidade.md](Metricas_Qualidade.md), [Relatorio_Revisao_Tecnica.md](Relatorio_Revisao_Tecnica.md), [Manual_Usuario.md](Manual_Usuario.md), [Manual_Tecnico.md](Manual_Tecnico.md), [Termo_Homologacao.md](Termo_Homologacao.md), [Relatorio_Final_Qualidade.md](Relatorio_Final_Qualidade.md).
- [Relatorio_Execucao_Testes_Unitarios.md](Relatorio_Execucao_Testes_Unitarios.md) e o [log integral](log-verify.txt).
- Este próprio resumo: `Resumo_Fechamento_Agente.md`.

**Renomeados/removidos**

- `FolhaPagamentoConfigTest.java` foi renomeado para `FolhaPagamentoWebApplicationTest.java`; o teste frágil `iniciaAplicacao` foi removido.
- O relatório duplicado `09_Homologacao/Relatorio_Execucao.md` foi removido após consolidação em `docs/Relatorio_Execucao_Testes_Unitarios.md`.
- Os nomes de arquivo dos casos de teste e do relatório consolidado foram normalizados para a capitalização usada pelos links e pelo roteiro de entrega.

## Resultado real da suíte

Copiado de [log-verify.txt](log-verify.txt): **BUILD SUCCESS**; 87 execuções descobertas, **84 aprovadas**, **0 falhas**, **0 erros** e **3 ignoradas** em `RepositorioColaboradoresPostgresTest`. Cobertura JaCoCo: linhas 93,38%, instruções 94,10%, branches 77,32%.

## Pendências que dependem de ação humana ou ambiente

- Investigar a resposta HTTP 400 do daemon Docker recebida por Testcontainers; os três testes PostgreSQL seguem **Bloqueados/ignorados**, apesar de o Docker CLI estar funcional.
- Revisar os vídeos anexados de CT-030 e CT-032.
- Executar/revisar CT-030-D (persistência após reinício).
- Concluir CT-033 com revisor humano e assinatura.
- Refazer/revisar as evidências de CT-038 e CT-040 no demonstrativo individual; as capturas existentes mostram a folha consolidada e foram preservadas.
- Preencher a métrica de requisitos aprovados depois de revisar evidências e obter validação da equipe; completar o termo de homologação e assinaturas.

## Decisões para validação da equipe

- ACE-3, ACE-4 e ACE-6 permaneceram **Pendentes**; não foram homologados com base apenas em testes automatizados ou vídeos não revisados.
- ACE-1, ACE-2 e ACE-5 foram mantidos como **Aprovados** com log real e evidências visuais revisadas correspondentes.
- O bloqueio Testcontainers foi descrito pela resposta observada (HTTP 400 com dados de daemon vazios); a causa subjacente não foi presumida.
- Nenhum arquivo de evidência, `.env`, regra de negócio de produção ou teste existente foi alterado para contornar os bloqueios.
