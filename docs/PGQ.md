# Plano de Gestão da Qualidade (PGQ) e Processo de Revisão Técnica

**Projeto:** Folha de Pagamento
**Versão:** QDS_1.1.0
**Responsável sugerido:** Dev 3
**Revisor sugerido:** Dev 1
**Branch:** docs/QDS_1.1.0/pgq-revisoes
**Dependência:** KAN-01
**Status:** Proposta para revisão e concordância da equipe

---

## 1. Objetivo
Definir práticas de gestão da qualidade para a versão QDS_1.1.0 do projeto Folha de Pagamento, estabelecendo padrões de codificação e organização, controle de versão, revisão técnica, gestão de defeitos, métricas, evidências e critérios de aprovação.

Este documento define metas e procedimentos a serem aplicados e medidos. Não representa resultados já obtidos nem declara que testes ou revisões foram executados.

## 2. Escopo
O PGQ aplica-se ao código-fonte e à configuração do projeto, incluindo `pom.xml`, a estrutura `src/main/` e os artefatos de acompanhamento no GitHub Issues, Projects e Pull Requests.

Considerando a estrutura atual do projeto, são referências relevantes as classes de domínio de colaboradores, `ColaboradorForm`, `FolhaPagamentoController`, `FolhaPagamentoService`, `RepositorioColaboradores`, `ResumoFolha`, `FormatadorMoeda` e `FolhaPagamentoConfig`.

## 3. Papéis e responsabilidades
* **Dev 3 - responsável sugerido pelo PGQ:** preparar e manter este documento, verificar sua completude e organizar as evidências da atividade.
* **Dev 1 - revisor sugerido:** revisar o documento e os PRs que lhe forem atribuídos, registrar comentários e confirmar se os critérios de revisão foram atendidos.
* **Desenvolvedor autor da alteração:** seguir os padrões acordados, executar os testes pertinentes, descrever a alteração no PR e corrigir as pendências apontadas.
* **Equipe:** revisar e concordar com os critérios de qualidade antes do início do desenvolvimento A3, além de registrar exceções e decisões relevantes.

*A atribuição acima é a sugerida na tarefa e deve ser confirmada pela equipe. Quem aprova cada PR deve ser definido conforme as regras do repositório e o acordo da equipe.*

## 4. Padrões de codificação e organização

### 4.1. Organização
Manter a separação de responsabilidades já adotada pelo projeto:
* **Domínio:** classes que representam colaboradores e regras de remuneração.
* **Formulário e validação de entrada:** `ColaboradorForm`.
* **Controlador web:** `FolhaPagamentoController`.
* **Serviço e regras de aplicação:** `FolhaPagamentoService`.
* **Persistência de colaboradores:** `RepositorioColaboradores`.
* **Configuração:** `FolhaPagamentoConfig` e arquivos de configuração pertinentes.
* **Resumo e formatação:** `ResumoFolha` e `FormatadorMoeda`.

Novas alterações devem respeitar a organização existente, evitar duplicação de regras e manter cada classe focada em sua responsabilidade.

### 4.2. Convenções de código
* Seguir as convenções Java utilizadas no projeto.
* Usar PascalCase para nomes de classes e camelCase para métodos e atributos.
* Preferir nomes descritivos e consistentes com o idioma e os nomes já utilizados no código.
* Restringir a visibilidade de atributos e métodos ao necessário.
* Utilizar `final` quando adequado para indicar que um atributo ou referência não será reatribuído.
* Manter métodos coesos, com responsabilidades claras.
* Validar dados de entrada e preservar as regras de domínio.
* Tratar exceções de forma explícita, sem ignorar erros relevantes.
* Evitar duplicação, código não utilizado e comentários desatualizados.
* Atualizar a documentação quando uma mudança alterar o comportamento ou o uso do sistema.

### 4.3. Configuração e dependências
Mudanças em `pom.xml` devem ter justificativa técnica. Antes da integração, verificar se o projeto compila e se os testes pertinentes passam, utilizando os comandos e plugins compatíveis com a configuração real do repositório.

Não presumir que uma ferramenta de cobertura ou uma pipeline automatizada já esteja configurada; confirmar isso no projeto.

## 5. Controle de versão: branches e commits

### 5.1. Branches
Para esta atividade, utilizar a branch informada na tarefa: `docs/QDS_1.1.0/pgq-revisoes`.

Como convenção proposta para o trabalho:
* `main`: linha principal do projeto, conforme as regras existentes do repositório.
* `feat/QDS_1.1.0/<id>-<descricao>`: implementação de funcionalidade.
* `fix/QDS_1.1.0/<id>-<descricao>`: correção de defeito.
* `docs/QDS_1.1.0/<descricao>`: documentação.

