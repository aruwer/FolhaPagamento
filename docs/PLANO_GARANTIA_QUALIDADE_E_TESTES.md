# 5.5 Plano de Garantia da Qualidade (PGQ)

## 5.5.1 Objetivo e aplicação

Este Plano de Garantia da Qualidade define práticas para prevenir defeitos, verificar os artefatos produzidos e decidir se uma versão do Sistema de Folha de Pagamento está apta para entrega. Aplica-se ao código Java, às páginas Thymeleaf/HTML, CSS e JavaScript, à configuração, aos dados de teste, aos casos de teste e à documentação do projeto.

O plano considera como fonte de requisitos o documento **T1 - Requisitos do Cliente**. Os requisitos descritos pelo cliente são separados das recomendações explicitamente identificadas como tais no documento. As recomendações só serão tratadas como compromisso obrigatório de entrega depois de confirmação da equipe e da professora/cliente.

## 5.5.2 Padrões de codificação

Serão adotados os seguintes padrões, sujeitos à confirmação da equipe:

- **Java:** manter compatibilidade com Java 17; usar nomes de classes em `UpperCamelCase`, métodos e atributos em `lowerCamelCase`, constantes em `UPPER_SNAKE_CASE`; declarar tipos de forma explícita; manter uma responsabilidade principal por classe; separar apresentação, coordenação das operações, regras de domínio e persistência.
- **Regras de remuneração:** manter os cálculos associados ao tipo de colaborador e evitar duplicar fórmulas em controller, templates ou JavaScript. Utilizar tipos e operações adequados a valores monetários e testar arredondamento/formatação de forma consistente.
- **HTML/Thymeleaf, CSS e JavaScript:** manter nomes compreensíveis, estrutura consistente, validação no servidor como autoridade e validação no navegador como apoio à experiência do usuário. Não depender apenas da validação do cliente para aceitar dados.
- **Documentação e testes:** usar identificadores estáveis para requisitos e casos de teste; escrever resultados esperados objetivos e reprodutíveis; atualizar documentação afetada por mudanças no comportamento.
- **Segurança dos dados:** usar dados fictícios nos testes e não versionar credenciais, dados pessoais reais ou segredos.

Uma alteração que introduza convenção diferente deverá ser justificada no pull request e aprovada na revisão técnica.

## 5.5.3 Controle de versões

O repositório Git será a fonte oficial do código e dos artefatos versionados. Para organizar o trabalho, propõe-se:

- Cada alteração deverá estar relacionada a uma issue ou tarefa do board.
- Criar branches por atividade, usando os padrões sugeridos `feature/QDS_1.1.0/<descricao>` para funcionalidades e `docs/QDS_1.1.0/<descricao>` para documentação. Ajustes de defeitos poderão usar `fix/QDS_1.1.0/<descricao>`.
- Preferir commits pequenos, com mensagens que indiquem a intenção da mudança e, quando disponível, o identificador da tarefa.
- Integrar alterações à branch principal por pull request (PR), sem desenvolver diretamente nela. Exigir pelo menos uma revisão de pessoa diferente da autora.
- Manter no PR a referência à tarefa, resumo da alteração, requisitos afetados, testes executados e evidências pertinentes. Registrar defeitos como issues, com severidade, prioridade, responsável, estado e evidência de reteste.
- Não incluir no controle de versão artefatos pessoais ou arquivos de execução que não façam parte da entrega. Conferir o diff antes do commit.

Os nomes de branches e o fluxo de PR são propostas alinhadas ao plano Kanban e devem ser confirmados pela equipe antes da adoção.

## 5.5.4 Processo de revisão técnica

Toda alteração de código que será integrada deverá passar pelo processo abaixo:

1. A pessoa autora relaciona o PR à tarefa e aos requisitos afetados, descreve a mudança e registra os testes executados.
2. Uma pessoa revisora que não implementou a alteração verifica o comportamento, a legibilidade, a coerência com a arquitetura, os efeitos sobre requisitos existentes e a adequação dos testes.
3. A revisão confere especialmente cálculos e limites, validação no servidor, unicidade da matrícula se acordada como requisito de entrega, persistência, relatórios e possíveis regressões nos três tipos de colaborador.
4. Comentários impeditivos são resolvidos e registrados no PR. A pessoa autora executa novamente os testes afetados após a correção.
5. O PR pode ser aprovado quando não houver comentários impeditivos pendentes e as verificações exigidas estiverem aprovadas. A aprovação não substitui a execução dos testes de sistema ou de aceitação planejados para a versão.

