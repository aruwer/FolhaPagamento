# Documento de Arquitetura e Modelagem UML
**Projeto:** Folha de Pagamento
**Versão:** QDS_1.1.0
**Responsável:** Dev 2 (Revisão: Dev 1)
**Dependências:** KAN-01
**Branch:** docs/QDS_1.1.0/uml-arquitetura

---

# 4. DIAGRAMAS UML

### Diagrama de Casos de Uso
```mermaid
flowchart LR
    Usuario([RH / Administrador])
    
    UC1([Cadastrar Colaborador])
    UC2([Consultar Colaboradores])
    UC3([Realizar Manutenção])
    UC4([Gerar Relatórios de Folha])
    
    Usuario --- UC1
    Usuario --- UC2
    Usuario --- UC3
    Usuario --- UC4
```

### Diagrama de Classes
```mermaid
classDiagram
    %% Camada de Interface
    class FolhaPagamentoController {
        +exibirFormulario()
        +processarCadastro(ColaboradorForm)
        +listarColaboradores()
    }
    class ColaboradorForm {
        -String nome
        -String tipo
        -double salarioBase
        -double comissao
        -int horasProducao
    }

    %% Camada de Serviço
    class FolhaPagamentoService {
        +salvarColaborador(Colaborador)
        +buscarTodos()
        +calcularFolhaGeral()
    }

    %% Camada de Persistência
    class RepositorioColaboradores {
        <<interface>>
        +save(Colaborador)
        +findAll()
        +findById(Long)
    }

    %% Camada de Domínio
    class Colaborador {
        <<abstract>>
        -Long id
        -String nome
        +calcularVencimento()* double
    }
    class ColaboradorPadrao {
        -double salarioBase
        +calcularVencimento() double
    }
    class ColaboradorComissionado {
        -double salarioBase
        -double comissao
        +calcularVencimento() double
    }
    class ColaboradorProducao {
        -int horas
        -double valorHora
        +calcularVencimento() double
    }

    %% Relações e Fronteiras
    FolhaPagamentoController ..> ColaboradorForm : Recebe (DTO)
    FolhaPagamentoController --> FolhaPagamentoService : Chama
    FolhaPagamentoService --> RepositorioColaboradores : Consome
    RepositorioColaboradores --> Colaborador : Persiste
    Colaborador <|-- ColaboradorPadrao : Herda
    Colaborador <|-- ColaboradorComissionado : Herda
    Colaborador <|-- ColaboradorProducao : Herda
```

### Diagramas de Sequência

**Fluxo de Cadastro e Manutenção**
```mermaid
sequenceDiagram
    autonumber
    actor RH as Usuário (RH)
    participant View as Thymeleaf View
    participant Control as FolhaPagamentoController
    participant Service as FolhaPagamentoService
    participant Repo as RepositorioColaboradores

    RH->>View: Preenche formulário (Novo ou Atualização)
    View->>Control: POST /colaboradores (ColaboradorForm)
    Control->>Service: salvarColaborador(dadosConvertidos)
    Service->>Repo: save(Colaborador)
    Repo-->>Service: Colaborador persistido no PostgreSQL
    Service-->>Control: Retorna sucesso
    Control-->>View: Redireciona para lista
    View-->>RH: Exibe mensagem de sucesso
```

**Fluxo de Consulta e Geração de Relatórios**
```mermaid
sequenceDiagram
    autonumber
    actor RH as Usuário (RH)
    participant View as Thymeleaf View
    participant Control as FolhaPagamentoController
    participant Service as FolhaPagamentoService
    participant Repo as RepositorioColaboradores
    participant Model as Colaborador (Domínio)

    RH->>Control: GET /relatorios/folha
    Control->>Service: calcularFolhaGeral()
    Service->>Repo: findAll()
    Repo-->>Service: Lista de Colaboradores
    
    loop Para cada Colaborador retornado
        Service->>Model: calcularVencimento()
        Model-->>Service: Retorna valor calculado pelo polimorfismo
    end
    
    Service-->>Control: ResumoFolha (Dados agregados)
    Control->>View: Envia modelo para o template
    View-->>RH: Renderiza tela com a tabela de resultados
```

---

# 5. DOCUMENTO DE ARQUITETURA

### Decisões Arquiteturais e Estrutura
A arquitetura do sistema segue o padrão **MVC (Model-View-Controller)** em camadas, promovendo uma clara separação de responsabilidades. O design suporta a inclusão de novos tipos de remuneração através de polimorfismo na classe `Colaborador`. Quando a migração de banco de dados se concretizar, as anotações do JPA tratarão do mapeamento sem impactar as camadas de Serviço ou Interface.

Abaixo apresentamos a arquitetura em camadas do sistema, partindo da interação do usuário até a persistência:

```mermaid
flowchart LR
    Usuario[Usuário] --> Interface[Interface]
    Interface --> Negocio[Camada de Negócio]
    Negocio --> BD[(Banco de Dados)]
```

### Tecnologias
Tabela de tecnologias definidas para a implementação do projeto:

| Camada | Tecnologia |
| :--- | :--- |
| **Front-end** | Thymeleaf, HTML, CSS, JavaScript |
| **Back-end** | Java 17, Spring Boot 3.5.5 |
| **Banco** | PostgreSQL |