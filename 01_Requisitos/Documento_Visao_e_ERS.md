# Documento de Visão e Especificação de Requisitos de Software (ERS)

**Projeto:** Sistema de Folha de Pagamento<br>
**Unidade Curricular:** Garantia da Qualidade de Software<br>
**Versão do software:** 2.0.0<br>

---

## 1. Visão do Sistema

### 1.1. Objetivo

Desenvolver um sistema de folha de pagamento que permita o cadastro, o gerenciamento e o cálculo de salários de colaboradores de diferentes categorias e tipos de pagamento, gerando relatórios individuais e consolidando os valores em uma folha de pagamento.

### 1.2. Escopo

O sistema deverá manter o cadastro de colaboradores, permitir diferentes tipos de remuneração (**Padrão**, **Comissionado** e por **Produção**), calcular automaticamente os valores a receber, emitir folhas de pagamento individuais e consolidadas, exibir informações cadastrais dos colaboradores e apresentar o custo total da folha.

O cálculo e a emissão das folhas deverão filtrar os dados por mês e ano de apuração, manter o histórico de colaboradores para as folhas mesmo após sua exclusão do cadastro ativo e exibir como zero os campos de valores não preenchidos quando não houver produtividade ou comissão.

### 1.3. Premissas

- O projeto será desenvolvido utilizando uma estrutura orientada a objetos.
- Toda matrícula deverá ser única dentro do sistema em ambiente produtivo.

### 1.4. Restrições

- O sistema deverá validar os dados de entrada antes de efetuar qualquer cálculo.
- Os registros de colaboradores e o histórico do processamento das folhas deverão ser armazenados de forma persistente em um banco de dados SQL estruturado, atendendo à necessidade de persistência do projeto.

### 1.5. Benefícios Esperados

Automatizar e aumentar a precisão dos cálculos de remuneração, consolidar de forma confiável os custos da folha e contribuir para a gestão financeira e o controle das contas da empresa.

### 1.6. Stakeholders

- **Cliente:** administradores e gestores.
- **Usuário:** funcionário do setor de RH ou administrador do sistema. Busca uma interface amigável e simplicidade para cadastrar colaboradores e gerar a folha automatizada.
- **Equipe:** desenvolvedores e profissionais de Quality Assurance (QA). Busca código estruturado, expansível e com qualidade assegurada.

---

## 2. Especificação de Requisitos de Software

### 2.1. Requisitos Funcionais (RF)

- **RF001 — Cadastro de Colaborador:** permitir o cadastro de colaboradores com matrícula, nome, salário base e tipo de colaborador.
- **RF002 — Cadastro de Colaborador Padrão:** permitir o cadastro de colaboradores que recebem apenas o salário base.
- **RF003 — Cadastro de Colaborador Comissionado:** permitir informar o valor das vendas e o percentual de comissão para o cálculo da remuneração.
- **RF004 — Cadastro de Colaborador por Produção:** permitir informar a quantidade produzida e o valor pago por unidade para o cálculo da remuneração.
- **RF005 — Consulta de Colaboradores:** apresentar a lista de colaboradores cadastrados, com seus dados cadastrais e remuneratórios.
- **RF006 — Cálculo de Salário:** calcular automaticamente o salário final de cada colaborador de acordo com sua categoria de remuneração, mediante filtro de mês e ano da competência.
- **RF007 — Geração da Folha de Pagamento:** gerar uma folha filtrada por período (mês/ano), contendo matrícula, nome, tipo de colaborador e salário final dos colaboradores.
- **RF008 — Emissão de Resumo da Folha:** apresentar o valor total da folha, somando os salários finais dos colaboradores do período filtrado.
- **RF009 — Manutenção de Cadastros:** permitir inclusão, alteração, exclusão e consulta de colaboradores. A exclusão deverá preservar o histórico das folhas de meses e anos anteriores.
- **RF010 — Relatório Individual (extensão do RF007):** exibir matrícula, nome, tipo de colaborador, salário base, adicionais e salário final. Se não houver valores de vendas ou produção no período, o adicional deverá aparecer explicitamente como `R$ 0,00`.
- **RF011 — Relatório Resumido (extensão do RF008):** exibir a quantidade de colaboradores, o valor total da folha e o total pago por categoria no respectivo mês/ano.

### 2.2. Requisitos Não Funcionais (RNF)

