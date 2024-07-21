## MIgrações de Banco de Dados
As tabelas do banco de dados para o projeto Planner são criadas utilizando scripts de migração do Flyway. Esses scripts estão localizados no diretório `/src/main/resources/db/migration/`.

### Script de Migração Inicial
Os arquivo devem ser iniciados com "V" maiúsculo, seguido de um número sequencial, duas underline e o nome do arquivo, por exemplo: `V1__nome-do-arquivo.sql`. Neste arqiovo deve conter o script SQL que cria a tabela que armazenará as informações necessárias.

### Como Executar as Migrações
As migrações do banco de dados (criação da tabela no banco) são executadas automaticamente pelo Flyway ao iniciar a aplicação. Para que a migração corra como desejado, as configurações do Flyway devem estar corretas dentro do  ``application.properties` ou `application.yml` do projeto.