Documentos e casos de teste também deverão ser revisados por outra pessoa, verificando consistência com os requisitos do cliente e possibilidade de reprodução. A distribuição Dev 1, Dev 2 e Dev 3 do plano Kanban é uma sugestão, não uma atribuição nominal definitiva.

## 5.5.5 Critérios de qualidade

Um produto de trabalho será considerado conforme quando atender aos critérios aplicáveis:

- O requisito relacionado está identificado e seu comportamento esperado é verificável.
- O código compila e a aplicação inicia com a configuração documentada.
- As regras de remuneração e validação estão cobertas por testes apropriados.
- Não há defeitos críticos conhecidos sem correção ou decisão formal de exceção.
- Os fluxos implementados preservam as funcionalidades já verificadas e não introduzem regressões conhecidas.
- Dados persistentes continuam disponíveis após reiniciar a aplicação, conforme RNF003 e a solução de persistência aprovada para a versão.
- A interface permite concluir os fluxos previstos e apresenta erros de validação de modo compreensível; a avaliação de facilidade de uso deve ser registrada por roteiro e observação, não apenas por opinião não documentada.
- A documentação, os casos de teste, os resultados e os defeitos estão coerentes com a versão avaliada.
- A estrutura permite avaliar a inclusão de novos tipos de remuneração sem alterar os cálculos já existentes, conforme RNF004.

## 5.5.6 Métricas adotadas

As métricas abaixo serão coletadas nas revisões de qualidade e antes da entrega. Os resultados devem sempre indicar versão, data, fonte e responsável. Metas são propostas para aprovação da equipe e não representam resultados já medidos.

| Métrica | Cálculo/definição | Fonte e periodicidade | Meta proposta |
|---|---|---|---|
| Cobertura de requisitos obrigatórios | Requisitos obrigatórios com pelo menos um caso de teste executado ÷ total de requisitos obrigatórios aplicáveis × 100 | Matriz de rastreabilidade; a cada ciclo de teste e na entrega | 100%, ou exceções justificadas e aceitas |
| Taxa de aprovação dos casos executados | Casos aprovados ÷ casos executados (aprovados + reprovados) × 100 | Relatório JUnit e/ou ferramenta de gestão; por execução | 100% dos casos críticos; demais falhas devem estar registradas e avaliadas |
| Cobertura de código | Linhas cobertas ÷ linhas executáveis consideradas pela ferramenta × 100 | Relatório de cobertura automatizado; por build de validação | 80% como meta recomendada para o escopo de testes unitários, sujeita à confirmação; informar separadamente a cobertura das regras de negócio |
| Defeitos abertos por severidade | Contagem de defeitos abertos, discriminada por severidade | Issues/registro de defeitos; atualização contínua e na entrega | Zero defeito crítico aberto para aprovação da versão |
| Taxa de correção de defeitos | Defeitos corrigidos e retestados ÷ defeitos registrados no período × 100 | Issues e evidências de reteste; por ciclo | Reportar o valor e manter todos os defeitos não resolvidos com decisão e responsável |
| Pull requests revisados | PRs integrados com revisão registrada ÷ total de PRs integrados × 100 | Histórico do repositório; por marco | 100% |

Defeitos serão classificados como **críticos** quando impedirem fluxo essencial, causarem cálculo incorreto generalizado ou perda/corrupção de dados; **altos** quando bloquearem requisito importante sem alternativa aceitável; **médios/baixos** quando houver impacto limitado ou contorno viável. A equipe deverá validar essa escala e registrar a justificativa da classificação.

## 5.5.7 Critérios de aprovação da qualidade

Uma versão poderá ser recomendada para aprovação quando:

1. O build limpo e os testes automatizados definidos para a versão forem executados, com resultados e versão registrados.
2. Os critérios de aceitação obrigatórios do cliente estiverem verificados: cadastro dos três tipos, validação de dados obrigatórios, cálculo correto de cada categoria, folha individual e consolidada e total geral da folha.
3. Todos os requisitos obrigatórios aplicáveis estiverem ligados a evidências de teste; qualquer requisito não atendido tiver desvio documentado, impacto avaliado e aceite formal da autoridade definida pela equipe/cliente.
4. Não houver defeito crítico aberto. Defeitos de outra severidade deverão ter decisão explícita sobre correção, contorno ou aceite do risco.
5. As revisões técnicas dos PRs e documentos da versão estiverem registradas.
6. Os resultados de métricas, casos aprovados/reprovados/bloqueados, defeitos e limitações conhecidos estiverem apresentados sem confundir metas com resultados.

