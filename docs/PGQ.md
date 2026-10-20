# 6. PLANO DE GARANTIA DA QUALIDADE

**Projeto:** Folha de Pagamento | **Versão:** QDS_1.1.0
**Responsável sugerido:** Dev 3 | **Revisor sugerido:** Dev 1
**Dependência:** KAN-01 | **Branch:** docs/QDS_1.1.0/pgq-revisoes

*Este documento define as metas e procedimentos de qualidade, servindo como estrutura mínima obrigatória (conforme Anexo I) complementada pelas regras de fluxo, papéis e gestão de defeitos definidas pela equipe.*

### Padrões de Desenvolvimento
A arquitetura mantém a separação de responsabilidades já adotada pelo projeto: Domínio (regras de remuneração), `ColaboradorForm` (validação de entrada), `FolhaPagamentoController` (controlador web), `FolhaPagamentoService` (regras de aplicação) e `RepositorioColaboradores` (persistência).
* **Convenções de código:** Seguir convenções Java (PascalCase para classes, camelCase para métodos e atributos).
* **Boas práticas:** Restringir visibilidade ao necessário, utilizar `final` quando adequado, manter métodos coesos, validar dados de entrada e tratar exceções de forma explícita sem ignorar erros relevantes.
* **Configuração e dependências:** Mudanças no `pom.xml` devem ter justificativa técnica. Confirmar se o projeto compila e se os testes pertinentes passam antes de qualquer integração.

### Controle de Versão
O versionamento é gerido via Git e armazenado no GitHub. A integração de alterações é feita por Pull Requests, evitando commits diretos na `main`.
* **Branches de trabalho:** `feat/` (novas funcionalidades), `fix/` (correção de defeitos) e `docs/` (documentação).
* **Commits Semânticos:** Devem ser pequenos e objetivos. Padrão sugerido: `<tipo>: <descrição objetiva> [KAN-XX]` (Ex: `docs: adiciona plano de gestão da qualidade [KAN-01]`).

### Revisões Técnicas
Todas as entregas passam por inspeção no Pull Request. O autor implementa a alteração, executa os testes e atualiza a documentação. O revisor valida o atendimento aos requisitos, a ausência de regressões e a organização do código.
**Checklist Obrigatório de PR:**
- [ ] Requisito ou Issue relacionado identificado.
- [ ] Alteração descrita com clareza e padrões de codificação respeitados.
- [ ] Regras de negócio, validações verificadas e tratamento de exceções garantido.
- [ ] Testes pertinentes executados e resultados/evidências registrados.
- [ ] Documentação atualizada (quando aplicável).
- [ ] Aprovação registrada e ausência de pendências bloqueadoras.

### Métricas de Qualidade
As metas abaixo são objetivos propostos para a versão e exigem comprovação através de relatórios e inspeção nas ferramentas do projeto:

| Métrica | Meta |
| :--- | :--- |
| Cobertura | 80% |
| Defeitos Críticos | 0 |
| Requisitos Atendidos | 80% |

*Nota de medição:* A meta de PRs integrados com revisão e de Testes obrigatórios executados também é de 100%. A cobertura será aferida configurando a ferramenta compatível no `pom.xml`.

---

### Gestão de Defeitos e Aprovação (Complementar)

**Fluxo de Defeitos (Issues):**
Todo defeito deve ser registrado no GitHub Issues detalhando os passos para reprodução, resultados esperados/observados e a versão. 
* **Estados:** `Aberto` ➔ `Em análise` ➔ `Em correção` ➔ `Em revisão` ➔ `Resolvido` ➔ `Validado` ➔ `Fechado`.
* **Severidade:** Crítica (S1 - falha grave/bloqueadora), Alta (S2), Média (S3) e Baixa (S4).

**Critérios de Aprovação da Entrega:**
Uma entrega só será aprovada quando os requisitos aplicáveis estiverem atendidos (ou exceções formalmente justificadas), a meta de defeitos críticos (Zero) for respeitada, os testes obrigatórios possuírem resultados registrados e os Pull Requests contiverem revisão e aprovação dentro do processo acordado pela equipe.
