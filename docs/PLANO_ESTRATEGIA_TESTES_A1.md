# Plano de Estratégia de Testes e Ambiente - A1

**Projeto:** Sistema de Folha de Pagamento  
**Versão:** QDS_1.1.0  
**Tarefa:** [Testes][A1] Planejar estratégia de testes e ambiente  
**Status:** Especificação do plano para A1; decisões de ambiente e contratos pendentes estão identificados abaixo

## 1. Objetivo

Definir como planejar, executar e registrar testes do Sistema de Folha de Pagamento, com rastreabilidade entre requisitos, casos de teste, resultados e evidências. Este documento cobre estratégia e ambiente de testes; os casos detalhados estão em [Casos de Teste Rastreáveis - A1](CASOS_TESTE_RASTREAVEIS_A1.md). O processo geral de qualidade e revisão permanece definido em [PGQ.md](PGQ.md).

## 2. Referências e premissas

- A fonte única dos requisitos é [Visão de Sistema e ERS Rastreável](VisaoDeSistema+ERSRastreavel.pdf), armazenada nesta mesma pasta `docs`. O documento anterior de requisitos não é usado para definir escopo nem rastreabilidade.
- O escopo inclui RF001-RF011, RN001-RN010, RNF001-RNF004 e todos os critérios gerais de aceitação da ERS.
- Os requisitos da ERS são tratados como obrigatórios. Qualquer exclusão ou adiamento requer decisão formal de escopo e não deve ser disfarçado como requisito condicional.
- O estado atual observado usa Java 17, Spring Boot 3.5.5, Thymeleaf e persistência em TSV; a ERS exige banco SQL em RNF003. A dependência `spring-boot-starter-test` está declarada, mas não há suíte em `src/test` neste estado do repositório. Assim, JUnit/MockMvc são ferramentas planejadas, não testes já executados.
- A ERS exige apuração mês/ano, exclusão lógica com histórico e valores nulos apresentados como zero. Ela não define os parâmetros HTTP da competência nem rota específica de recibo; os casos registram propostas e não tratam essas rotas como contrato já implementado.
- Os cenários descrevem verificações planejadas. Requisitos sem implementação/ambiente disponível devem ficar bloqueados ou reprovados com evidência, nunca marcados como aprovados por inferência. A especificação A1 pode estar completa mesmo quando um caso de implementação futura permaneça bloqueado para execução.
- Usar exclusivamente dados fictícios. Não guardar credenciais ou dados pessoais reais no repositório ou nas evidências.

## 3. Escopo de teste

### Incluído

- Cadastro e consulta de colaboradores padrão, comissionados e por produção.
- Cálculo do salário final e dos adicionais de cada categoria.
- Validação de campos obrigatórios e valores não negativos.
- Cálculo e emissão de folha individual/consolidada filtrados por mês e ano.
- Resumo com quantidade, total geral e totais por categoria no mês/ano.
- Relatório individual e recibo com salário base, adicionais e salário final; valores variáveis nulos devem ser apresentados como R$ 0,00.
- Inclusão, consulta, alteração e exclusão lógica, preservando folhas históricas após remoção do cadastro ativo.
- Persistência em banco SQL, leitura após reinício e integridade histórica.
- Fluxos inválidos, limites de valores, regressão e avaliação guiada da interface.

### Fora do escopo atual

- Regras fiscais, descontos legais, integração bancária, autenticação e integrações externas que não constem dos requisitos aprovados.
- Testes de carga, alta disponibilidade ou desempenho em produção sem metas quantitativas acordadas; esses atributos não têm metas na ERS.
- RNF003 não pode ser aprovado usando TSV como substituto. CT-030 deve permanecer bloqueado até a persistência SQL e o ambiente de teste estarem disponíveis.

## 4. Estratégia por nível

| Nível | Finalidade e foco | Ferramenta/execução | Critério de aprovação |
|---|---|---|---|
| Unitário | Validar fórmulas, regras de domínio, fronteiras e validações isoladas. | JUnit 5/Spring Boot Test pelo Maven Wrapper. | Resultado igual ao esperado; teste determinístico e isolado. |
| Integração | Verificar controller, serviço, banco SQL, validação do fluxo, persistência/recuperação e histórico após exclusão lógica. | JUnit/Spring Boot Test e banco SQL exclusivo; dados isolados por teste. | Operações, restrições e histórico produzem o estado esperado sem contaminar outros testes. |
| Sistema | Percorrer cadastro, consulta, manutenção, relatórios filtrados por mês/ano, recibos, zeros explícitos, validação e reinício. | Aplicação local, banco SQL isolado e navegador; registrar nome e versão. | Fluxo completo atende à ERS sem defeito impeditivo. |
| Aceitação | Confirmar os critérios da ERS com representante definido pela equipe e, quando aplicável, cliente/professora. | Casos revisados em ferramenta de gestão aprovada ou planilha versionada. | Cada critério obrigatório aprovado ou desvio formalmente aceito, com evidência. |

Os casos identificam a camada e a forma proposta de execução. Os candidatos a automação HTTP poderão usar MockMvc/JUnit após a criação da suíte; os casos de UI/aceitação são manuais no navegador. No estado atual não há testes automatizados em `src/test`, portanto não declarar automação executada. Após correções, repetir os casos afetados e a regressão de cálculos, validações, persistência e relatórios.

## 5. Ambiente

### Ambiente automatizado

- Sistema operacional de desenvolvimento: Windows.
- Runtime/build: JDK 17 e Maven Wrapper incluído no repositório (`mvnw.cmd`).
- Comandos a partir da raiz do projeto:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

