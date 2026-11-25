# Modelagem UML (Análise e Projeto)
**Projeto:** Folha de Pagamento  
**Versão do artefato Maven:** 2.0.0<br>
**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta<br>
**Última revisão técnica:** 23/11/2026

---

# 1. Diagramas UML

### 1.1. Diagrama de Casos de Uso
O diagrama abaixo representa as interações do ator principal (**Administrador / RH**) dentro da fronteira do Sistema de Folha de Pagamento. Os rótulos foram dimensionados para garantir legibilidade integral na exportação para PDF.

```mermaid
flowchart LR
    %% Ator com estereótipo explícito de UML
    Admin["👤 <<Actor>><br/>Administrador / RH"]

    %% Fronteira do Sistema
    subgraph Sistema["Sistema de Folha de Pagamento"]
        direction TB
        UC01(["UC01: Cadastrar Colaborador"])
        UC02(["UC02: Consultar Colaboradores"])
        UC03(["UC03: Atualizar ou Remover Cadastro"])
        UC04(["UC04: Processar Folha de Pagamento"])
        UC05(["UC05: Consultar Folha Consolidada"])
        UC06(["UC06: Consultar Demonstrativo Individual"])
    end

    Admin --> UC01
    Admin --> UC02
    Admin --> UC03
    Admin --> UC04
    Admin --> UC05
    Admin --> UC06
```

---

### 1.2. Diagrama de Classes (Arquitetura Implementada)
O diagrama apresenta as principais classes e responsabilidades da implementação atual:
* **Identificação e Base Herdada:** A classe abstrata `Colaborador` utiliza `matricula` (String) como identificador de negócio e define o `salarioBase` como atributo comum herdado por todas as modalidades.
* **Regras de Cálculo por Modalidade:**
  * `ColaboradorPadrao`: recebe o `salarioBase` integral.
  * `ColaboradorComissionado`: herda `salarioBase` e adiciona o cálculo sobre `valorVendas` multiplicado pelo `percentualComissao`.
  * `ColaboradorProducao`: herda `salarioBase` e soma a bonificação baseada na `quantidadeProduzida` multiplicada pelo `valorPorUnidade`.
* **Persistência atual:** `RepositorioColaboradores` define o contrato usado pelo serviço e `RepositorioColaboradoresJpa` o implementa com Spring Data JPA e PostgreSQL.
* **Histórico da folha:** cada processamento grava snapshots imutáveis por competência em `folhas_processadas`; a exclusão de colaboradores é lógica.

