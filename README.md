## Migrações de Banco de Dados
As tabelas do banco de dados para o projeto Planner são criadas utilizando scripts de migração do Flyway. Esses scripts estão localizados no diretório `/src/main/resources/db/migration/`.

### Script de Migração Inicial
Os arquivo devem ser iniciados com "V" maiúsculo, seguido de um número sequencial, duas underline e o nome do arquivo, por exemplo: `V1__nome-do-arquivo.sql`. Neste arqiovo deve conter o script SQL que cria a tabela que armazenará as informações necessárias.

### Como Executar as Migrações
As migrações do banco de dados (criação da tabela no banco) são executadas automaticamente pelo Flyway ao iniciar a aplicação. Para que a migração corra como desejado, as configurações do Flyway devem estar corretas dentro do  ``application.properties` ou `application.yml` do projeto.

## Arquivo Repository
O arquivo `Repository.java` é uma interface que faz parte da camada de persistência da aplicação e é responsável por fornecer métodos para interagir com a tabela correspondente criada no banco de dados.

Ele estende a interface `JpaRepository`- é uma das interfaces fornecidas pelo `Spring Data JPA` que fornece uma implementação padrão para operações básicas de acesso a dados, que ao ser estendido para o `Repository.java` herda uma série de metódos que permitem realizar operações de banco de dados com a entidade(arquivo responsável pela tabela no banco de dados) - e fornece uma maneira fácil e conveniente de realizar operações CRUD sem a necessidade de implementar o código de acesso a dados manualmente.

## Arquivo da Entidade 
A Entidade representa a tabela no banco de dados, neste caso as entidades são `Participants` e `Trips`, onde são definidas classes Java que mapeiam as tabelas correspondentes no banco de dados usando o JPA. Siginifica que cada instância da classe representa uma linha na tabela, exemplo de uma linha na tabela de `Participants`:
```
  @Column(name = "is_confirmed", nullable = false)
  private boolean isConfirmed;
```
### Anotações JPA
As anotações JPA nos arquivos facilitam o mapeamento e gerenciamento das entidades e tabelas, permitindo que os dados sejam trabalhados de forma mais intuitiva e estruturada, definindo como o JAVA deve interagir com o banco de dados. Exemplos:
`@Entity:` - marca a classe como uma entidade JPA;
`@Table(name = "table_name")` - define nome da tabela;
`@Id` - indica o campo que será a chave primaria da tabela;
`@Column(name = "column_name", nullable = false)` - define características das colunas da tabela, como nome e se podem ser nulas;
`@ManyToOne, @OneToMany, @OneToOne, @ManyToMany:` - definem relacionamentos entre entidades.
Entre outros.
