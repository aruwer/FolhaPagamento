# Relatório de Revisão Técnica

**Projeto:** Sistema de Folha de Pagamento · **Versão:** 2.0.0  
**Revisor(a):** ____________________________________  
**Data:** ____/____/________

## Não conformidades e problemas encontrados

1. A especificação dos CT-015, CT-016, CT-019 e CT-020 contrariava a RN010 ao rejeitar campos variáveis em branco.
2. Faltava uma bateria HTTP integrada de ponta a ponta para os cenários funcionais solicitados.
3. A execução PostgreSQL com Testcontainers não pôde iniciar, apesar de o Docker CLI estar funcional; o cliente recebeu HTTP 400 no endpoint de informações do daemon.
4. As imagens CT-040 anexadas mostram a impressão da folha consolidada, não o demonstrativo individual descrito pelo caso.
5. Os vídeos CT-030 e CT-032 estão anexados, mas não foram revisados nesta revisão automatizada.

## Correções realizadas

- Especificação dos quatro casos alinhada à RN010, sem alterar os demais casos.
- Criado `FolhaPagamentoWebCasosDeTesteTest`, com 20 testes HTTP em MockMvc e dados H2 isolados.
- Renomeado o teste de configuração para `FolhaPagamentoWebApplicationTest` e removido o teste frágil que interceptava `SpringApplication`.
- Adicionados rótulos de casos/requisitos aos testes sem rastreabilidade explícita.
- Documentada a causa observada do bloqueio Testcontainers e mantido o skip condicional.
- Rebaixados para pendente critérios e casos sem evidência manual revisada; as capturas observáveis foram descritas sem alterar os arquivos de evidência.

## Resultado de verificação

`clean verify`: BUILD SUCCESS; 84 execuções aprovadas, 0 falhas, 0 erros e 3 testes ignorados. Cobertura JaCoCo: 93,38% de linhas, 94,10% de instruções e 77,32% de branches. O detalhe por classe está em [Relatorio_Execucao_Testes_Unitarios.md](Relatorio_Execucao_Testes_Unitarios.md).

## Lições aprendidas

- A rastreabilidade de um caso deve refletir o comportamento efetivamente especificado e implementado, em especial quando uma regra trata dados ausentes como zero.
- Testes web com MockMvc e banco H2 verificam as rotas e o bind HTTP sem depender do Docker.
- Um Docker CLI operacional não comprova que a biblioteca Java/Testcontainers consegue consultar o mesmo daemon.
- Evidências anexadas devem ser verificadas quanto ao cenário representado antes de serem usadas para aprovar um critério.

## Parecer

**Conclusão do(a) revisor(a):**  
____________________________________________________________________________  
____________________________________________________________________________

**Assinatura:** ____________________________________
