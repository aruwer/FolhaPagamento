# Documento de Arquitetura e Modelagem UML
**Projeto:** Folha de Pagamento
**Versão:** QDS_1.1.0
**Responsável:** Dev 2 (Revisão: Dev 1)
**Dependências:** KAN-01
**Branch:** docs/QDS_1.1.0/uml-arquitetura

---

## 1. Decisões Arquiteturais e Tecnológicas
A arquitetura do sistema segue o padrão **MVC (Model-View-Controller)** em camadas, promovendo uma clara separação de responsabilidades.

*   **Tecnologias Atuais e Planejadas:** Java 17, Spring Boot 3.5.5 (core do back-end), Thymeleaf + HTML/CSS/JS (renderização no lado do servidor e interface do usuário).
*   **Persistência e Base de Dados:** O sistema utiliza o Spring Data JPA para a abstração da persistência. A base de dados principal planejada é o **PostgreSQL**. A estratégia de herança para as classes de domínio (`Colaborador`) deverá adotar *Single Table* ou *Joined* dependendo dos ganhos de performance planejados na migração.
*   **Evolução e Escalabilidade:** O design suporta a inclusão de novos tipos de remuneração através de polimorfismo na classe `Colaborador`. Quando a migração de banco de dados se concretizar, as anotações do JPA tratarão do mapeamento sem impactar as camadas de Serviço ou Interface. *Nota: Os diagramas deverão ser atualizados caso a migração torne o modelo diferente do estado atual.*

## 2. Estrutura em Camadas (Componentes)
*   **Interface (Presentation):** `FolhaPagamentoController` e os templates `Thymeleaf`. Responsáveis por receber requisições HTTP, mapear para o `ColaboradorForm` e devolver as views HTML.
*   **Aplicação/Serviço (Application):** `FolhaPagamentoService`. Orquestra as regras de negócio, processa a lógica de cálculo (chamando as entidades) e interage com a persistência.
*   **Domínio (Domain):** `Colaborador` (abstrata), `ColaboradorPadrao`, `ColaboradorComissionado`, `ColaboradorProducao`. Contêm o estado e o comportamento core (ex: cálculo de vencimentos baseados em diferentes regras de negócio).
*   **Persistência (Infrastructure):** `RepositorioColaboradores`. Interface de comunicação com o banco PostgreSQL.

---

## 3. Diagrama de Casos de Uso
Este diagrama ilustra os fluxos principais de interação do usuário com o sistema.

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
    FolhaPagamentoWebApplication ..> FolhaPagamentoController : Inicia (Boot)
    FolhaPagamentoController ..> ColaboradorForm : Recebe (DTO)
    FolhaPagamentoController --> FolhaPagamentoService : Chama
    FolhaPagamentoService --> RepositorioColaboradores : Consome
    RepositorioColaboradores --> Colaborador : Persiste
    Colaborador <|-- ColaboradorPadrao : Herda
    Colaborador <|-- ColaboradorComissionado : Herda
    Colaborador <|-- ColaboradorProducao : Herda

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