# Documento de Arquitetura e Modelagem UML
**Projeto:** Folha de Pagamento  
**Versão:** QDS_1.2.0 (Revisão Pós-Feedback A1)  
**Responsável:** Rafaela Acosta (Revisão: Amanda Ruwer)  
**Dependências:** KAN-01  
**Branch:** docs/QDS_1.1.0/uml-arquitetura  

---

# 4. DIAGRAMAS UML

### 4.1. Diagrama de Casos de Uso
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
        UC05(["UC05: Emitir Resumo Consolidado"])
    end

    Admin --> UC01
    Admin --> UC02
    Admin --> UC03
    Admin --> UC04
    Admin --> UC05
```

---

### 4.2. Diagrama de Classes (Arquitetura Implementada)
O diagrama reflete fielmente o código-fonte Java do projeto:
* **Identificação e Base Herdada:** A classe abstrata `Colaborador` utiliza `matricula` (String) como identificador de negócio e define o `salarioBase` como atributo comum herdado por todas as modalidades.
* **Regras de Cálculo por Modalidade:**
  * `ColaboradorPadrao`: recebe o `salarioBase` integral.
  * `ColaboradorComissionado`: herda `salarioBase` e adiciona o cálculo sobre `vendasBrutas` (ou total de vendas) multiplicado pelo `percentualComissao`.
  * `ColaboradorProducao`: herda `salarioBase` e soma a bonificação baseada na `quantidadeProduzida` multiplicada pelo `valorPorUnidade`.
* **Persistência atual:** `RepositorioColaboradores` define o contrato usado pelo serviço e `RepositorioColaboradoresJpa` o implementa com Spring Data JPA e PostgreSQL.
* **Histórico da folha:** cada processamento grava snapshots imutáveis por competência em `folhas_processadas`; a exclusão de colaboradores é lógica.

```mermaid
classDiagram
    direction TB

    %% Camada de Apresentação / Web
    class FolhaPagamentoController {
        -FolhaPagamentoService service
        -FormatadorMoeda formatador
        +inicio(Model) String
        +novo(Model) String
        +cadastrar(ColaboradorForm, BindingResult, RedirectAttributes) String
        +editar(String, Model, RedirectAttributes) String
        +atualizar(String, ColaboradorForm, BindingResult, RedirectAttributes) String
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
        -BigDecimal vendasBrutas
        -BigDecimal percentualComissao
        -Integer quantidadeProduzida
        -BigDecimal valorPorUnidade
        +toColaborador() Colaborador
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
        +gerarResumo(int, int) ResumoFolha
    }

    class ResumoFolha {
        -int quantidadeColaboradores
        -BigDecimal totalFolha
        -Map~String, BigDecimal~ totaisPorCategoria
        -Map~String, Integer~ quantidadePorCategoria
    }

    class FormatadorMoeda {
        +formatar(double) String
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

    class ColaboradorJpaEntity {
        -Long id
        -String matricula
        -String nome
        -BigDecimal salarioBase
        -boolean ativo
        -OffsetDateTime excluidoEm
        -OffsetDateTime criadoEm
    }

    class FolhaProcessadaJpaEntity {
        -Long id
        -int mes
        -int ano
        -BigDecimal salarioBaseSnapshot
        -BigDecimal adicionalCalculado
        -BigDecimal salarioFinal
        -OffsetDateTime dataProcessamento
    }

    class FolhaProcessada {
        <<record>>
        +String matricula
        +String nome
        +String tipo
        +int mes
        +int ano
        +BigDecimal salarioBase
        +BigDecimal adicional
        +BigDecimal salarioFinal
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
        +getAdicional() BigDecimal*
        +getSalarioFinal() BigDecimal
    }

    class ColaboradorPadrao {
        +getTipo() String
        +getAdicional() BigDecimal
    }

    class ColaboradorComissionado {
        -BigDecimal valorVendas
        -BigDecimal percentualComissao
        +getValorVendas() BigDecimal
        +getPercentualComissao() BigDecimal
        +getAdicional() BigDecimal
    }

    class ColaboradorProducao {
        -BigDecimal quantidadeProduzida
        -BigDecimal valorPorUnidade
        +getQuantidadeProduzida() BigDecimal
        +getValorPorUnidade() BigDecimal
        +getAdicional() BigDecimal
    }

    %% Relacionamentos
    FolhaPagamentoController ..> ColaboradorForm : Recebe e valida
    FolhaPagamentoController --> FolhaPagamentoService : Orquestra fluxo
    FolhaPagamentoController --> FormatadorMoeda : Formata valores (BRL)
    FolhaPagamentoService --> RepositorioColaboradores : Usa contrato
    FolhaPagamentoService ..> ResumoFolha : Consolida cálculos
    RepositorioColaboradores <|.. RepositorioColaboradoresJpa : Implementa
    RepositorioColaboradoresJpa --> ColaboradorJpaRepository : CRUD JPA
    RepositorioColaboradoresJpa --> FolhaProcessadaJpaRepository : Snapshots JPA
    RepositorioColaboradoresJpa --> ColaboradorJpaEntity : Persiste
    RepositorioColaboradoresJpa --> FolhaProcessadaJpaEntity : Persiste snapshots
    FolhaProcessadaJpaEntity --> ColaboradorJpaEntity : Referencia
    FolhaProcessadaJpaEntity ..> FolhaProcessada : Projeta leitura
    Colaborador <|-- ColaboradorPadrao : Herda salarioBase
    Colaborador <|-- ColaboradorComissionado : Herda salarioBase
    Colaborador <|-- ColaboradorProducao : Herda salarioBase
```

---

### 4.3. Diagramas de Sequência

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

    Admin->>View: Preenche matrícula, nome, tipo, salário base e variáveis
    View->>Control: POST /colaboradores (ColaboradorForm)
    Control->>Form: toColaborador() (Instancia Padrão, Comissionado ou Produção)
    Form-->>Control: Objeto Colaborador criado
    Control->>Service: cadastrar(colaborador)
    Service->>Repo: cadastrar(colaborador)
    Repo->>Db: Verifica matrícula ativa e insere entidade JPA
    Db-->>Repo: Confirmação transacional
    Repo-->>Service: Cadastro persistido
    Service-->>Control: Registro concluído
    Control-->>View: Redirect /colaboradores (Mensagem de sucesso)
    View-->>Admin: Exibe listagem atualizada
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

# 5. DOCUMENTO DE ARQUITETURA

### 5.1. Visão Geral e Estilo Arquitetural
O sistema adota o padrão **MVC (Model-View-Controller)** combinado com uma arquitetura em camadas (*Layered Architecture*), garantindo baixo acoplamento, alta coesão e facilidade de manutenção evolutiva.

```mermaid
flowchart LR
    Usuario["👤 Administrador / RH"] --> View["Interface Web<br/>(Thymeleaf + HTML/CSS)"]
    View --> Controller["Camada de Controle<br/>(FolhaPagamentoController)"]
    Controller --> Service["Camada de Negócio<br/>(FolhaPagamentoService + Domínio)"]
    Service --> Repository["Contrato de persistência<br/>(RepositorioColaboradores)"]
    Repository --> Adapter["Adaptador JPA<br/>(RepositorioColaboradoresJpa)"]
    Adapter --> Database[("PostgreSQL<br/>colaboradores + folhas_processadas")]
```

1. **Camada de Apresentação (Interface e Controle):** Composta pelos templates `Thymeleaf` e pelo `FolhaPagamentoController`. Recebe os formulários HTTP (`ColaboradorForm`), aplica formatação monetária (`FormatadorMoeda`) e encaminha comandos para a camada de serviço.
2. **Camada de Negócio e Domínio:** Centralizada em `FolhaPagamentoService`, `ResumoFolha` e na hierarquia polimórfica de `Colaborador`. Isola todas as fórmulas de remuneração (Padrão, Comissionado por vendas/percentual e Produção por quantidade/valor unitário).
3. **Camada de Persistência (Infraestrutura de Dados):** `RepositorioColaboradores` define o contrato, implementado por `RepositorioColaboradoresJpa` com Spring Data JPA. Flyway versiona o esquema PostgreSQL; H2 e Testcontainers suportam os testes.

---

### 5.2. Tecnologias e Justificativas Arquiteturais

| Camada | Tecnologia | Versão / Papel | Justificativa Técnica da Escolha |
| :--- | :--- | :--- | :--- |
| **Front-end** | Thymeleaf + HTML5 / CSS3 / JS | Renderização Server-Side (SSR) | Integração nativa com o Spring MVC, permitindo bind direto de objetos Java (`ColaboradorForm` e `ResumoFolha`) no HTML sem necessidade de manter um projeto SPA separado, reduzindo a complexidade operacional. |
| **Back-end** | Java + Spring Boot | Java 17 LTS / Spring Boot 3.5.5 | Java 17 garante suporte de longo prazo (LTS), tipagem forte e segurança aritmética com `BigDecimal` para cálculos financeiros. O Spring Boot fornece injeção de dependências (`@Service`, `@Controller`) e configuração simplificada via Maven (`pom.xml`). |
| **Persistência** | PostgreSQL + Spring Data JPA | SGBD relacional / ORM | Transações, integridade referencial e restrições `CHECK`/`UNIQUE`; valores monetários persistidos em `NUMERIC(12,2)`. |
| **Migrações** | Flyway | V1 e V2 em `src/main/resources/db/migration` | Evolução versionada do esquema; Hibernate executa validação (`ddl-auto=validate`) na inicialização. |
| **Testes de persistência** | H2 + Testcontainers | H2 para integração rápida; PostgreSQL 17 em container | Testa o adaptador e a compatibilidade das migrations com o SGBD alvo quando Docker está disponível. |

### 5.3. Modelo de Dados e Histórico da Folha

As migrations Flyway `V1__schema.sql` e `V2__colaboradores_data_exclusao.sql` criam e evoluem as tabelas `colaboradores` e `folhas_processadas`.

- `colaboradores` armazena os campos das três categorias em uma tabela, com `tipo_colaborador`, dados variáveis nullable, restrições de valores não negativos e matrícula única. A coluna `ativo` e o timestamp `excluido_em` implementam exclusão lógica.
- `folhas_processadas` mantém snapshots dos dados cadastrais e remuneratórios por matrícula e competência. A chave única (`colaborador_id`, `mes`, `ano`) impede snapshots duplicados da mesma folha.
- O processamento é transacional e idempotente: cria snapshots somente para colaboradores elegíveis sem registro naquela competência. Consultas e demonstrativos leem os snapshots e não recalculam folhas processadas.
- Colaboradores desligados continuam disponíveis nos snapshots já processados; sua elegibilidade para novas competências considera a data de cadastro e a data de exclusão.
- A aplicação usa PostgreSQL por padrão, `spring.jpa.hibernate.ddl-auto=validate` e Flyway para validar e versionar o esquema. `docker-compose.yml` fornece PostgreSQL e pgAdmin locais; os testes rápidos usam H2 e a suíte Testcontainers usa PostgreSQL 17 quando Docker está disponível.
- O arquivo legado `dados/colaboradores.tsv` não é importado automaticamente. Qualquer migração de dados existentes deve ser planejada e validada separadamente antes de remover a fonte original.