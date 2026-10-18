# AV2-DEV-WEB3

Projeto acadêmico da disciplina **Desenvolvimento Web 3** — microsserviço de cadastro de clientes construído com **Spring Boot**.

## Sobre o projeto

O `automanager` é uma API REST responsável pelo cadastro e gerenciamento de dados de clientes, incluindo seus documentos, endereços e telefones. Cada cliente possui um endereço (relação 1:1) e pode ter múltiplos documentos e telefones (relação 1:N).

A API implementa os quatro níveis do Modelo de Maturidade de Richardson (RMM):

| Nível | Requisito | Como está no projeto |
|-------|-----------|----------------------|
| 0 | Uso do protocolo HTTP | API REST trocando JSON sobre HTTP |
| 1 | URI única por recurso | `/clientes`, `/documentos`, `/enderecos` e `/telefones`, cada um com `/{id}` |
| 2 | Métodos e suas semânticas | GET, POST, PUT e DELETE com status HTTP corretos (ver [Respostas HTTP](#respostas-http-rmm-nível-2)) |
| 3 | HATEOAS | Respostas com links para o próprio recurso, para a lista e para recursos relacionados (ver [HATEOAS](#hateoas-rmm-nível-3)) |

## Tecnologias

- Java 17
- Spring Boot 2.6.3
- Spring Data JPA / Spring Data JDBC
- Spring HATEOAS
- Banco de dados H2 (em memória, usado em runtime)
- Bean Validation (`javax.validation`)
- Lombok
- Maven (com wrapper `mvnw`)

## Estrutura do projeto

```
atvi-autobots-microservico-spring/automanager
├── src/main/java/com/autobots/automanager
│   ├── controles/        # Controllers REST (Cliente, Documento, Endereco, Telefone)
│   ├── Servicos/         # Regras de negócio
│   ├── repositorios/     # Interfaces JPA Repository
│   ├── entidades/        # Entidades JPA (Cliente, Documento, Endereco, Telefone)
│   ├── DTO/Request/      # DTOs de entrada (criação/atualização)
│   ├── DTO/Response/     # DTOs de saída (estendem RepresentationModel para carregar links)
│   ├── Mappers/          # Conversão entre entidades e DTOs
│   ├── modelos/          # Adicionadores de links HATEOAS (AdicionadorLink<T> e um por recurso)
│   └── exceptions/       # Tratamento global de erros
└── src/test/             # Testes automatizados
```

## Como executar

Pré-requisitos: JDK 17 e Maven (ou use o wrapper incluso).

```bash
cd atvi-autobots-microservico-spring/automanager
./mvnw spring-boot:run
```

A aplicação sobe por padrão em `http://localhost:8080`.

Se não conseguir rodar, execute `./mvnw clean` antes e tente novamente.

## Endpoints da API

### Clientes

| Método | Rota            | Descrição                  |
|--------|-----------------|-----------------------------|
| GET    | `/clientes`     | Lista todos os clientes     |
| GET    | `/clientes/{id}`| Busca um cliente por ID     |
| POST   | `/clientes`     | Cadastra um novo cliente    |
| PUT    | `/clientes`     | Atualiza um cliente         |
| DELETE | `/clientes/{id}`| Remove um cliente           |

### Documentos

| Método | Rota              | Descrição                |
|--------|-------------------|----------------------------|
| GET    | `/documentos`     | Lista todos os documentos  |
| GET    | `/documentos/{id}`| Busca um documento por ID  |
| POST   | `/documentos`     | Cadastra um novo documento |
| PUT    | `/documentos`     | Atualiza um documento      |
| DELETE | `/documentos/{id}`| Remove um documento        |

### Endereços

| Método | Rota             | Descrição                 |
|--------|------------------|-----------------------------|
| GET    | `/enderecos`     | Lista todos os endereços    |
| GET    | `/enderecos/{id}`| Busca um endereço por ID    |
| POST   | `/enderecos`     | Cadastra um novo endereço   |
| PUT    | `/enderecos`     | Atualiza um endereço        |
| DELETE | `/enderecos/{id}`| Remove um endereço          |

### Telefones

| Método | Rota             | Descrição                 |
|--------|------------------|-----------------------------|
| GET    | `/telefones`     | Lista todos os telefones    |
| GET    | `/telefones/{id}`| Busca um telefone por ID    |
| POST   | `/telefones`     | Cadastra um novo telefone   |
| PUT    | `/telefones`     | Atualiza um telefone        |
| DELETE | `/telefones/{id}`| Remove um telefone          |

## Respostas HTTP (RMM nível 2)

Todos os controllers devolvem `ResponseEntity` com o status adequado a cada operação:

| Operação | Sucesso | Falha |
|----------|---------|-------|
| GET (lista) | `200 OK` com a lista | n/a |
| GET (por id) | `200 OK` com o recurso | `404 Not Found` se o id não existe |
| POST | `201 Created`, sem corpo, com o header `Location` apontando para o novo recurso | `400 Bad Request` se os dados são inválidos; `404 Not Found` se o cliente informado (telefone e documento) não existe |
| PUT | `204 No Content` | `404 Not Found` se o id não existe; `400 Bad Request` se os dados são inválidos |
| DELETE | `204 No Content` | `404 Not Found` se o id não existe |

O corpo de erro tem os campos `titulo` e `message`. Erros de validação (`400`) devolvem um objeto com o nome de cada campo inválido e a mensagem correspondente.

## HATEOAS (RMM nível 3)

As respostas de GET trazem links para navegar entre os recursos. Os links são montados com Spring HATEOAS, por um adicionador em `modelos/` para cada recurso (`AdicionadorLinkCliente`, `AdicionadorLinkDocumento`, `AdicionadorLinkEndereco` e `AdicionadorLinkTelefone`), todos implementando a interface `AdicionadorLink<T>`.

| Recurso | Links no item (`GET /{recurso}/{id}`) | Links na lista (`GET /{recurso}`) |
|---------|---------------------------------------|-----------------------------------|
| Cliente | `self`, `clientes`, `endereco`; cada telefone e cada documento do cliente traz o seu `self` | `self` em cada cliente |
| Telefone | `self`, `telefones`, `cliente` (dono do telefone) | `self` em cada telefone |
| Documento | `self`, `documentos`, `cliente` (dono do documento) | `self` em cada documento |
| Endereço | `self`, `enderecos` | `self` em cada endereço |

Exemplo, `GET /telefones/1`:

```json
{
  "id": 1,
  "ddd": "21",
  "numero": "981234576",
  "cliente": 1,
  "_links": {
    "self": { "href": "http://localhost:8080/telefones/1" },
    "telefones": { "href": "http://localhost:8080/telefones" },
    "cliente": { "href": "http://localhost:8080/clientes/1" }
  }
}
```

O item individual responde em `application/hal+json`, com os links em `_links`. Na listagem, os links de cada item ficam no campo `links`.

## Modelo de dados

- **Cliente**: nome, nome social, data de nascimento, data de cadastro, endereço (1:1), documentos (1:N), telefones (1:N)
- **Documento**: tipo, número (único), vinculado a um cliente
- **Endereco**: estado, cidade, bairro, rua, número, código postal, informações adicionais
- **Telefone**: DDD, número, vinculado a um cliente

## Autor

Hugo Perestrelo — Fatec São José dos Campos, DSM.