O percentual de cobertura recomendado é uma meta de acompanhamento, não substitui a verificação funcional nem autoriza declarar a versão aprovada quando critérios obrigatórios falharem. A aprovação final e eventuais exceções cabem às pessoas responsáveis definidas pela equipe e, quando aplicável, ao cliente/professora.

# 5.6 Plano de Testes

## 5.6.1 Objetivo

Este plano descreve como verificar que o Sistema de Folha de Pagamento atende aos requisitos do cliente. Os casos devem ser rastreáveis a RF001–RF009, RN001–RN008, RNF001–RNF004 e aos critérios de aceitação do documento do cliente. A execução deverá produzir resultados reproduzíveis e evidências vinculadas à versão testada.

## 5.6.2 Escopo

### Dentro do escopo

- Cadastro e consulta dos dados cadastrais e remuneratórios dos colaboradores (RF001, RF005).
- Cadastro e cálculo de colaborador padrão: salário final igual ao salário base (RF002, RF006).
- Cadastro e cálculo de colaborador comissionado: comissão igual a vendas multiplicadas pelo percentual, e salário final igual ao salário base mais comissão (RF003, RF006).
- Cadastro e cálculo de colaborador por produção: produtividade igual à quantidade produzida multiplicada pelo valor por unidade, e salário final igual ao salário base mais produtividade (RF004, RF006).
- Folha consolidada com matrícula, nome, tipo e salário final (RF007), além dos campos detalhados do relatório especificados pelo cliente: salário base, adicionais e salário final.
- Folha individual, incluída no critério de aceitação do cliente e no escopo atual do sistema.
- Soma total dos salários finais (RF008) e, se confirmados como parte da entrega, resumo com quantidade de colaboradores e totais por categoria.
- Inclusão, alteração, exclusão e consulta (RF009). O documento do cliente identifica a manutenção como extensão recomendada; a equipe deverá confirmar se é critério obrigatório desta entrega.
- Validação: nome obrigatório; salário base, vendas, percentual de comissão, quantidade produzida e valor por unidade não negativos (RN002–RN007); cálculo automático (RN008).
- Persistência dos registros (RNF003), na tecnologia aprovada para a versão. Para a implementação atual, documentada como arquivo TSV; PostgreSQL só deve ser tratado como ambiente de teste da versão se sua integração for concluída.
- Requisitos não funcionais de interface intuitiva (RNF001), validação antes do cálculo (RNF002) e extensibilidade para novos tipos de remuneração (RNF004), com métodos de avaliação definidos neste plano.

### Fora do escopo ou sujeito a confirmação

- Requisitos regulatórios, fiscais, descontos legais, autenticação/autorização, folha bancária e integrações externas não especificados no documento do cliente.
- Comportamento em produção em escala, alta disponibilidade e desempenho com grande volume, pois não há limites quantitativos definidos pelo cliente.
- Unicidade da matrícula (RN001), relatório resumido com totais por categoria e manutenção completa (RF009) são recomendações no documento de requisitos; serão testados como obrigatórios apenas se a equipe/cliente confirmar sua inclusão na entrega. Até lá, podem constar como testes recomendados.
- Inclusão real de um novo tipo de remuneração poderá ser verificada por inspeção de arquitetura ou protótipo controlado; o critério não deve exigir uma funcionalidade nova de produção sem decisão de escopo.

## 5.6.3 Estratégia de teste

Os testes partirão dos requisitos e das regras de negócio, cobrindo caminhos válidos e inválidos, valores de fronteira, cálculos independentes e fluxos completos. Para cada caso, registrar: identificador, requisito relacionado, objetivo, nível, pré-condições, dados, passos, resultado esperado, prioridade, resultado obtido, estado, versão/ambiente e evidência.

Para os cálculos, usar dados simples que permitam conferir o resultado manualmente. Por exemplo, testar valores zero e positivos para base, vendas, percentual, quantidade e valor por unidade; conferir comissão e produtividade separadamente do salário final. Incluir entradas negativas, campos obrigatórios ausentes e combinações de campos específicos de categoria incompatíveis ou incompletos, conforme as validações implementadas e os requisitos aprovados.

Testes de regressão deverão repetir os casos dos cálculos, validações, persistência e relatórios após alterações que possam afetá-los. Casos manuais de usabilidade deverão seguir tarefas definidas (cadastrar cada categoria, corrigir dados inválidos, consultar e emitir relatórios) e registrar observações e dificuldades encontradas.

## 5.6.4 Níveis de teste