- Framework: Spring Boot 3.5.5; `spring-boot-starter-test`/JUnit consta no `pom.xml`, mas a suíte ainda precisa ser criada.
- Persistência requerida pela ERS: banco SQL. O projeto atual usa TSV, então cenários de persistência SQL ficam bloqueados até implementar essa arquitetura. Quando disponível, configurar instância/esquema exclusivo, dados reproduzíveis e credenciais fora do repositório.
- Cobertura: JaCoCo ou ferramenta equivalente não está declarada no `pom.xml` no estado consultado. Só publicar percentual após configurar uma ferramenta aprovada e registrar o escopo medido.

### Ambiente manual

- Executar a aplicação conforme `application.properties`; no estado consultado a porta é `8081`. O README ainda informa `8080`, então confirmar a URL efetiva antes de cada execução e corrigir a divergência na documentação de execução.
- Registrar navegador e versão, commit ou versão da aplicação, data, executor e dados de teste.
- Usar dados fictícios e preparar o estado inicial necessário para cada execução.
- Usar banco SQL exclusivo de teste, esquema e dados reproduzíveis, sem credenciais versionadas. Nunca executar rotinas destrutivas contra dados reais.

## 6. Ferramentas e evidências

| Ferramenta | Uso | Situação |
|---|---|---|
| Maven Wrapper | Compilar o projeto e executar a suíte. | Incluído (`mvnw.cmd`); comandos: `test` e `clean package`. |
| JUnit 5, Spring Boot Test e MockMvc | Automatizar testes unitários e de integração HTTP. | Dependências de teste declaradas; falta criar a suíte em `src/test`. |
| Microsoft Edge ou Google Chrome | Executar casos manuais de sistema/UI/aceitação. | Usar o navegador disponível no ambiente e registrar produto, versão, viewport e evidências por caso. |
| Git e Markdown versionado | Manter casos, matriz e registro de resultados. | Usar a tabela de execução deste documento de casos; repetir uma linha por caso e execução. |
| GitHub Issues e Pull Requests | Registrar defeitos, revisão e versão testada. | Usar o repositório remoto; vincular issue/PR ao ID do caso quando houver defeito ou correção. |
| JaCoCo | Medir cobertura de código, se exigida para a versão. | Não configurado; não reportar percentual até configurar e executar a ferramenta. |

Para cada execução guardar: ID do caso, versão/commit, data, executor, ambiente, dados utilizados, resultado (aprovado/reprovado/bloqueado), evidência e, em caso de falha, issue relacionada. Não registrar casos ainda não executados como aprovados.

## 7. Critérios de entrada e saída

### Entrada

- Requisitos aplicáveis e casos revisados, com resultados esperados objetivos.
- Commit ou versão candidata identificado.
- Build concluído e aplicação inicializável no ambiente aplicável.
- Dados fictícios e ambiente isolado preparados.
- Responsáveis sabem onde registrar resultado, evidência e defeito.

Se algum pré-requisito impedir a execução, marcar os casos afetados como **bloqueados**, descrevendo a dependência. Bloqueado não equivale a aprovado.

### Saída do ciclo

- Casos previstos classificados como aprovados, reprovados ou bloqueados, com executor e evidência.
- Defeitos registrados com caso/requisito relacionado, severidade, responsável e estado.
- Correções retestadas e regressões pertinentes executadas.
- Métricas reportadas com método, fonte, versão e data.
- Requisitos sem caso, limitações e testes não executados explicitados.

## 8. Critérios de aprovação dos testes

- Critérios de aceitação da ERS verificados: cadastro dos três tipos, validação de dados obrigatórios conforme RN, cálculo correto por categoria, folha individual/consolidada por mês/ano, total geral do período e possibilidade de expansão para novos tipos.
- Casos de requisitos obrigatórios executados; desvios formalmente documentados e aceitos.
- Casos críticos aprovados e nenhum defeito crítico aberto relacionado à entrega sem decisão formal.
- Resultados de build, testes unitários, integração, sistema e aceitação registrados conforme aplicabilidade.
- Evidências rastreáveis à versão testada, executor, ambiente e resultado.
- RF001-RF011, RN001-RN010, RNF001-RNF004 e critérios gerais de aceitação da ERS rastreados a casos; desvios devem ter decisão formal.
- Filtragem por mês/ano, soft delete com histórico e persistência SQL verificados, ou explicitamente reprovados/bloqueados com evidência.

A meta recomendada de cobertura de código de 80%, se aprovada pela equipe, é métrica complementar. Ela não substitui testes funcionais nem comprova por si só a qualidade da versão.

## 9. Rastreabilidade e manutenção

Os casos usam IDs estáveis no formato `CT-NNN` e relacionam requisito, nível, prioridade, dados, passos, resultado esperado e método de execução. A matriz e os resultados devem ser atualizados quando requisitos ou comportamento mudarem. Rever este plano se mudar a persistência, o stack de testes, as ferramentas de gestão ou o escopo aprovado.

## 10. Referências

- [Visão de Sistema e ERS Rastreável](VisaoDeSistema+ERSRastreavel.pdf), fonte única dos requisitos e critérios de aceitação desta versão; o arquivo está em `docs/` junto a este plano.
- [Casos de Teste Rastreáveis - A1](CASOS_TESTE_RASTREAVEIS_A1.md).
- [PGQ oficial](PGQ.md), para processo de qualidade, revisão e defeitos.
- `README.md`, `pom.xml` e configuração efetiva da versão testada.
- Aplicação e testes ainda não comprovam RNF003 (SQL), filtro de competência, exclusão lógica/histórico ou emissão de recibo. Esses pontos são lacunas/bloqueios conhecidos, não resultados aprovados.