```mermaid
classDiagram
    direction TB

    %% Camada de Apresentação / Web
    class FolhaPagamentoController {
        -FolhaPagamentoService service
        +inicio(Model) String
        +novo(Model) String
        +cadastrar(ColaboradorForm, BindingResult, Model, RedirectAttributes) String
        +editar(String, Model, RedirectAttributes) String
        +atualizar(String, ColaboradorForm, BindingResult, Model, RedirectAttributes) String
        +excluir(String, RedirectAttributes) String
        +folha(Integer, Integer, Model) String
        +processarFolha(Integer, Integer, RedirectAttributes) String
        +folhaIndividual(String, Integer, Integer, Model) String
    }

    class ColaboradorForm {
        -String matricula
        -String nome
        -String tipo
        -BigDecimal salarioBase
        -BigDecimal valorVendas
        -BigDecimal percentualComissao
        -BigDecimal quantidadeProduzida
        -BigDecimal valorPorUnidade
        +construirColaborador() Colaborador
    }

    %% Camada de Serviço / Aplicação
    class FolhaPagamentoService {
        -RepositorioColaboradores repositorio
        +cadastrar(Colaborador) void
        +atualizar(Colaborador) void
        +excluir(String) void
        +listar() List~Colaborador~
        +buscar(String) Optional~Colaborador~
        +processarFolha(int, int) List~FolhaProcessada~
        +consultarFolha(int, int) List~FolhaProcessada~
        +buscarFolha(String, int, int) Optional~FolhaProcessada~
        +gerarResumo(int, int) ResumoFolha
        +gerarResumo() ResumoFolha
        +resumir(List~FolhaProcessada~) ResumoFolha
    }

    class ResumoFolha {
        -int quantidadeColaboradores
        -BigDecimal totalFolha
        -Map~String, BigDecimal~ totaisPorCategoria
        -Map~String, Integer~ quantidadePorCategoria
    }

    class FormatadorMoeda {
        +formatar(double) String
        +formatar(Double) String
        +formatar(BigDecimal) String
    }

    %% Contrato e adaptador de persistência
    class RepositorioColaboradores {
        <<interface>>
        +cadastrar(Colaborador) void
        +listar() List~Colaborador~
        +buscar(String) Optional~Colaborador~
        +atualizar(Colaborador) void
        +excluir(String) void
        +processarFolha(int, int) List~FolhaProcessada~
        +consultarFolha(int, int) List~FolhaProcessada~
        +buscarFolha(String, int, int) Optional~FolhaProcessada~
    }

    class RepositorioColaboradoresJpa {
        -ColaboradorJpaRepository colaboradores
        -FolhaProcessadaJpaRepository folhas
    }

    class ColaboradorJpaRepository {
        <<interface>>
    }

    class FolhaProcessadaJpaRepository {
        <<interface>>
    }

    class ColaboradorJpaEntity {
        -Long id
        -String matricula
        -String nome
        -TipoColaborador tipoColaborador
        -BigDecimal salarioBase
        -BigDecimal valorVendas
        -BigDecimal percentualComissao
        -BigDecimal quantidadeProduzida
        -BigDecimal valorPorUnidade
        -boolean ativo
        -OffsetDateTime excluidoEm
        -OffsetDateTime criadoEm
        -OffsetDateTime atualizadoEm
    }

    class FolhaProcessadaJpaEntity {
        -Long id
        -ColaboradorJpaEntity colaborador
        -String matriculaSnapshot
        -String nomeSnapshot
        -String tipoSnapshot
        -int mes
        -int ano
        -BigDecimal salarioBaseSnapshot
        -BigDecimal adicionalCalculado
        -BigDecimal salarioFinal
        -OffsetDateTime dataProcessamento
    }

    class FolhaProcessada {
        <<record>>
        +Long id
        +String matricula
        +String nome
        +String tipo
        +int mes
        +int ano
        +BigDecimal salarioBase
        +BigDecimal adicional
        +BigDecimal salarioFinal
        +OffsetDateTime processadaEm
    }

    %% Camada de Domínio
    class Colaborador {
        <<abstract>>
        -String matricula
        -String nome
        -BigDecimal salarioBase
        +getMatricula() String
        +getNome() String
        +getSalarioBase() BigDecimal
        +getTipo() String*
        +getCodigoPersistencia() String*
        +getAdicional() BigDecimal*
        +getDetalhesRemuneracao() String*
        +getSalarioFinal() BigDecimal
    }

    class ColaboradorPadrao {
        +getTipo() String
        +getCodigoPersistencia() String
        +getAdicional() BigDecimal
        +getDetalhesRemuneracao() String
    }

    class ColaboradorComissionado {
        -BigDecimal valorVendas
        -BigDecimal percentualComissao
        +getValorVendas() BigDecimal
        +getPercentualComissao() BigDecimal
        +getCodigoPersistencia() String
        +getAdicional() BigDecimal
        +getDetalhesRemuneracao() String
    }

    class ColaboradorProducao {
        -BigDecimal quantidadeProduzida
        -BigDecimal valorPorUnidade
        +getQuantidadeProduzida() BigDecimal
        +getValorPorUnidade() BigDecimal
        +getCodigoPersistencia() String
        +getAdicional() BigDecimal
        +getDetalhesRemuneracao() String
    }

    %% Relacionamentos
    FolhaPagamentoController ..> ColaboradorForm : Recebe e valida
    FolhaPagamentoController --> FolhaPagamentoService : Orquestra fluxo
    FolhaPagamentoService --> RepositorioColaboradores : Usa contrato
    FolhaPagamentoService ..> ResumoFolha : Consolida cálculos
    RepositorioColaboradores <|.. RepositorioColaboradoresJpa : Implementa
    RepositorioColaboradoresJpa --> ColaboradorJpaRepository : CRUD JPA
    RepositorioColaboradoresJpa --> FolhaProcessadaJpaRepository : Snapshots JPA
    RepositorioColaboradoresJpa --> ColaboradorJpaEntity : Persiste
    RepositorioColaboradoresJpa --> FolhaProcessadaJpaEntity : Persiste snapshots
    FolhaProcessadaJpaEntity --> ColaboradorJpaEntity : N:1
    FolhaProcessadaJpaEntity ..> FolhaProcessada : Projeta leitura
    Colaborador <|-- ColaboradorPadrao : Herda salarioBase
    Colaborador <|-- ColaboradorComissionado : Herda salarioBase
    Colaborador <|-- ColaboradorProducao : Herda salarioBase
```

---

### 1.3. Diagramas de Sequência

**Fluxo 1: Cadastro de Colaborador (Persistência PostgreSQL e Validação por Matrícula)**
```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador (RH)
    participant View as Thymeleaf View
    participant Control as FolhaPagamentoController
    participant Form as ColaboradorForm
    participant Service as FolhaPagamentoService
    participant Repo as RepositorioColaboradoresJpa
    participant Db as PostgreSQL

    Admin->>View: Preenche dados cadastrais e variáveis do tipo selecionado
    View->>Control: POST /colaboradores (ColaboradorForm)
    Control->>Form: construirColaborador() (instancia o subtipo selecionado)
    Form-->>Control: Objeto Colaborador criado
    Control->>Service: cadastrar(colaborador)
    Service->>Repo: cadastrar(colaborador)
    Repo->>Db: Verifica matrícula ativa e insere entidade JPA
    Db-->>Repo: Confirmação transacional
    Repo-->>Service: Cadastro persistido
    Service-->>Control: Registro concluído
    Control-->>View: Redirect / (Mensagem de sucesso)
    View-->>Admin: Exibe a página inicial com a listagem atualizada
```