| Nível | Objetivo e exemplos | Execução/ferramenta | Critério de aprovação |
|---|---|---|---|
| Unitário | Verificar regras isoladas: fórmulas de cada tipo, valores de fronteira, validações, conversões de formulário e cálculos do resumo | JUnit 5 via `spring-boot-starter-test` e Maven Wrapper; o `pom.xml` já declara Spring Boot Test | Resultado real igual ao esperado; testes independentes, determinísticos e sem depender de dados externos |
| Integração | Verificar interação entre serviço e repositório, persistência, atualização/exclusão, unicidade se adotada, leitura após reinício e tratamento de erros | JUnit/Spring Boot Test; repositório isolado e dados preparados por teste. Usar TSV na versão atual ou PostgreSQL de teste se a migração for concluída | Operações persistem e são recuperadas corretamente; restrições e erros esperados são observados; estado do teste não contamina outros testes |
| Sistema | Verificar a aplicação como um todo: cadastro, consulta, folha individual/consolidada, totais, validação e persistência em execução | Aplicação executada localmente com JDK 17 ou superior e Maven Wrapper; navegador suportado escolhido pela equipe | Fluxos completos produzem dados e resultados coerentes com requisitos e interface; nenhuma falha crítica impede o uso |
| Aceitação | Confirmar com representante do cliente/professora os critérios de aceitação e os fluxos principais | Casos revisados e registrados em ferramenta de gestão aprovada pela equipe (o Kanban sugere Kimi TCMS) ou em planilha versionada | Cada critério obrigatório tem resultado aprovado ou desvio formalmente aceito; evidência vinculada à versão testada |

Os testes automatizados e manuais deverão ser identificados separadamente. A ferramenta de gestão de casos deverá ser confirmada pela equipe; o uso de Kimi TCMS é uma proposta do plano Kanban, não uma dependência técnica da aplicação.

## 5.6.5 Ambiente de teste

- **Sistema e runtime:** Windows conforme ambiente de desenvolvimento do projeto; JDK 17 ou superior.
- **Build e execução:** Maven Wrapper incluído no repositório (`mvnw.cmd` no Windows); comandos de referência: `. mvnw.cmd test` e `. mvnw.cmd clean package`.
- **Aplicação:** Spring Boot 3.5.5, Java 17, Thymeleaf, HTML, CSS e JavaScript conforme dependências/configuração do projeto.
- **Dados:** utilizar somente dados fictícios. Preparar o estado inicial de cada teste e limpar ou isolar dados ao final.
- **Persistência:** testar o TSV na configuração correspondente à versão atual. Caso PostgreSQL seja integrado para a entrega, usar banco exclusivo de teste, com configuração reproduzível e sem credenciais versionadas; nunca executar testes destrutivos contra dados reais.
- **Navegador:** definir e registrar a versão do navegador utilizada nos testes de sistema/aceitação. Testes de lógica não devem depender de navegador.
- **Registro:** guardar versão/commit, comando ou procedimento, ambiente, data, executor, resultado, logs/relatórios e capturas ou outros artefatos pertinentes.

## 5.6.6 Ferramentas

| Ferramenta | Uso previsto | Situação/observação |
|---|---|---|
| Git e GitHub (ou hospedagem equivalente aprovada) | Versionamento, issues, PRs, revisão e rastreabilidade das alterações | Fluxo proposto no plano Kanban; confirmar com a equipe |
| Maven Wrapper | Build e execução da suíte (`test`, `clean package`) | Incluído no repositório |
| JUnit / Spring Boot Test | Testes unitários e de integração | Dependência de teste já declarada; ainda é necessário criar e executar os testes |
| Ferramenta de cobertura, por exemplo JaCoCo | Medir cobertura e publicar relatório | Adicionar/configurar se a equipe aprovar a meta de cobertura; não está declarada atualmente no `pom.xml` |
| Kimi TCMS ou planilha versionada | Organizar casos e registrar execução/evidências de aceitação | Kimi TCMS é sugestão do Kanban; confirmar disponibilidade e formato de exportação |
| Navegador web | Testes manuais de sistema e interface | Registrar nome e versão em cada execução |

## 5.6.7 Critérios de entrada e saída

### Entrada para iniciar uma execução

- Requisitos e casos de teste revisados, com resultados esperados objetivos.
- Versão/candidato a teste identificado por commit ou etiqueta.
- Build concluído e aplicação inicializável no ambiente previsto.
- Ambiente e dados fictícios preparados; banco de teste acessível, quando aplicável.
- Testadores sabem como registrar resultado, evidência e defeito.

Se um pré-requisito não estiver atendido, registrar a execução como **bloqueada**, indicando o motivo; não registrar como aprovada.

### Saída de uma execução/ciclo