- **RNF001 — Usabilidade:** possuir interface amigável e intuitiva.
- **RNF002 — Validação de Entrada:** validar os dados de entrada antes de efetuar qualquer cálculo.
- **RNF003 — Persistência:** armazenar os registros de colaboradores de forma persistente em banco de dados SQL, propiciando relatórios confiáveis e integridade histórica.
- **RNF004 — Extensibilidade:** permitir a inclusão futura de novos tipos de remuneração sem necessidade de alterar os cálculos já existentes.

### 2.3. Regras de Negócio (RN)

- **RN001:** toda matrícula deverá ser única dentro do sistema.
- **RN002:** o nome do colaborador é obrigatório.
- **RN003:** o salário base não poderá ser negativo.
- **RN004:** o valor de vendas não poderá ser negativo.
- **RN005:** o percentual de comissão não poderá ser negativo.
- **RN006:** a quantidade produzida não poderá ser negativa.
- **RN007:** o valor pago por unidade produzida não poderá ser negativo.
- **RN008:** o salário final deverá ser calculado automaticamente pelo sistema.
- **RN009 — Exclusão Lógica (*soft delete*):** a exclusão de colaboradores deverá ser lógica, preservando o histórico e as folhas retroativas independentemente da remoção do cadastro ativo.
- **RN010 — Valores Zerados nos Relatórios:** valores nulos de remuneração variável não deverão ser ocultados; deverão ser exibidos como `R$ 0,00` nos relatórios e recibos.

### 2.4. Critérios Gerais de Aceitação

O sistema poderá ser considerado conforme quando:

1. Permitir o cadastro dos três tipos de colaboradores.
2. Validar os dados obrigatórios conforme as regras de negócio.
3. Calcular corretamente os salários de cada categoria.
4. Gerar folhas individuais e consolidadas respeitando os filtros de mês e ano.
5. Exibir o total geral da folha de pagamento do período.
6. Possibilitar expansão para novos tipos de remuneração.

---

## 3. Matriz de Rastreabilidade

A matriz abaixo relaciona requisitos, casos de uso, componentes e casos de teste. Os IDs `CT001`–`CT010` são agrupadores de alto nível; a correspondência com os 40 casos detalhados (`CT-001`–`CT-040`) está em [Casos_Teste_Rastreaveis.md](../04_Testes/Casos_Teste_Rastreaveis.md#correspondência-com-os-ids-da-matriz-da-ers). O status reflete a execução registrada em 23/11/2026 (suíte automatizada e evidências de homologação). A matriz detalhada requisito × implementação × teste está em [Matriz_Conformidade_Requisitos.md](Matriz_Conformidade_Requisitos.md).

| Requisito / Regra | Caso de Uso | Componente / Classe UML | Caso de Teste | Status (A3, 23/11/2026) |
| :--- | :--- | :--- | :---: | :--- |
| `RF001`, `RF009`, `RN001`, `RN002`, `RN009` | `UC01` — Cadastrar Colaborador | `Colaborador` | `CT001` | Verificado — aprovado |
| `RF002`, `RN003` | `UC01` — Cadastrar Colaborador | `ColaboradorPadrao` | `CT002` | Verificado — aprovado |
| `RF003`, `RN004`, `RN005` | `UC01` — Cadastrar Colaborador | `ColaboradorComissionado` | `CT003` | Verificado — aprovado |
| `RF004`, `RN006`, `RN007` | `UC01` — Cadastrar Colaborador | `ColaboradorProducao` | `CT004` | Verificado — aprovado |
| `RF005`, `RF009`, `RN009` | `UC02` — Consultar Colaboradores | `Colaborador` | `CT005` | Verificado — aprovado |
| `RF006`, `RF007`, `RF010`, `RN008`, `RN010` | `UC03` — Gerar Folha de Pagamento | `FolhaPagamentoService`, `Colaborador` | `CT006` | Verificado — aprovado |
| `RF008`, `RF011` | `UC04` — Emitir Resumo da Folha | `ResumoFolha`, `FolhaPagamentoService` | `CT007` | Verificado — aprovado |
| `RNF002` | `UC01`, `UC03` | Interface / Backend Service | `CT008` | Verificado — aprovado |
| `RNF003` | `UC01`, `UC02`, `UC03`, `UC04` | Banco de Dados (PostgreSQL) | `CT009` | Verificado — aprovado (PostgreSQL real via Testcontainers e CT-030-D) |
| `RNF004` | Todos | Arquitetura (`Colaborador`) | `CT010` | Verificado — aprovado (CT-033 automatizado) |
