# 💳 Di2win Account – Sistema de Conta Digital (Backend)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=java&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791?logo=postgresql&logoColor=white)](#)
[![OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?logo=swagger&logoColor=white)](#)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](#)

Sistema backend que **simula uma conta bancária online para clientes**.  
Permite **cadastrar cliente/usuário**, **abrir conta**, **autenticar por CPF e senha**, **realizar depósito/saque/transferência**, **consultar saldo**, **emitir extrato por período**, além de **bloquear/desbloquear** a conta.  
A API é documentada com **Swagger (OpenAPI)**.

> **Stack principal:** Java 21 · Spring Boot 3 · Spring Web · Spring Data JPA · PostgreSQL · BCrypt (Spring Security) · springdoc-openapi (Swagger)

---

## 🧭 Sumário

- [✨ Funcionalidades](#-funcionalidades)
- [📐 Regras de Negócio](#-regras-de-negócio)
- [⚙️ Como o sistema opera (visão técnica)](#️-como-o-sistema-opera-visão-técnica)
- [🏗️ Arquitetura (MVC em camadas)](#️-arquitetura-mvc-em-camadas)
- [🧰 Tecnologias e bibliotecas](#-tecnologias-e-bibliotecas)
- [🔐 Segurança](#-segurança)
- [🗄️ Modelo de dados (visão geral)](#️-modelo-de-dados-visão-geral)
- [📚 Exemplos de uso (endpoints & payloads)](#-exemplos-de-uso-endpoints--payloads)
- [🔮 Expansões futuras](#-expansões-futuras)
- [▶️ Como rodar o projeto (passo a passo)](#️-como-rodar-o-projeto-passo-a-passo)


---

## ✨ Funcionalidades

1. **Cadastro de cliente/usuário + abertura de conta**  
   Cria cliente, usuário (senha hasheada via BCrypt) e conta inicial com saldo `0.00`.

2. **Autenticação (CPF + senha)**  
   Valida CPF e confere senha com BCrypt.

3. **Depósito**  
   Valor positivo → crédito em conta + lançamento `DEPOSIT`.

4. **Saque**  
   Valor positivo → valida saldo e limite diário de R$ 2.000,00 → lançamento `WITHDRAWAL`.

5. **Transferência**  
   Origem → destino (valida saldo) → `TRANSFER_MADE` e `TRANSFER_RECEIVED` transacionais.

6. **Saldo**  
   Consulta do saldo atual.

7. **Extrato por período**  
   Lista operações entre datas.

8. **Bloquear/Desbloquear conta**  
   Bloqueio impede débitos, desbloqueio reativa.

---

## 📐 Regras de Negócio

- CPF único por cliente.  
- Senha ≥ 6 caracteres, com letras e números.  
- Senhas nunca em texto puro (hash BCrypt).  
- Saque diário limitado a R$ 2.000,00.  
- Nunca permite saldo negativo.  
- Conta bloqueada impede débitos.  
- Valores monetários com `BigDecimal` (2 casas decimais).  

---

## ⚙️ Como o sistema opera (visão técnica)

- Persistência no PostgreSQL (Spring Data JPA).  
- Fluxo: Controllers → Services (regras) → Repositories → DB.  
- Saques/transferências são transacionais.  
- Cada operação gera log (`Operation`) com `timestamp`, `type`, `value`, `counterparty`, `balance_after`.  
- Documentação disponível em Swagger UI.  

---

## 🏗️ Arquitetura (MVC em camadas)

- **Controller** → camada que expõe os **endpoints REST**, recebe as requisições HTTP e delega para os Services.  
- **DTO (Data Transfer Object)** → objetos simples usados para **transportar dados** entre as camadas, evitando expor diretamente as entidades do domínio.  
- **Entity (Entidade)** → classes que representam as **tabelas do banco de dados**, mapeadas com JPA/Hibernate.  
- **Repository** → interfaces do Spring Data JPA que fazem o **acesso ao banco de dados**, com métodos prontos ou consultas personalizadas.  
- **Service** → camada que concentra as **regras de negócio** e orquestra as operações entre Controllers, Repositories e demais componentes.  
- **Exception/Handler** → classes usadas para **padronizar os erros** da aplicação e melhorar a resposta para o cliente.  


---

## 📖 Documentação disponível

1. **README.md (este arquivo)**  
   - Explica detalhadamente o sistema, suas funcionalidades, regras de negócio, arquitetura, tecnologias e como rodar o projeto.  
   - Serve como guia principal para desenvolvedores ou avaliadores entenderem rapidamente o funcionamento da aplicação.  

2. **Swagger UI (OpenAPI)**  
   - Disponível em: `http://localhost:8080/swagger-ui/index.html` (ou na porta configurada em `application.properties`).  
   - Fornece documentação interativa dos **endpoints REST**: é possível visualizar as rotas, os métodos HTTP, os parâmetros de entrada e os modelos de resposta.  
   - Foram utilizadas anotações como `@Operation` e `@Schema(description = "...")` para enriquecer a descrição dos endpoints e DTOs, ajudando quem for testar a API a entender melhor cada campo e operação.  



---

## 🧰 Tecnologias e bibliotecas

- Java 21  
- Spring Boot 3.x (Web, Data JPA)  
- PostgreSQL  
- Spring Security (BCryptPasswordEncoder)  
- springdoc-openapi (Swagger UI)  
- Maven  

---

## 🔐 Segurança

- BCrypt para senhas.  
- Neste MVP, endpoints ainda não exigem autenticação/autorização plena.  
- Próximas versões incluirão JWT e roles.  

---

## 🗄️ Modelo de dados (visão geral)

- **clients**: id, name, cpf (unique), birth_date, created_at  
- **users**: id, cpf, password_hash, client_id (FK)  
- **accounts**: id, number, agency, balance, blocked, client_id (FK)  
- **operations**: id, account_id (FK), type, value, counterparty, timestamp, balance_after  

Tipos: `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_MADE`, `TRANSFER_RECEIVED`.  

---

## 🔮 Expansões futuras

- Autenticação/autorização com Spring Security (JWT/roles).  
- Recuperação de senha via SMS/e-mail.  
- Suporte a múltiplas contas por cliente (já previsto em alguns retornos/DTOs).  

---

## ▶️ Como rodar o projeto (passo a passo)

### Pré-requisitos
- Java 21 (JDK)  
- Maven 3.9+  
- PostgreSQL 14+  
- IDE (IntelliJ/Eclipse/STS) ou apenas terminal  

### Clonar repositório
```bash
git clone https://github.com/phabp/di2winaccount.git
cd di2winaccount

```

### Criar banco no Postgre 
CREATE DATABASE di2winaccount_database ENCODING 'UTF8';
CREATE USER di2win WITH ENCRYPTED PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE di2winaccount_database TO di2win;


### Configurar application.properties (arquivo em: main/resources) :

spring.datasource.url=jdbc:postgresql://localhost:5432/di2winaccount_database
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
springdoc.swagger-ui.enabled=true

### Rodar o projeto em uma IDE:
- Importar projeto Maven;
- Rodar (run) D2WinaccountApplication

## Swagger UI:
- http://localhost:8080/swagger-ui/index.html       [Por padrão, no Spring Boot a aplicação sobe na porta 8080] 


  











