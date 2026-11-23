# Manual do Usuário

## 1. Pré-requisitos e instalação local

- Windows com PowerShell e JDK 17 ou superior.
- Docker Desktop em execução.
- Arquivos do projeto, incluindo `mvnw.cmd` e `docker-compose.yml`.

Na raiz do projeto, crie o arquivo local `.env` a partir de `.env.example` e preencha as variáveis exigidas pelo Compose (`POSTGRES_PASSWORD`, `PGADMIN_EMAIL` e `PGADMIN_PASSWORD`). Não compartilhe nem versione o `.env`. Configure `DATABASE_PASSWORD` na sessão do PowerShell com o mesmo valor de `POSTGRES_PASSWORD`; não coloque senhas em argumentos ou arquivos versionados.

Inicie o banco e a aplicação:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Abra [http://localhost:8081](http://localhost:8081). O PostgreSQL local usa a porta 5433; o pgAdmin usa a porta 5050. O primeiro início aplica as migrations Flyway.

## 2. Cadastrar colaborador

1. No painel, clique em **Cadastrar colaborador**.
2. Preencha **Matrícula**, **Nome completo**, **Salário base** e **Tipo de remuneração**.
3. Selecione **Salário base**, **Salário + comissão** ou **Salário + produção**.
4. Para comissão, informe **Valor das vendas** e **Comissão sobre vendas**; para produção, informe **Quantidade produzida** e **Valor por unidade**. Campos variáveis em branco são tratados como zero.
5. Clique em **Cadastrar colaborador**. Em caso de edição, use **Salvar alterações**.

## 3. Consultar e manter cadastros

O painel apresenta colaboradores ativos, remuneração e custo mensal. Use **Editar** para alterar os dados. Use **Excluir** para inativar um cadastro; folhas já processadas são preservadas no histórico.

## 4. Processar e consultar folha

Na página **Folha de pagamento**, selecione **Mês** e **Ano** e clique em **Consultar competência**. Quando a competência ainda não foi processada, use **Processar folha desta competência**. O processamento cria snapshots históricos e é idempotente para a mesma competência.

Use **Imprimir folha** para abrir a impressão do relatório consolidado. Para consultar uma pessoa, clique no nome na folha e, no demonstrativo individual, use **Imprimir demonstrativo**. A impressão pode ser enviada à impressora ou salva em PDF pelo navegador.

## 5. Encerrar o ambiente

Para parar os serviços sem apagar o volume persistente:

```powershell
docker compose down
```

Não use `docker compose down -v` se precisar preservar os dados locais.
