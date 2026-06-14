# Análise do Projeto BJJ Full System

## Estrutura do workspace
- Projeto mono-repo com frontend Angular, backend Node.js/Express legado e backend Java/Spring Boot principal.
- Diretórios principais:
  - `/src` - aplicativo Angular principal.
  - `/backend` - backend Node.js/Express legado.
  - `/backend-springboot` - backend Java/Spring Boot canônico.

## Frontend Angular
- Aplicação Angular standalone baseada em Angular 18.
- Dependências principais:
  - `@angular/core`, `@angular/common`, `@angular/forms`, `@angular/router` @ 18.x
  - `rxjs` 7.8
  - `typescript` ~5.4
- Scripts:
  - `npm start` → `ng serve`
  - `npm build` → `ng build`
  - `npm test` → `ng test`

## Backend Node.js/Express
- Diretório `/backend` contém servidor Express simples com dependências:
  - `express` 4.18.2
  - `cors` 2.8.5
  - `body-parser` 1.20.2
  - `uuid` 9.0.0
- Scripts de desenvolvimento:
  - `npm start` → `node server.js`
  - `npm run dev` → `nodemon server.js`
- Conclusão: backend Node existe como legado/refência e não é o backend principal do sistema.

## Backend Spring Boot
- Diretório principal para o sistema Java: `backend-springboot`.
- `pom.xml` usa Spring Boot 3.3.4 e Java 17 como baseline.
- Stack Java:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Security
  - Spring Boot Starter Validation
  - Spring Boot Starter Mail
  - Flyway Core + Flyway PostgreSQL
  - PostgreSQL JDBC
  - H2 runtime para testes/dev
  - JJWT 0.12.6 para JWT
  - MapStruct 1.5.5.Final
  - Springdoc OpenAPI 2.6.0
  - OpenPDF 1.3.43
  - Spring Boot Starter Test + Spring Security Test
- Build:
  - `spring-boot-maven-plugin`
  - `maven-compiler-plugin` com annotationProcessorPaths para MapStruct
- Containerização:
  - Dockerfile usa Maven 3.9 + Temurin 17 para build e Temurin 17 JRE em runtime.

## Configuração do backend Spring Boot
- `application.yml` configura datasource PostgreSQL com fallback para `jdbc:postgresql://localhost:5432/bjj_academy`.
- Habilita Flyway com `baseline-on-migrate: true`.
- JWT configurável via `JWT_SECRET`, expiração de access e refresh tokens.
- CORS default `http://localhost:4200`.
- Swagger UI exposto em `/swagger-ui.html`.

## Estrutura de código do backend Spring Boot
- Pacote raiz: `com.academia.bjj`
- Classe principal: `BjjAcademyApplication`
- Presença de configuração via `@Configuration`, `@Component`, `@Service`, `@Repository`, `@RestController`, `@Entity`, `@Mapper`.
- Componentes detectados:
  - Autenticação/JWT
  - Segurança Spring Security
  - Controller de saúde
  - Auditoria
  - Financeiro e geração de PDF
  - E-commerce
  - Frequência e check-in
  - Graduação e certificados
  - Notificações
  - Relatórios / dashboard
- Testes integrados Spring Boot presentes no pacote `src/test/java`.

## Observações de versão e compatibilidade
- Java alvo atual: 17
- Spring Boot atual: 3.3.4
- Ambiente Docker/Imagem também usa Java 17.
- Projeto já está numa linha moderna de Spring Boot 3 e não precisa de migração de Jakarta namespace para este nível.
- O README do backend Spring Boot confirma que o backend Java é o canônico e o backend Node é legado.

## Recomendações iniciais
- Priorizar análise e upgrades no `backend-springboot` se o objetivo for modernizar o backend Java.
- O frontend Angular está em versão recente e não parece exigir atualização imediata.
- O backend Node pode ser mantido como referência; não há necessidade de migrá-lo agora.
- Caso haja meta de Java 21/25, será necessário verificar compatibilidade de build tool e possivelmente ajustar o Dockerfile e o `java.version` no `pom.xml`.

## Captura para memória
- Projeto: TCC BJJ Full System
- Tipo: mono-repo Angular + Java Spring Boot + Node.js legado
- Backend Java principal: `backend-springboot`
- Spring Boot: 3.3.4
- Java versão do projeto: 17
- Ferramenta build: Maven 3.9 (via imagem Docker, sem wrapper no repo)
- Frontend: Angular 18
- Backend Node: Express 4.18