**Fluxo 2: Processamento e Consulta da Folha por Competência**
```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador (RH)
    participant View as Thymeleaf View
    participant Control as FolhaPagamentoController
    participant Service as FolhaPagamentoService
    participant Repo as RepositorioColaboradoresJpa
    participant Db as PostgreSQL
    participant Snapshot as FolhaProcessadaJpaEntity

    Admin->>Control: POST /folha/processar (mês, ano)
    Control->>Service: processarFolha(mês, ano)
    Service->>Repo: processarFolha(mês, ano)
    Repo->>Db: Busca colaboradores e snapshots existentes
    loop Para cada colaborador elegível sem snapshot
        Repo->>Snapshot: Calcula valores e cria snapshot
        Snapshot->>Db: Persiste valores e identificação históricos
    end
    Repo-->>Service: Folha processada da competência
    Service-->>Control: Confirma processamento
    Control-->>Admin: Redirect para GET /folha?mes=...&ano=...
    Admin->>Control: GET /folha?mes=...&ano=...
    Control->>Service: consultarFolha(mês, ano)
    Service->>Repo: consulta somente leitura
    Repo->>Db: Busca snapshots da competência
    Db-->>Repo: Snapshots ordenados por matrícula
    Repo-->>Service: Lista de FolhaProcessada
    Service-->>Control: Lista e resumo por categoria
    Control->>View: Renderiza folha e valores formatados
    View-->>Admin: Exibe folha consolidada e links para demonstrativos
```


---

### 1.4. Outros diagramas relevantes

**Diagrama de estados — ciclo de vida do colaborador e da folha da competência.** A exclusão é lógica (RN009) e o snapshot de uma competência, depois de criado, não é recalculado: alterações cadastrais só afetam competências processadas depois delas.

```mermaid
stateDiagram-v2
    [*] --> Ativo: cadastrar (POST /colaboradores)
    Ativo --> Ativo: alterar cadastro (POST /colaboradores/{matricula})
    Ativo --> ExcluidoLogicamente: excluir (ativo = false, excluido_em = agora)
    ExcluidoLogicamente --> [*]
    note right of ExcluidoLogicamente
        Sai da lista ativa, mas os snapshots já
        processados continuam consultáveis.
    end note

    state "Folha da competência (mês/ano)" as Folha {
        [*] --> NaoProcessada
        NaoProcessada --> Processada: processar (cria snapshots)
        Processada --> Processada: reprocessar (idempotente: só inclui quem não tem snapshot)
    }
```

**Diagrama de implantação — ambiente local de execução e de testes.**

```mermaid
flowchart LR
    subgraph Host["Estação de trabalho (Windows)"]
        Browser["Navegador<br/>(Edge/Chrome)"]
        App["folha-pagamento-2.0.0.jar<br/>Spring Boot + Tomcat :8081"]
        subgraph Docker["Docker Desktop (docker compose)"]
            PG[("postgres:17-alpine<br/>:5433 → 5432")]
            PGA["pgAdmin 4<br/>:5050"]
        end
        subgraph Testes["mvnw clean verify"]
            H2[("H2 em memória<br/>testes rápidos")]
            TC[("PostgreSQL 17<br/>Testcontainers")]
        end
    end
    Browser -- HTTP --> App
    App -- JDBC / Flyway --> PG
    PGA -- administração --> PG
```

**Modelo entidade-relacionamento** — detalhado em [06_Banco_Dados](../06_Banco_Dados/README.md).

```mermaid
erDiagram
    COLABORADORES ||--o{ FOLHAS_PROCESSADAS : "gera snapshots"
    COLABORADORES {
        bigint id PK
        varchar matricula UK
        varchar nome
        varchar tipo_colaborador "PADRAO | COMISSIONADO | PRODUCAO"
        numeric salario_base
        numeric valor_vendas "nulo se não comissionado"
        numeric percentual_comissao ">= 0"
        numeric quantidade_produzida "nulo se não produção"
        numeric valor_por_unidade
        boolean ativo
        timestamptz criado_em
        timestamptz atualizado_em
        timestamptz excluido_em "V2 - exclusão lógica"
    }
    FOLHAS_PROCESSADAS {
        bigint id PK
        bigint colaborador_id FK
        varchar matricula_snapshot
        varchar nome_snapshot
        varchar tipo_snapshot
        int mes "1 a 12"
        int ano "2000 a 9999"
        numeric salario_base_snapshot
        numeric adicional_calculado
        numeric salario_final
        timestamptz data_processamento
    }
```

---

A arquitetura em camadas, as tecnologias e a estratégia de persistência estão no [Documento de Arquitetura](../03_Projeto/Documento_Arquitetura.md).