- Casos executados com estado **aprovado**, **reprovado** ou **bloqueado**, executor e evidência.
- Defeitos encontrados registrados com requisito/caso relacionado, severidade, prioridade, estado e responsável.
- Casos reprovados retestados após correção; regressões relevantes reexecutadas.
- Métricas calculadas com fonte, versão e data.
- Requisitos sem teste, testes não executados e limitações explicitados no relatório.

## 5.6.8 Critérios de aprovação dos testes

Um caso individual será **aprovado** somente se o resultado observado corresponder ao resultado esperado e houver evidência suficiente. Será **reprovado** quando houver divergência reproduzível. Será **bloqueado** quando não puder ser executado por dependência ou condição de ambiente não disponível. Bloqueado não equivale a aprovado.

O ciclo de aceitação da versão será recomendado para aprovação quando:

- Todos os critérios de aceitação obrigatórios do cliente passarem: cadastro dos três tipos, validação de dados obrigatórios, cálculo de cada categoria, emissão de folha individual e consolidada e exibição do total geral.
- Os casos de requisitos obrigatórios estiverem executados; a meta de cobertura de requisitos é 100%, exceto desvios documentados e formalmente aceitos.
- Todos os casos críticos passarem e não houver defeito crítico aberto. Defeitos de menor severidade deverão ter decisão registrada.
- Build, testes unitários, testes de integração e testes de sistema definidos para a versão tiverem resultado registrado.
- Resultados de aceitação, defeitos, limitações e evidências forem rastreáveis à versão testada.
- Recomendações do cliente (como matrícula única, manutenção completa e resumo por categoria) estiverem identificadas como incluídas ou não incluídas, conforme decisão documentada de escopo.

A cobertura de código de 80% é uma meta recomendada no plano de trabalho, sujeita à aprovação da equipe; não é, por si só, critério suficiente para declarar conformidade. O relatório final deve distinguir requisitos obrigatórios, recomendações adotadas e recomendações fora do escopo.

## 5.6.9 Rastreabilidade inicial dos requisitos

A tabela abaixo orienta a criação dos casos. A equipe deverá substituir a coluna final pelos identificadores dos casos elaborados e manter o estado atualizado após a execução.

| Requisito do cliente | Verificação mínima planejada | Casos |
|---|---|---|
| RF001 e RF002 | Cadastrar colaborador padrão com dados válidos e consultar os dados cadastrais | A definir |
| RF003 e RF006 (comissionado) | Informar base, vendas e percentual; conferir comissão e salário final | A definir |
| RF004 e RF006 (produção) | Informar base, quantidade e valor por unidade; conferir produtividade e salário final | A definir |
| RF005 | Listar colaboradores e conferir dados cadastrais e remuneratórios apresentados | A definir |
| RF007 | Conferir matrícula, nome, tipo e salário final na folha consolidada; verificar detalhamento exigido no relatório | A definir |
| RF008 | Conferir soma de salários finais, inclusive lista vazia e mais de um colaborador | A definir |
| RF009 (recomendado; confirmar escopo) | Incluir, alterar, excluir e consultar; verificar estado após cada operação | A definir |
| RN001 (recomendado; confirmar escopo) | Tentar incluir matrícula repetida e verificar o comportamento acordado | A definir |
| RN002–RN007 | Testar nome ausente e valores negativos para cada campo numérico aplicável | A definir |
| RN008 e RNF002 | Confirmar cálculo automático e rejeição/indicação de entradas inválidas antes do resultado | A definir |
| RNF001 | Executar roteiro de tarefas de interface e registrar sucesso, erros percebidos e dificuldades | A definir |
| RNF003 | Gravar dados, reiniciar a aplicação e confirmar leitura dos registros | A definir |
| RNF004 | Revisar desenho do domínio ou demonstrar inclusão isolada de novo tipo sem alterar cálculos existentes | A definir |
| Critério de aceitação: três categorias e cálculos corretos | Executar ao menos um caso válido e casos de fronteira por categoria | A definir |
| Critério de aceitação: folha individual, consolidada e total geral | Conferir dados, adicionais, salários finais e agregação usando conjunto conhecido | A definir |

## 5.6.10 Referências

- **T1 - Requisitos do Cliente**, fornecido para o trabalho: objetivo, escopo, RF001–RF009, RN001–RN008, RNF001–RNF004, relatórios, casos de uso e critérios de aceitação.
- **PLANO_KANBAN_QDS_1.1.0.md**, como fonte de propostas de fluxo de trabalho, responsabilidades, ferramentas e metas de acompanhamento.
- **README.md** e `pom.xml`, como fontes do ambiente e das dependências atualmente declaradas. As ferramentas e versões devem ser atualizadas se a configuração do projeto mudar.