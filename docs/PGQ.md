# 6. PLANO DE GARANTIA DA QUALIDADE

**Projeto:** Folha de Pagamento | **Versão:** QDS_1.2.0 (Revisão Pós-Feedback A1)  
**Responsável pelo PGQ e Coleta de Métricas:** Ana Luísa da Silva (QA / Gestão de Qualidade)  
**Revisora Técnica e Guardiã de PRs:** Amanda Ruwer (Líder Técnica / Revisora)  
**Responsável por Arquitetura e Testes de Domínio:** Rafaela Acosta (Desenvolvedora Back-end / Arquitetura)  
**Dependência:** KAN-01 | **Branch:** docs/QDS_1.1.0/pgq-revisoes  

*Este documento estabelece a estrutura obrigatória do Anexo I complementada pelas fórmulas de medição, escopo técnico de cobertura, critérios operacionais de severidade e governança nominal de qualidade da equipe.*

---

### 6.1. Padrões de Desenvolvimento
A arquitetura mantém o padrão **MVC em camadas**, isolando responsabilidades entre:
* **Domínio (`model`):** `Colaborador` (abstrata), `ColaboradorPadrao`, `ColaboradorComissionado`, `ColaboradorProducao` e `ResumoFolha`.
* **Apresentação e Entrada (`controller` / `dto`):** `FolhaPagamentoController`, `ColaboradorForm` e `FormatadorMoeda`.
* **Serviço e Regras de Aplicação (`service`):** `FolhaPagamentoService`.
* **Persistência (`repository`):** `RepositorioColaboradores` (classe concreta TSV atual e interface JPA alvo).
* **Configuração (`config`):** `FolhaPagamentoConfig`.

**Convenções e Boas Práticas Obrigatórias:**
* **Nomenclatura e Tipagem:** Uso de `PascalCase` para classes e `camelCase` para métodos e atributos. Cálculos monetários e percentuais devem utilizar `BigDecimal` (ou arredondamento padronizado em 2 casas decimais) para evitar imprecisão de ponto flutuante.
* **Encapsulamento e Coesão:** Restringir a visibilidade de atributos (`private`), utilizar `final` em dependências injetadas, validar entradas nulas/negativas no construtor ou formulário e tratar exceções de leitura/escrita de forma explícita.
* **Gerência de Build (`pom.xml`):** Qualquer alteração de dependência exige validação prévia via `./mvnw clean test` antes da abertura do Pull Request.

---

### 6.2. Controle de Versão
O código-fonte é versionado no Git/GitHub sob proteção da branch principal (`main`), sendo vedados commits diretos.
* **Padrão de Branches:**
  * `feat/QDS_1.1.0/<id>-<descricao>`: novas funcionalidades ou evolução arquitetural.
  * `fix/QDS_1.1.0/<id>-<descricao>`: correção de defeitos registrados em Issues.
  * `docs/QDS_1.1.0/<descricao>`: criação e atualização de artefatos de documentação.
  * `test/QDS_1.1.0/<descricao>`: implementação ou refatoração de testes automatizados.
* **Padrão de Commits Semânticos:** `<tipo>(<escopo>): <descrição objetiva> [KAN-XX]`  
  *Exemplo:* `test(service): adiciona casos de teste de borda para ColaboradorComissionado [KAN-05]`

---

### 6.3. Revisões Técnicas (Processo de Pull Request)
Toda entrega passa por inspeção técnica cruzada antes do *merge* na branch `main`.
* **Papéis Nominais no Fluxo:** A **Autora** (**Rafaela Acosta** ou **Ana Luísa da Silva**) executa a suíte local de testes e abre o PR preenchendo as evidências; a **Revisora Técnica** (**Amanda Ruwer**) inspeciona o código e emite o parecer (*Approved*, *Changes Requested* ou *Rejected*). Excepcionalmente, quando **Amanda Ruwer** for a autora do PR, a revisão técnica será conduzida por **Rafaela Acosta**.

**Checklist Obrigatório de Pull Request:**
- [ ] Issue/Requisito (RF/RNF) vinculado na descrição do PR.
- [ ] Código aderente ao padrão MVC, sem duplicação de regras de negócio.
- [ ] Validações de entrada (`ColaboradorForm` / domínio) e tratamento de exceções verificados.
- [ ] Build e suíte de testes (`./mvnw clean verify`) executados com 100% de aprovação.
- [ ] Relatório de cobertura JaCoCo verificado sem queda abaixo da meta de 80%.
- [ ] Documentação e diagramas UML atualizados caso haja mudança estrutural.
- [ ] Zero defeitos críticos (S1) ou altos (S2) abertos vinculados à funcionalidade.

---

### 6.4. Métricas de Qualidade (Fórmulas, Escopo, Ferramental e Governança)

#### Tabela Resumo (Padrão Anexo I)
| Métrica | Meta |
| :--- | :--- |
| Cobertura | $\ge 80\%$ |
| Defeitos Críticos | $0$ |
| Requisitos Atendidos | $100\%$ |

