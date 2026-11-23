# Matriz de Conformidade de Requisitos e Regras de Negócio (QDS 2.0.0)

**Branch:** `feat/QDS_2.0.0/db-postgresql`
**Escopo:** Verificação requisito a requisito (RF001–RF009, RN001–RN008, RNF001–RNF004) e critérios de aceitação do cliente.

---

## 1. Requisitos Funcionais (RF001 a RF009)

| ID | Requisito | Status | Evidência no Código / Template |
| :--- | :--- | :--- | :--- |
| **RF001** | Calcular salário final conforme categoria (Padrão, Comissionado, Produção) | **CONFORME** | `Colaborador.getSalarioFinal()` soma `salarioBase + getAdicional()` polimorficamente em `ColaboradorPadrao`, `ColaboradorComissionado` e `ColaboradorProducao`. |
| **RF002** | Armazenar e exibir informações cadastrais (matrícula, nome, tipo, salário base e dados variáveis) | **CONFORME** | Atributos encapsulados em `Colaborador.java` e subclasses; exibição em `index.html`, `folha.html` e `folha-individual.html`. |
| **RF003** | Manter colaboradores cadastrados (incluir, consultar, editar e remover) | **CONFORME** | `FolhaPagamentoController.java` e `FolhaPagamentoService.java` (`cadastrar`, `atualizar`, `excluir`, `buscar`, `listar`). |
| **RF004** | Emitir relatório individual de pagamento detalhado | **IMPLEMENTADO** | Rota individual e `templates/folha-individual.html` consultam o snapshot da competência, sem recalcular o histórico. |
| **RF005** | Emitir relatório consolidado da folha (matrícula, nome, tipo, base, adicionais e salário final) | **IMPLEMENTADO** | `/folha` e `templates/folha.html` consultam os snapshots por mês/ano. |
| **RF006** | Validar dados de entrada no cadastro e na edição | **CONFORME** | Bean Validation (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Pattern`) em `ColaboradorForm.java` e validações de construtor nas classes de domínio. |
| **RF007** | Disponibilizar interface web navegável e intuitiva | **CONFORME** | Templates Thymeleaf (`index.html`, `folha.html`, `folha-individual.html`, `colaboradores/form.html`), `app.css` e `colaborador-form.js`. |
| **RF008** | Persistir os dados dos colaboradores entre execuções do sistema | **IMPLEMENTADO; validação PostgreSQL pendente** | Contrato `RepositorioColaboradores`, adaptador JPA, migrations Flyway e testes H2. Os testes Testcontainers exigem Docker e ainda precisam de execução confirmada. |
| **RF009** | Exibir resumo consolidado com quantidade, total geral e totais por categoria | **IMPLEMENTADO** | `ResumoFolha.java` é calculado por competência e exibido em `folha.html`; o resumo inicial está disponível em `index.html`. |

---

## 2. Regras de Negócio (RN001 a RN008)

| ID | Regra de Negócio | Status | Evidência no Código |
| :--- | :--- | :--- | :--- |
| **RN001** | Colaborador Padrão: `Salário Final = Salário Base` (`Adicional = 0.0`) | **CONFORME** | `ColaboradorPadrao.getAdicional()` retorna zero em `BigDecimal`; `getSalarioFinal()` retorna a base. |
| **RN002** | Colaborador Comissionado: `Salário Final = Salário Base + (Valor Vendas * Percentual Comissão / 100)` | **CONFORME** | `ColaboradorComissionado.getAdicional()` calcula o percentual em `BigDecimal`. |
| **RN003** | Colaborador Produção: `Salário Final = Salário Base + (Quantidade Produzida * Valor por Unidade)` | **CONFORME** | `ColaboradorProducao.getAdicional()` calcula quantidade decimal vezes valor unitário em `BigDecimal`. |
| **RN004** | Unicidade de matrícula no cadastro | **CONFORME** | O serviço valida cadastros ativos e o banco aplica restrição `UNIQUE` à matrícula. |
| **RN005** | Proibição de valores negativos (salário base, vendas, comissão, quantidade produzida e valor unitário >= 0) | **CONFORME** | `Colaborador.validarNaoNegativo()` e `@DecimalMin("0.0")` em `ColaboradorForm.java`. |
| **RN006** | Restrição de limites em percentual de comissão (0% a 100%) e higienização de entradas | **AJUSTADO / CONFORME** | `@DecimalMax("100.0")` em `ColaboradorForm.java` e validação equivalente em `ColaboradorComissionado.java`. |
| **RN007** | Extensibilidade para novos tipos sem condicionais (`if/switch`) de cálculo espalhadas | **CONFORME** | Cálculo 100% polimórfico via `getAdicional()` e `getSalarioFinal()` na hierarquia `Colaborador`. |
| **RN008** | Formatação monetária no padrão brasileiro (`R$ #.##0,00`) | **CONFORME** | `FormatadorMoeda.java` formata valores monetários no padrão `pt-BR`. |

---

## 3. Requisitos Não Funcionais (RNF001 a RNF004)

| ID | Requisito Não Funcional | Status | Evidência Técnica |
| :--- | :--- | :--- | :--- |
| **RNF001** | Arquitetura em camadas e separação de responsabilidades (MVC + Service + Repository) | **CONFORME** | Separação entre `FolhaPagamentoController`, `FolhaPagamentoService`, `RepositorioColaboradores` e classes de domínio. |
| **RNF002** | Manutenibilidade, Orientação a Objetos e Testabilidade | **CONFORME** | Herança, encapsulamento (`final`) e polimorfismo validados por `ConformidadeRequisitosTest.java`. |
| **RNF003** | Usabilidade e tratamento amigável de erros com dados vazios ou inválidos | **CONFORME** | `BindingResult` e tratamento de erros de negócio em `FolhaPagamentoController.java`; telas principais também são exercitadas por teste MockMvc. |
| **RNF004** | Confiabilidade na persistência e inicialização segura | **IMPLEMENTADO; validação PostgreSQL pendente** | Flyway versiona o esquema e Hibernate valida o mapeamento na inicialização; integração real com PostgreSQL ainda depende de execução Testcontainers/Docker. |

---

## 4. Lacunas e evidências pendentes

1. **Integração PostgreSQL:** a implementação JPA, migrations e configuração estão presentes, mas a aprovação final de RF008/RNF004 requer executar `RepositorioColaboradoresPostgresTest` com Docker disponível e confirmar a inicialização real via Compose.
2. **Dados legados:** `dados/colaboradores.tsv` não é importado automaticamente. Se a entrega A3 exigir retenção desses registros, é necessário implementar e testar uma migração separada.
3. **Avaliação acadêmica:** esta matriz registra evidências técnicas do código; a nota máxima depende também da rubrica oficial, da demonstração e dos artefatos de entrega exigidos pelo docente.
4. **Regressão automatizada:** `ConformidadeRequisitosTest` cobre fórmulas RN001–RN003, limites e conversão do formulário. `RepositorioColaboradoresTest` cobre persistência, snapshots e exclusão lógica em H2.