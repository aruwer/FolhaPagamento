# Matriz de Conformidade de Requisitos e Regras de Negócio (QDS 2.0.0)

**Branch:** `feat/QDS_2.0.0/conformidade-requisitos`  
**Escopo:** Verificação requisito a requisito (RF001–RF009, RN001–RN008, RNF001–RNF004) e critérios de aceitação do cliente.

---

## 1. Requisitos Funcionais (RF001 a RF009)

| ID | Requisito | Status | Evidência no Código / Template |
| :--- | :--- | :--- | :--- |
| **RF001** | Calcular salário final conforme categoria (Padrão, Comissionado, Produção) | **CONFORME** | `Colaborador.getSalarioFinal()` soma `salarioBase + getAdicional()` polimorficamente em `ColaboradorPadrao`, `ColaboradorComissionado` e `ColaboradorProducao`. |
| **RF002** | Armazenar e exibir informações cadastrais (matrícula, nome, tipo, salário base e dados variáveis) | **CONFORME** | Atributos encapsulados em `Colaborador.java` e subclasses; exibição em `index.html`, `folha.html` e `folha-individual.html`. |
| **RF003** | Manter colaboradores cadastrados (incluir, consultar, editar e remover) | **CONFORME** | `FolhaPagamentoController.java` e `FolhaPagamentoService.java` (`cadastrar`, `atualizar`, `remover`, `buscar`, `listar`). |
| **RF004** | Emitir relatório individual de pagamento detalhado | **CONFORME** | Rota de visualização individual em `FolhaPagamentoController.java` e template `templates/folha-individual.html`. |
| **RF005** | Emitir relatório consolidado da folha (matrícula, nome, tipo, base, adicionais e salário final) | **CONFORME** | Rota `/folha` em `FolhaPagamentoController.java` e tabela consolidada em `templates/folha.html`. |
| **RF006** | Validar dados de entrada no cadastro e na edição | **CONFORME** | Bean Validation (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Pattern`) em `ColaboradorForm.java` e validações de construtor nas classes de domínio. |
| **RF007** | Disponibilizar interface web navegável e intuitiva | **CONFORME** | Templates Thymeleaf (`index.html`, `folha.html`, `folha-individual.html`, `colaboradores/form.html`), `app.css` e `colaborador-form.js`. |
| **RF008** | Persistir os dados dos colaboradores entre execuções do sistema | **CONFORME** | `RepositorioColaboradores.java` com persistência e recarregamento automático dos registros. |
| **RF009** | Exibir resumo consolidado com quantidade, total geral e totais por categoria | **CONFORME** | `ResumoFolha.java` calculado em `FolhaPagamentoService.java` e exibido em `index.html` e `folha.html`. |

---

## 2. Regras de Negócio (RN001 a RN008)

| ID | Regra de Negócio | Status | Evidência no Código |
| :--- | :--- | :--- | :--- |
| **RN001** | Colaborador Padrão: `Salário Final = Salário Base` (`Adicional = 0.0`) | **CONFORME** | `ColaboradorPadrao.getAdicional()` retorna `0.0`; `getSalarioFinal()` retorna `salarioBase`. |
| **RN002** | Colaborador Comissionado: `Salário Final = Salário Base + (Valor Vendas * Percentual Comissão / 100)` | **CONFORME** | `ColaboradorComissionado.getAdicional()` retorna `valorVendas * percentualComissao / 100`. |
| **RN003** | Colaborador Produção: `Salário Final = Salário Base + (Quantidade Produzida * Valor por Unidade)` | **CONFORME** | `ColaboradorProducao.getAdicional()` retorna `quantidadeProduzida * valorPorUnidade`. |
| **RN004** | Unicidade de matrícula no cadastro | **CONFORME** | Verificação de duplicidade em `FolhaPagamentoService.java` / `RepositorioColaboradores.java` lançando `IllegalArgumentException`. |
| **RN005** | Proibição de valores negativos (salário base, vendas, comissão, quantidade produzida e valor unitário >= 0) | **CONFORME** | `Colaborador.validarNaoNegativo()` e `@DecimalMin("0.0")` em `ColaboradorForm.java`. |
| **RN006** | Restrição de limites em percentual de comissão (0% a 100%) e higienização de entradas | **AJUSTADO / CONFORME** | Lacuna corrigida nesta branch: adicionado `@DecimalMax("100.0")` em `ColaboradorForm.java` e validação `percentualComissao > 100.0` em `ColaboradorComissionado.java`. |
| **RN007** | Extensibilidade para novos tipos sem condicionais (`if/switch`) de cálculo espalhadas | **CONFORME** | Cálculo 100% polimórfico via `getAdicional()` e `getSalarioFinal()` na hierarquia `Colaborador`. |
| **RN008** | Formatação monetária no padrão brasileiro (`R$ #.##0,00`) | **CONFORME** | `FormatadorMoeda.java` formata valores monetários no padrão `pt-BR`. |

---

## 3. Requisitos Não Funcionais (RNF001 a RNF004)

| ID | Requisito Não Funcional | Status | Evidência Técnica |
| :--- | :--- | :--- | :--- |
| **RNF001** | Arquitetura em camadas e separação de responsabilidades (MVC + Service + Repository) | **CONFORME** | Separação entre `FolhaPagamentoController`, `FolhaPagamentoService`, `RepositorioColaboradores` e classes de domínio. |
| **RNF002** | Manutenibilidade, Orientação a Objetos e Testabilidade | **CONFORME** | Herança, encapsulamento (`final`) e polimorfismo validados por `ConformidadeRequisitosTest.java`. |
| **RNF003** | Usabilidade e tratamento amigável de erros com dados vazios ou inválidos | **CONFORME** | `BindingResult` e `try/catch (IllegalArgumentException)` em `FolhaPagamentoController.java` retornando mensagens amigáveis ao formulário. |
| **RNF004** | Confiabilidade na persistência e inicialização segura | **CONFORME** | `getCodigoPersistencia()` e `getDadosEspecificos()` garantem serialização consistente sem perda de dados. |

---

## 4. Registro de Lacunas Confirmadas e Ajustes Realizados
1. **Lacuna Confirmada (RN006 / RF006):** `ColaboradorComissionado.java` e `ColaboradorForm.java` validavam apenas limite inferior (`>= 0.0`) para o percentual de comissão, permitindo percentuais inválidos acima de `100%`.
   - **Ajuste aplicado:** Adicionada validação `if (percentualComissao > 100.0)` no construtor de `ColaboradorComissionado.java` e anotação `@DecimalMax(value = "100.0")` em `ColaboradorForm.java`.
2. **Evidência Automatizada de Não-Regressão:** Adicionada a suíte `src/test/java/folhapagamento/ConformidadeRequisitosTest.java` cobrindo os cálculos RN001–RN003, bloqueio de dados vazios/negativos/acima do limite e conversão do formulário.