#### Detalhamento Matemático e Operacional das Métricas

| Métrica | Fórmula Matemática e Denominador | Escopo Técnico e Ferramenta de Medição | Frequência de Coleta | Responsável Nominal pela Coleta e Auditoria |
| :--- | :--- | :--- | :--- | :--- |
| **1. Cobertura de Testes (Instruções/Linhas)** | $$\text{Cobertura (\%)} = \left( \frac{\text{Linhas Executadas pelos Testes}}{\text{Total de Linhas Executáveis no Escopo}} \right) \times 100$$ | **Ferramenta:** Plugin **JaCoCo** (`jacoco-maven-plugin` 0.8.12) integrado ao `pom.xml`, acionado via `./mvnw clean test jacoco:report` (relatório gerado em `target/site/jacoco/index.html`).<br>**Escopo Incluído (Denominador):** Classes de negócio, domínio, serviço, formatação e persistência (`Colaborador*`, `FolhaPagamentoService`, `ResumoFolha`, `FormatadorMoeda`, `ColaboradorForm`, `RepositorioColaboradores`).<br>**Escopo Excluído:** Método `main` (`FolhaPagamentoApplication`) e classes puras de configuração do framework (`FolhaPagamentoConfig`). | A cada Pull Request aberto e no fechamento das entregas A1/A3. | **Coleta:** Rafaela Acosta e Ana Luísa da Silva.<br>**Auditoria:** Ana Luísa da Silva (QA). |
| **2. Defeitos Críticos Abertos** | $$\text{Defeitos Críticos} = \sum \text{Issues com label } \texttt{S1-Critico} \text{ e status } \neq (\texttt{Validado}, \texttt{Fechado})$$ | **Ferramenta:** GitHub Issues filtradas pela *label* `S1-Critico` na milestone da versão ativa (`QDS_1.1.0` / `A3`).<br>**Escopo:** Todo o sistema (cálculo de folha, persistência TSV/SQL e renderização web). | Contínua (triagem diária em período de testes) e bloqueante antes de cada *release*. | **Coleta e Triagem:** Ana Luísa da Silva (QA).<br>**Validação de Fechamento:** Amanda Ruwer (Revisora). |
| **3. Requisitos Funcionais Atendidos** | $$\text{Requisitos (\%)} = \left( \frac{\text{RFs Implementados e Aprovados em Casos de Teste}}{\text{Total de RFs Comprometidos no Escopo (ERS)}} \right) \times 100$$ | **Ferramenta:** Matriz de Rastreabilidade (`RFxx` $\leftrightarrow$ `UCxx` $\leftrightarrow$ Classe Java $\leftrightarrow$ `CTxx` executado com status *Aprovado*).<br>**Denominador:** Total de Requisitos Funcionais listados na ERS do projeto. | Ao final de cada ciclo de desenvolvimento (checkpoints semanais) e na homologação final. | **Coleta:** Ana Luísa da Silva (QA).<br>**Aprovação:** Amanda Ruwer e Rafaela Acosta. |
| **4. Índice de Revisão de PRs** | $$\text{PRs Revisados (\%)} = \left( \frac{\text{PRs integrados na } \texttt{main} \text{ com revisão aprovada}}{\text{Total de PRs integrados na branch } \texttt{main}} \right) \times 100$$ | **Ferramenta:** Histórico de *Pull Requests* do GitHub (`is:pr is:merged`). Meta: **100%**. | Semanal e no fechamento da entrega. | **Auditoria:** Amanda Ruwer (Líder Técnica). |
| **5. Taxa de Execução e Sucesso de Testes** | $$\text{Execução (\%)} = \left( \frac{\text{Casos de Teste Executados}}{\text{Total de Casos de Teste Planejados}} \right) \times 100$$ | **Ferramenta:** Relatório do **Maven Surefire Plugin** (`target/surefire-reports`) combinado com a planilha/documento de Casos de Teste manuais e automatizados (JUnit 5). Meta: **100% executados**. | A cada execução de bateria de testes de regressão. | **Coleta:** Rafaela Acosta e Ana Luísa da Silva. |

---

### 6.5. Gestão de Defeitos e Critérios Operacionais de Severidade

Todo defeito identificado durante inspeções de código ou execução de testes (JUnit / Sistema) deve ser registrado no **GitHub Issues** contendo: Título padronizado (`[BUG-XX] Descrição curta`), Requisito afetado (`RFxx`), Passos para reprodução, Resultado Esperado, Resultado Observado, Evidência (log/print) e Rótulo de Severidade.

#### 6.5.1. Matriz Operacional de Severidade de Defeitos

