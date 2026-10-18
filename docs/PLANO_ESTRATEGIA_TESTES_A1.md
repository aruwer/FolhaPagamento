# Plano de Estratégia de Testes e Ambiente - A1

**Projeto:** Sistema de Folha de Pagamento  
**Versão:** QDS_1.1.0  
**Tarefa:** [Testes][A1] Planejar estratégia de testes e ambiente  
**Status:** Proposta para revisão da equipe

## 1. Objetivo

Definir como planejar, executar e registrar testes do Sistema de Folha de Pagamento, com rastreabilidade entre requisitos, casos de teste, resultados e evidências. Este documento cobre estratégia e ambiente de testes; os casos detalhados estão em [Casos de Teste Rastreáveis - A1](CASOS_TESTE_RASTREAVEIS_A1.md). O processo geral de qualidade e revisão permanece definido em [PGQ.md](PGQ.md).

## 2. Referências e premissas

- A fonte única dos requisitos é [Visão de Sistema e ERS Rastreável](../VisaoDeSistema+ERSRastreavel.pdf); o antigo `T1 - Requisitos do Cliente.pdf` foi substituído e não deve orientar escopo nem rastreabilidade.
- O escopo inclui RF001-RF011, RN001-RN010, RNF001-RNF004 e todos os critérios gerais de aceitação da ERS.
- Os requisitos da ERS são tratados como obrigatórios. Qualquer exclusão ou adiamento requer decisão formal de escopo e não deve ser disfarçado como requisito condicional.
- O estado atual observado usa Java 17, Spring Boot 3.5.5, Thymeleaf e persistência TSV; a ERS exige banco SQL em RNF003, portanto há uma lacuna de implementação. A dependência `spring-boot-starter-test` está declarada no `pom.xml`.
- A ERS requer apuração mês/ano e preservação de histórico após exclusão lógica. Os casos definem `mes` e `ano` como proposta de parâmetros HTTP porque a ERS não prescreve a URL.
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
- O comportamento de SQL não pode ser aprovado usando TSV como substituto. CT-A1-030 deve permanecer bloqueado até a persistência SQL e o ambiente de teste estarem disponíveis.

## 4. Estratégia por nível

| Nível | Finalidade e foco | Ferramenta/execução | Critério de aprovação |
|---|---|---|---|
| Unitário | Validar fórmulas, regras de domínio, fronteiras e validações isoladas. | JUnit 5/Spring Boot Test pelo Maven Wrapper. | Resultado igual ao esperado; teste determinístico e isolado. |
| Integração | Verificar controller, serviço, banco SQL, validação do fluxo, persistência/recuperação e histórico após exclusão lógica. | JUnit/Spring Boot Test e banco SQL exclusivo; dados isolados por teste. | Operações, restrições e histórico produzem o estado esperado sem contaminar outros testes. |
| Sistema | Percorrer cadastro, consulta, manutenção, relatórios filtrados por mês/ano, recibos, zeros explícitos, validação e reinício. | Aplicação local, banco SQL isolado e navegador; registrar nome e versão. | Fluxo completo atende à ERS sem defeito impeditivo. |
| Aceitação | Confirmar critérios obrigatórios com representante definido pela equipe/cliente. | Casos revisados em ferramenta de gestão aprovada ou planilha versionada. | Cada critério obrigatório aprovado ou desvio formalmente aceito. |

Identificar explicitamente quais casos são automatizados e quais são manuais. Repetir os casos afetados após correções e executar regressão dos cálculos, validações, persistência e relatórios relacionados.

## 5. Ambiente

### Ambiente automatizado

- Sistema operacional de desenvolvimento: Windows.
- Runtime/build: JDK 17 e Maven Wrapper incluído no repositório (`mvnw.cmd`).
- Comandos a partir da raiz do projeto:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

- Framework: Spring Boot 3.5.5; testes com `spring-boot-starter-test`/JUnit.
- Persistência requerida pela ERS: banco SQL. Configurar instância/esquema exclusivo de teste, com dados reproduzíveis e sem credenciais versionadas. O TSV atual não satisfaz RNF003.
- Cobertura: JaCoCo ou ferramenta equivalente não está declarada no `pom.xml` no estado consultado. Só publicar percentual após configurar uma ferramenta aprovada e registrar o escopo medido.

### Ambiente manual

- Executar a aplicação conforme as instruções do `README.md` e confirmar a URL/porta efetiva na configuração da versão testada.
- Registrar navegador e versão, commit ou versão da aplicação, data, executor e dados de teste.
- Usar dados fictícios e preparar o estado inicial necessário para cada execução.
- Usar banco SQL exclusivo de teste, esquema e dados reproduzíveis, sem credenciais versionadas. Nunca executar rotinas destrutivas contra dados reais.

## 6. Ferramentas e evidências

| Ferramenta | Uso | Situação |
|---|---|---|
| Git/GitHub Issues, Projects e Pull Requests | Rastrear tarefa, defeitos, revisão e versão testada. | Conforme fluxo de trabalho acordado pela equipe. |
| Maven Wrapper | Compilar e executar testes automatizados. | Incluído (`mvnw.cmd`). |
| JUnit/Spring Boot Test | Testes unitários e de integração. | Dependência presente; confirmar/criar a suíte de testes da tarefa. |
| Navegador web | Testes manuais de sistema e interface. | Registrar navegador e versão usados. |
| Kimi TCMS | Organizar casos e registrar resultados/evidências. | Sugestão do Kanban; confirmar acesso, configuração e formato de exportação. Se não disponível, usar planilha versionada. |
| JaCoCo ou equivalente | Medir cobertura de código. | Não configurado no `pom.xml` consultado; requer decisão e configuração. |

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

- Critérios de aceitação obrigatórios do cliente verificados, incluindo cadastro e cálculo das três categorias, validações acordadas, relatórios exigidos e total geral.
- Casos de requisitos obrigatórios executados; desvios formalmente documentados e aceitos.
- Casos críticos aprovados e nenhum defeito crítico aberto relacionado à entrega sem decisão formal.
- Resultados de build, testes unitários, integração, sistema e aceitação registrados conforme aplicabilidade.
- Evidências rastreáveis à versão testada.
- RF001-RF011, RN001-RN010, RNF001-RNF004 e critérios gerais de aceitação da ERS rastreados a casos; desvios devem ter decisão formal.
- Filtragem por mês/ano, soft delete com histórico e persistência SQL verificados, ou explicitamente reprovados/bloqueados com evidência.

A meta recomendada de cobertura de código de 80%, se aprovada pela equipe, é métrica complementar. Ela não substitui testes funcionais nem comprova por si só a qualidade da versão.

## 9. Rastreabilidade e manutenção

Os casos usam IDs estáveis no formato `CT-A1-NNN` e relacionam requisito, nível, prioridade, dados, passos, resultado esperado e método de execução. A matriz e os resultados devem ser atualizados quando requisitos ou comportamento mudarem. Rever este plano se mudar a persistência, o stack de testes, as ferramentas de gestão ou o escopo aprovado.

## 10. Referências

- [Visão de Sistema e ERS Rastreável](../VisaoDeSistema+ERSRastreavel.pdf), fonte única dos requisitos e critérios de aceitação desta versão.
- [Casos de Teste Rastreáveis - A1](CASOS_TESTE_RASTREAVEIS_A1.md).
- [PGQ oficial](PGQ.md), para processo de qualidade, revisão e defeitos.
- `README.md`, `pom.xml` e configuração efetiva da versão testada.
- O antigo `T1 - Requisitos do Cliente.pdf` não é referência de testes para esta versão.