# Sistema de Folha de Pagamento - Grupo 11

**Versão Atual:** `1.1.0` 

**Equipe:** Amanda Ruwer, Ana Luísa Lopes e Rafaela Acosta

Aplicação web Java para cadastrar colaboradores, calcular remunerações e emitir relatórios individuais e consolidados.

## 🔗 Repositório e Rastreabilidade

- **URL do Repositório (GitHub):** `https://github.com/aruwer/folhapagamento`
- Todo o histórico de commits, revisões de código via Pull Requests e gestão de tarefas (Quadro KANBAN/Issues) pode ser verificado integralmente no link acima, garantindo a rastreabilidade da EAP e do cronograma.

## 📂 Índice de Equivalência Documental

Para manter a integridade dos links internos do repositório, os artefatos foram mantidos na pasta `docs/`. Abaixo encontra-se o índice inequívoco mapeando nossos arquivos para a estrutura de pastas documentais exigida (01 a 11):

- **01 - Visão do Sistema e ERS:** `docs/VisaoDeSistema+ERSRastreavel.pdf`
- **02 - Matriz de Rastreabilidade:** `docs/VisaoDeSistema+ERSRastreavel.pdf` (pág. 4) e detalhamento na Seção 5 de `docs/CASOS_TESTE_RASTREAVEIS_A1.md`
- **03 - Modelagem UML e Arquitetura:** `docs/Arquitetura.md`
- **04 - Plano de Garantia da Qualidade (PGQ):** `docs/PGQ.md`
- **05 - Plano de Testes:** `docs/PLANO_ESTRATEGIA_TESTES_A1.md`
- **06 - Casos de Teste (Especificação):** `docs/CASOS_TESTE_RASTREAVEIS_A1.md`
- **07 - EAP, Cronograma e Responsabilidades:** `docs/Consolidar EAP, cronograma e responsabilidades.pdf`
- **08 a 11 - (Artefatos da Fase A3):** Evidências de execução de testes, manuais e termo de homologação serão adicionados na entrega final.

## Requisitos

- JDK 17 ou superior
- PowerShell no Windows para usar o Maven Wrapper incluído

## Executar

Na pasta raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Na primeira execução, o Maven Wrapper baixa o Maven necessário. Acesse [http://localhost:8081](http://localhost:8081) no navegador.

Para gerar um JAR executável:

```powershell
.\mvnw.cmd clean package
java -jar target\folha-pagamento-1.2.0.jar
```

Os dados ficam em `dados/colaboradores.tsv`, criado automaticamente. O local pode ser alterado pela propriedade `folha.arquivo-dados`.

## Funcionalidades

- Cadastro, consulta, alteração e exclusão de colaboradores.
- Remuneração padrão, comissionada e por produção, calculada por tipo.
- Folha individual e consolidada, com opção de impressão pelo navegador.
- Resumo com quantidade de colaboradores, custo total e valores por categoria.
- Validação de dados e matrícula única.
- Páginas Thymeleaf e backend Spring Boot no mesmo repositório.