Trabalhar em branches específicas e integrar alterações por Pull Request, evitando commits diretos na branch principal, salvo se a equipe tiver uma regra diferente formalmente acordada.

### 5.2. Commits
Os commits devem ser pequenos, objetivos e relacionados a uma mudança coerente. Padrão proposto:
`<tipo>: <descrição objetiva> [KAN-XX]`

Tipos sugeridos:
* `feat`: nova funcionalidade.
* `fix`: correção de defeito.
* `docs`: documentação.
* `test`: testes.
* `refactor`: reorganização interna sem mudança intencional de comportamento.
* `chore`: manutenção ou configuração.

Exemplos:
* `docs: adiciona plano de gestão da qualidade [KAN-01]`
* `test: adiciona testes para cálculo da folha [KAN-05]`
* `fix: corrige validação de colaborador [KAN-08]`

Quando aplicável, referenciar a Issue correspondente na descrição do PR ou do commit, conforme as práticas do repositório.

## 6. Processo de revisão técnica por Pull Request

### 6.1. Preparação do PR
O autor deverá:
* Criar ou utilizar uma branch de trabalho adequada.
* Implementar a alteração relacionada à Issue ou ao requisito.
* Executar os testes e verificações pertinentes.
* Atualizar a documentação afetada.
* Abrir um PR com objetivo, resumo das alterações, referência à Issue, testes executados e evidências disponíveis.

### 6.2. Inspeção pelo revisor
O revisor deverá verificar:
* Se a alteração atende ao requisito descrito.
* Se o código segue os padrões deste PGQ.
* Se as responsabilidades das classes e a organização do projeto foram preservadas.
* Se as validações de entrada e as regras de negócio continuam consistentes.
* Se exceções e erros são tratados adequadamente.
* Se existem riscos de regressão ou impactos em funcionalidades relacionadas.
* Se os testes pertinentes foram executados e seus resultados registrados.
* Se a documentação foi atualizada quando necessário.
* Se existem defeitos críticos abertos relacionados à entrega.
* Se o PR contém informações suficientes para rastrear a alteração.

### 6.3. Resultado da revisão
O PR poderá ser:
* **Aprovado:** atende aos critérios de revisão e não possui pendências bloqueadoras.
* **Solicitada alteração:** precisa de correções ou esclarecimentos antes da aprovação.
* **Rejeitado/fechado:** a alteração não será integrada, com a justificativa registrada.

Após as correções, o autor deverá atualizar o PR e solicitar nova revisão. A integração deverá seguir as permissões e regras configuradas no repositório.

### 6.4. Checklist de PR
- [ ] Requisito ou Issue relacionado identificado.
- [ ] Alteração descrita com clareza.
- [ ] Padrões de codificação respeitados.
- [ ] Regras de negócio e validações verificadas.
- [ ] Testes pertinentes executados.
- [ ] Resultados e evidências registrados.
- [ ] Documentação atualizada, quando aplicável.
- [ ] Pendências bloqueadoras resolvidas.
- [ ] Aprovação registrada antes da integração.

## 7. Fluxo de gestão de defeitos

Defeitos devem ser registrados no GitHub Issues. Cada Issue deverá conter, quando aplicável: título objetivo, descrição, passos para reprodução, resultado esperado, resultado observado, versão ou contexto, severidade, responsável, estado e evidências.

### 7.1. Estados
Fluxo proposto:
`Aberto` ➔ `Em análise` ➔ `Em correção` ➔ `Em revisão` ➔ `Resolvido` ➔ `Validado` ➔ `Fechado`

* **Aberto:** defeito registrado e ainda não analisado.
* **Em análise:** impacto, severidade e forma de correção estão sendo avaliados.
* **Em correção:** um responsável está trabalhando na solução.
* **Em revisão:** a correção está submetida à revisão técnica (Pull Request).
* **Resolvido:** a correção foi implementada, aguardando validação.
* **Validado:** a verificação confirmou que o defeito foi corrigido.
* **Fechado:** registro concluído com evidência ou justificativa.

Se a validação falhar, a Issue retorna para Em correção. Os nomes finais dos estados podem ser adaptados aos estados disponíveis no GitHub Projects, preservando o significado e a rastreabilidade do fluxo.

### 7.2. Responsabilidades
* **Responsável pela triagem:** classificar severidade, impacto e prioridade; deve ser definido pela equipe.
* **Responsável pela correção:** desenvolvedor atribuído à Issue.
* **Revisor:** verificar a correção por PR.
* **Responsável pela validação:** integrante designado para confirmar o resultado, preferencialmente alguém diferente do autor quando viável.

Defeitos críticos devem receber prioridade máxima e bloquear a aprovação da entrega enquanto estiverem abertos. Toda exceção ao fluxo deve ser justificada e registrada.