| Nível de Severidade | Critério Operacional de Classificação | Exemplo Prático no Domínio (Folha de Pagamento) | Impacto na Entrega e SLA de Correção |
| :--- | :--- | :--- | :--- |
| **Crítica (S1)** | Falha que impede a execução do sistema (crash/exceção não tratada), corrompe ou perde dados persistidos, ou gera erro aritmético no cálculo do salário final, comissão ou produção sem contorno possível. | • `ColaboradorComissionado` calculando comissão sem somar o `salarioBase` herdado.<br>• Perda ou truncamento de linhas ao salvar arquivo `.tsv` ou falha grave na tabela PostgreSQL.<br>• Erro `500 Internal Server Error` ao abrir a listagem ou cadastrar colaborador válido. | **Bloqueia imediatamente o PR e a entrega.** Correção obrigatória antes da homologação (Meta na entrega = **0**). |
| **Alta (S2)** | Funcionalidade principal apresenta comportamento incorreto ou validação importante falha, mas não há corrupção da base de dados existente ou existe uma alternativa operacional temporária (*workaround*) complexa. | • Sistema permite cadastrar dois colaboradores com a mesma `matricula` sem exibir mensagem de erro amigável.<br>• Filtro ou consulta por matrícula retorna lista vazia mesmo com registro existente, embora a listagem geral funcione.<br>• `ResumoFolha` exibe os salários individuais corretos, mas não atualiza um dos subtotais parciais na tela. | **Bloqueia o merge do PR afetado.** Deve ser corrigido no mesmo ciclo de desenvolvimento, salvo justificativa técnica documentada e aprovada por Amanda Ruwer, Rafaela Acosta e Ana Luísa da Silva. |
| **Média (S3)** | Comportamento inadequado em fluxo secundário, validação de borda não crítica ou formatação de saída incorreta que não altera os valores matemáticos calculados pelo domínio. | • `FormatadorMoeda` exibe valor sem o espaço padrão após `R\$` ou com separador de milhar ausente na tela de resumo, mas o valor numérico está exato.<br>• Mensagem de erro de formulário em inglês ou pouco descritiva ao inserir campo em branco.<br>• Redirecionamento após edição leva para a tela inicial em vez de voltar direto para a lista de colaboradores. | **Não bloqueia o build principal**, mas deve ter Issue vinculada com correção programada antes da entrega final A3. |
| **Baixa (S4)** | Problema puramente cosmético/visual na interface (HTML/CSS), desalinhamento de layout, erro ortográfico em rótulos estáticos ou melhoria de legibilidade de código (*code smell*) sem impacto funcional. | • Botão "Salvar Colaborador" desalinhado em resoluções menores no template Thymeleaf.<br>• Erro de digitação em um cabeçalho da tabela HTML (ex: "Colaboradore" em vez de "Colaboradores").<br>• Variável interna com nome pouco expressivo ou import não utilizado em classe de teste. | **Não bloqueia PR nem entrega.** Correção oportuna (baixo esforço) ou alocada no backlog de melhoria contínua. |

#### 6.5.2. Ciclo de Vida do Defeito (Estados e Transições)
`Aberto` ➔ `Em Análise` ➔ `Em Correção` ➔ `Em Revisão` ➔ `Resolvido` ➔ `Validado` ➔ `Fechado`

1. **Aberto:** Defeito registrado na Issue pela testadora (**Ana Luísa da Silva** ou **Amanda Ruwer**).
2. **Em Análise:** Triagem realizada por **Ana Luísa da Silva** em conjunto com **Amanda Ruwer** para atribuir a severidade (`S1` a `S4`) e designar a desenvolvedora responsável.
3. **Em Correção:** Desenvolvedora atribuída (**Rafaela Acosta**) cria a branch `fix/QDS_1.1.0/bug-xx` e implementa a correção juntamente com um teste unitário (JUnit 5) que reproduz a falha.
4. **Em Revisão:** Pull Request aberto aguardando inspeção técnica da Revisora (**Amanda Ruwer**).
5. **Resolvido:** PR aprovado e integrado à branch principal, aguardando reteste no ambiente integrado.
6. **Validado:** Testadora (**Ana Luísa da Silva**) reexecuta o Caso de Teste correspondente e confirma a eliminação do defeito sem regressão. *(Caso o reteste falhe, a Issue retorna imediatamente para `Em Correção`)*.
7. **Fechado:** Issue encerrada no GitHub com o link do PR e a evidência do teste anexada.

---

### 6.6. Critérios Objetivos de Aprovação da Entrega (Gate de Qualidade)
A versão do sistema só será considerada homologada e apta para entrega acadêmica (A1/A3) quando cumprir simultaneamente:
1. **Requisitos:** 100% dos requisitos funcionais do escopo implementados e rastreados com casos de teste aprovados.
2. **Cobertura:** Relatório do plugin **JaCoCo** comprovando cobertura $\ge 80\%$ nas classes de escopo.
3. **Severidades Bloqueantes:** **0 (zero)** defeitos Críticos (`S1`) e **0 (zero)** defeitos Altos (`S2`) abertos sem tratamento.
4. **Revisão por Pares:** 100% dos Pull Requests integrados na branch `main` contendo o checklist preenchido e aprovação registrada pela revisora designada (**Amanda Ruwer**).