### 7.3. Severidade
* **Crítica (S1):** falha grave em função essencial, risco relevante à integridade dos dados ou indisponibilidade significativa.
* **Alta (S2):** comprometimento importante de funcionalidade sem alternativa aceitável.
* **Média (S3):** impacto limitado ou existência de alternativa temporária.
* **Baixa (S4):** impacto reduzido, como problema visual não bloqueador ou melhoria menor.

A severidade deve refletir o impacto observado, não apenas a dificuldade de correção.

## 8. Métricas de qualidade

As metas abaixo são objetivos propostos para a versão. Os resultados devem ser medidos e reportados; não se deve declarar uma meta como atingida sem evidência.

| Métrica | Meta ou critério | Método de medição e evidência |
| :--- | :--- | :--- |
| **Cobertura de testes** | Mínimo recomendado de **80%** | Gerar relatório com ferramenta compatível com o projeto, registrar o escopo medido e anexar ou vincular o relatório |
| **Defeitos críticos abertos** | **0 (Zero)** na aprovação da entrega | Consultar Issues abertas classificadas como críticas no escopo da versão |
| **Requisitos atendidos** | **100%**, ou exceções formalmente justificadas e aprovadas | Comparar requisitos com evidências de implementação e validação em uma matriz de rastreabilidade |
| **PRs integrados com revisão** | **100%** das alterações integradas sujeitas ao processo acordado | Conferir histórico de PRs e aprovações |
| **Testes obrigatórios executados** | **100%** dos testes definidos como obrigatórios para a entrega | Conferir relatórios de execução e registro no PR |

### 8.1. Definição e reporte das métricas
Antes de medir, a equipe deverá definir o escopo de cada métrica e as ferramentas disponíveis. Para cobertura, confirmar se há ferramenta configurada no `pom.xml`; caso contrário, avaliar e configurar uma ferramenta compatível antes de reportar o percentual.

O reporte deverá registrar:
* versão e período avaliados;
* nome da métrica e método de cálculo;
* meta definida;
* resultado observado;
* fonte da evidência;
* divergências em relação à meta;
* ações corretivas, responsáveis e prazos, quando aplicáveis.

A cobertura deverá ser interpretada junto com a qualidade dos testes e a análise técnica, pois cobertura isolada não comprova ausência de defeitos.

## 9. Evidências e rastreabilidade
Manter as evidências nos artefatos do projeto, preferencialmente vinculadas às Issues e aos PRs correspondentes:
* histórico de branches e commits;
* PRs com descrição, checklist, comentários e aprovação;
* relatórios de compilação e testes;
* relatório de cobertura, quando disponível;
* Issues com severidade, responsável, estado e histórico;
* matriz de rastreabilidade entre requisitos, implementação e validação;
* exceções documentadas, com justificativa, responsável pela aprovação e impacto;
* registro de concordância da equipe com o PGQ.

As evidências devem permitir identificar o que foi verificado, por quem, em qual versão e qual foi o resultado. Não registrar verificações como concluídas antes de executá-las.

## 10. Critérios de aprovação da entrega
Uma entrega poderá ser aprovada quando:
* Os requisitos aplicáveis estiverem atendidos ou as exceções estiverem formalmente justificadas e aprovadas.
* Não houver defeitos críticos abertos relacionados à entrega.
* Os testes obrigatórios definidos para a entrega tiverem sido executados, com resultados registrados.
* As métricas tiverem sido medidas e reportadas.
* A meta de cobertura recomendada de 80% tiver sido avaliada; desvios deverão ser documentados e tratados conforme decisão registrada da equipe.
* O PR tiver revisão e aprovação registradas conforme o processo acordado.
* As evidências de inspeção, testes e validação estiverem disponíveis.
* A alteração respeitar os padrões de organização e codificação definidos neste PGQ.

A aprovação não deverá ser baseada apenas na ausência de erros aparentes: deverá considerar requisitos, testes, revisão e evidências.

## 11. Aprovação e manutenção do PGQ
Este documento deverá ser revisado pelo responsável sugerido e pelo revisor indicado, e apresentado à equipe para concordância antes do desenvolvimento A3.

A concordância deverá ser registrada no GitHub, por exemplo, em um PR, Issue ou ata vinculada ao projeto. Alterações posteriores no PGQ deverão incluir justificativa e histórico de revisão.

* **Responsável sugerido:** Dev 3 *(pendente de confirmação)*
* **Revisor sugerido:** Dev 1 *(revisão pendente)*
* **Concordância da equipe:** *(pendente de registro)*

## 12. Referências do projeto
* `pom.xml`
* `src/main/`
* GitHub Issues
* GitHub Projects
* GitHub Pull Requests
* Código-fonte Java do projeto Folha de Pagamento

**Prazo interno informado na tarefa:** 12/10/2026.