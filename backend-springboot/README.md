# BJJ Academy — Backend (Spring Boot)

Backend do *Sistema de Gestão de Academia de BJJ + E-commerce* (TCC), reconstruído
em **Java + Spring Boot 3** conforme o prompt mestre. Este README cobre a **Fase 1
— Fundação** (autenticação completa, segurança, base de schema e documentação de API).

> Este diretório (`backend-springboot/`) é o **novo backend canônico**. O backend
> Node.js/Express anterior (`../backend/`) foi **preservado** como referência/legado
> e não é mais usado.

---

## Decisões e suposições assumidas

| Item | Decisão | Motivo |
|---|---|---|
| **Java 17** (e não 21) | Alvo de compilação 17 | É a JDK instalada na máquina (Temurin 17) e o baseline do Spring Boot 3. Para usar 21, basta instalar a JDK 21 e alterar `<java.version>` no `pom.xml`. |
| **Stack flexível** | Mantida a stack Java exigida pelo prompt | Confirmado pelo autor que a stack é flexível, mas optou-se pela reconstrução em Spring Boot. |
| **Faixa atual / papel no auto-registro** | `POST /auth/register` concede sempre `ROLE_ALUNO` | Papéis administrativos são atribuídos por um ADMIN em endpoints de gestão (fases futuras). |
| **Seed de usuários** | Via `DataInitializer` (idempotente) e não via SQL | Senhas precisam de hash BCrypt em runtime; papéis (dados de referência) vêm da migration `V2`. |
| **H2 em testes/dev** | Perfil `dev-h2` + testes usam H2 (modo PostgreSQL) | Permite rodar e testar sem um Postgres ativo. Migrations evitam dialetos específicos. |

---

## Stack

- Java 17 · Spring Boot 3.3 (Web, Data JPA, Security, Validation, Mail)
- PostgreSQL + Flyway (migrations) · H2 (testes/dev)
- JWT (jjwt) — access token (stateless) + refresh token (opaco, persistido, rotacionável)
- MapStruct (DTO ↔ entidade) · springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + Spring Security Test

---

## Como executar

### Opção A — Sem Docker, usando H2 (mais rápido para avaliar)

```bash
cd backend-springboot
mvn spring-boot:run -Dspring-boot.run.profiles=dev-h2
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Console H2: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:bjj`)

### Opção B — PostgreSQL local

Suba um Postgres (porta 5432, db `bjj_academy`, user/senha `bjj`/`bjj`) e:

```bash
cd backend-springboot
mvn spring-boot:run
```

### Opção C — Docker Compose (Postgres + backend)

> Requer Docker instalado (não disponível na máquina de desenvolvimento atual,
> portanto este caminho ainda não foi verificado localmente).

```bash
cd backend-springboot
docker compose up --build
```

### Testes

```bash
cd backend-springboot
mvn clean test
```

Resultado atual: **5 testes, 0 falhas** (3 unitários + 2 de integração ponta a ponta).

---

## Usuários de teste (seed)

Criados automaticamente no primeiro start (somente para desenvolvimento):

| E-mail | Senha | Papéis |
|---|---|---|
| `admin@bjj.local` | `Admin@123` | ADMIN, SUPER_ADMIN |
| `professor@bjj.local` | `Professor@123` | PROFESSOR |
| `aluno@bjj.local` | `Aluno@123` | ALUNO |

---

## Endpoints (Fase 1)

Base: `/api/v1`

| Método | Caminho | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/register` | público | Registra usuário (ROLE_ALUNO) e retorna tokens |
| POST | `/auth/login` | público | Login por e-mail/senha → access + refresh |
| POST | `/auth/refresh` | público | Rotaciona o refresh token |
| POST | `/auth/logout` | público | Revoga o refresh token informado |
| POST | `/auth/forgot-password` | público | Envia token de redefinição (sempre 204) |
| POST | `/auth/reset-password` | público | Redefine a senha via token |
| GET | `/auth/me` | autenticado | Dados do usuário logado |
| GET | `/public/health` | público | Health check |

### Núcleo da Academia (Fase 2 — RF-041 a RF-055)

Base: `/api/v1`. Leitura do catálogo é pública; escritas exigem **ADMIN/SUPER_ADMIN**.

| Recurso | Endpoints | Acesso |
|---|---|---|
| Modalidades | `GET /modalidades`, `GET /modalidades/{id}` | público |
| | `POST/PUT/DELETE /modalidades` | ADMIN |
| Planos | `GET /planos`, `GET /planos/{id}` | público |
| | `POST/PUT/DELETE /planos` | ADMIN |
| Professores | `GET /professores`, `GET /professores/{id}` | público |
| | `POST/PUT/DELETE /professores` | ADMIN |
| Turmas | `GET /turmas`, `GET /turmas/{id}`, `GET /turmas/grade` | público |
| | `POST/PUT/DELETE /turmas` | ADMIN |
| Alunos | `GET /alunos`, `GET /alunos/{id}` | ADMIN/PROFESSOR |
| | `POST/PUT/DELETE /alunos` | ADMIN |
| Matrículas | `GET /matriculas`, `GET /matriculas/{id}` | ADMIN/PROFESSOR |
| | `POST /matriculas`, `PATCH /matriculas/{id}/status` | ADMIN |
| Lista de espera | `POST /matriculas/lista-espera`, `GET /matriculas/lista-espera/{turmaId}` | ADMIN / staff |

Todas as listagens aceitam paginação (`?page=0&size=20&sort=nome`) e filtros
(`/turmas?modalidadeId=1&ativo=true`, `/matriculas?alunoId=1&status=ATIVA`, `/alunos?nome=joao`).

### Frequência e Graduação (Fase 3 — RF-056 a RF-062, RF-068 a RF-073)

| Recurso | Endpoints | Acesso |
|---|---|---|
| Check-in | `POST /frequencia/check-in` (valida matrícula + dia + janela de horário) | autenticado |
| Chamada | `POST /frequencia/chamada` (lote pelo professor) | ADMIN/PROFESSOR |
| Indicadores | `GET /frequencia/aluno/{id}`, `GET /frequencia/aluno/{id}/historico` | autenticado |
| Alerta baixa freq. | `GET /frequencia/alertas-baixa` | ADMIN |
| Faixas (catálogo) | `GET /graduacoes/faixas` | público |
| Graduação | `POST /graduacoes` (atualiza faixa atual + histórico) | ADMIN/PROFESSOR |
| Histórico/faixa atual | `GET /graduacoes/aluno/{id}`, `GET /graduacoes/aluno/{id}/faixa-atual` | autenticado |
| Elegíveis | `GET /graduacoes/elegiveis` | ADMIN/PROFESSOR |
| Certificado PDF | `GET /graduacoes/{id}/certificado` → `application/pdf` | ADMIN/PROFESSOR |

Job agendado diário (`AlertasJob`, 08:00) dispara alertas de baixa frequência (RF-061)
e elegibilidade de graduação (RF-072) via `NotificationService`. Configurável em
`app.frequencia.*` e `app.graduacao.*` (janela de check-in, dias de baixa frequência,
dias mínimos de elegibilidade, cron).

### Financeiro (Fase 4 — RF-074 a RF-082)

Base `/api/v1/financeiro`. Escrita = ADMIN; leitura = ADMIN/PROFESSOR.

| Recurso | Endpoints | Acesso |
|---|---|---|
| Geração | `POST /mensalidades/gerar` (1 por matrícula ativa/competência, sem duplicar) | ADMIN |
| Listagem/consulta | `GET /mensalidades?alunoId&status`, `GET /mensalidades/{id}` | ADMIN/PROFESSOR |
| Pagamento | `POST /mensalidades/{id}/pagar` (gateway mock; aprova → PAGA) | ADMIN/PROFESSOR |
| Cancelamento | `POST /mensalidades/{id}/cancelar` | ADMIN |
| Atrasadas | `POST /atualizar-atrasadas` (marca ATRASADA + multa/juros) | ADMIN |
| Bloqueio | `GET /aluno/{id}/bloqueado` (inadimplência > limite) | ADMIN/PROFESSOR |
| Recibo PDF | `GET /mensalidades/{id}/recibo` → `application/pdf` | ADMIN/PROFESSOR |
| Relatório | `GET /relatorio?ano&mes` (recebido/pendente/atrasado + total loja) | ADMIN |

Jobs (`MensalidadeJob`): geração mensal (dia 1, 02:00) e atualização de atrasadas
(diária, 03:00). Gateway de pagamento abstraído (`PaymentGatewayService`) com
implementação `MockPaymentGatewayService` (sempre aprova) — trocável via
`payment.provider`. Multa/juros/vencimento/bloqueio configuráveis em `app.financeiro.*`.

### E-commerce (Fase 5 — RF-011 a RF-040)

Base `/api/v1/loja`. Catálogo é leitura pública; carrinho/checkout funcionam para
visitante (header `X-Session-Id`) ou usuário autenticado; gestão é ADMIN.

| Recurso | Endpoints | Acesso |
|---|---|---|
| Catálogo | `GET /categorias`, `GET /produtos?categoriaId&busca`, `GET /produtos/{id}` | público |
| Produtos/variações | `POST/PUT/DELETE /produtos`, `POST /produtos/{id}/variacoes`, `PUT/DELETE /variacoes/{id}` | ADMIN |
| Estoque | `POST /variacoes/{id}/estoque` (ENTRADA/SAIDA + motivo) | ADMIN |
| Carrinho | `GET /carrinho`, `POST/PUT/DELETE /carrinho/itens/...` | público (sessão) ou logado |
| Checkout | `POST /checkout` (endereço, cupom, frete, pagamento) | público (sessão) ou logado |
| Webhook | `POST /webhook/pagamento` (confirma → PAGO + baixa estoque) | público |
| Meus pedidos | `GET /meus-pedidos` | autenticado |
| Gestão pedidos | `GET /admin/pedidos?status`, `GET /admin/pedidos/{id}`, `PATCH /admin/pedidos/{id}/status` | ADMIN |

Estoque vive na **variação**; o pedido grava o **preço praticado** (snapshot) e o
endereço de entrega. Pagamento aprovado → `PAGO` + **baixa de estoque**; cancelamento
de pedido já pago **devolve** o estoque. Frete via `ShippingService`
(`FixedRateShippingService` mock por região do CEP, grátis acima de R$ 300); cupom
percentual/fixo com validade e subtotal mínimo.

### Refinamento e Relatórios (Fase 6 — RF-091 a RF-095, RF-103, RF-104)

| Recurso | Endpoints | Acesso |
|---|---|---|
| Dashboard | `GET /dashboard` (alunos ativos, novas matrículas, receita mês, inadimplência, risco de evasão, série de receita 6 meses) | ADMIN |
| Notificações in-app | `GET /notificacoes`, `GET /notificacoes/nao-lidas/contagem`, `PATCH /notificacoes/{id}/lida`, `PATCH /notificacoes/lidas` | autenticado |
| Comunicado em massa | `POST /notificacoes/comunicados` (todos ou por papel; opcional e-mail) | ADMIN |
| Auditoria | `GET /auditoria` (log de ações sensíveis) | ADMIN |
| Exportação | `GET /relatorios/alunos.csv`, `GET /relatorios/mensalidades.csv?ano&mes` | ADMIN |

O **log de auditoria** é populado automaticamente por um `AuditoriaFilter` que registra
toda mutação (POST/PUT/PATCH/DELETE) de usuário autenticado. O dashboard consolida
mensalidades + vendas da loja na receita do mês e na série de evolução.

### Exemplo rápido (curl)

```bash
# Login
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bjj.local","senha":"Admin@123"}'

# Usar o accessToken retornado:
curl -s http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## Configuração (variáveis de ambiente)

Definidas em `application.yml` com defaults para dev. Sobrescreva em produção:

| Variável | Default | Descrição |
|---|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Postgres local | Conexão com o banco |
| `JWT_SECRET` | (dev) | Chave HMAC Base64 (≥ 256 bits) |
| `JWT_ACCESS_MIN` | 30 | Validade do access token (min) |
| `JWT_REFRESH_DAYS` | 7 | Validade do refresh token (dias) |
| `MAX_LOGIN_ATTEMPTS` | 5 | Tentativas antes do bloqueio temporário |
| `LOCKOUT_MINUTES` | 15 | Duração do bloqueio |
| `CORS_ORIGINS` | `http://localhost:4200` | Origens permitidas (separadas por vírgula) |
| `NOTIFICATION_PROVIDER` | `console` | `console` (log) ou SMTP (fases futuras) |

---

## Mapeamento SRS → entregue nesta fase

| Requisito | Implementação |
|---|---|
| RF-096 — registro/login com e-mail e senha (BCrypt) | `AuthController`, `AuthServiceImpl`, `BCryptPasswordEncoder` |
| RF-097 — JWT access + refresh + endpoint de refresh | `JwtService`, `RefreshToken`, `/auth/refresh` |
| RF-098 — RBAC com 4 papéis | `PapelNome` (ALUNO/PROFESSOR/ADMIN/SUPER_ADMIN), `@EnableMethodSecurity` |
| RF-099 — sessão stateless + `/me` | `JwtAuthenticationFilter`, `SecurityConfig` |
| RF-100 — recuperação de senha via token | `forgot-password` / `reset-password`, `PasswordResetToken` |
| RF-101 — bloqueio após N tentativas | `Usuario.tentativasLogin` / `bloqueadoAte` em `AuthServiceImpl` |
| RF-102 (parcial) — notificações | `NotificationService` + `ConsoleNotificationService` (assíncrono) |
| Diretriz 4 — erros padronizados | `GlobalExceptionHandler` + `ApiError` |
| Diretriz 12 — Swagger | `OpenApiConfig`, `/swagger-ui.html` |

---

## Estrutura

```
backend-springboot/src/main/java/com/academia/bjj/
├── auth/
│   ├── controller/   AuthController
│   ├── dto/          Register/Login/Refresh/Reset... + AuthResponse, UsuarioResponse
│   ├── mapper/       UsuarioMapper (MapStruct)
│   ├── model/        Usuario, Papel, PapelNome, RefreshToken, PasswordResetToken
│   ├── repository/   *Repository (Spring Data JPA)
│   ├── security/     JwtService, JwtAuthenticationFilter, AppUserDetailsService, AppUserPrincipal
│   └── service/      AuthService (+ impl/AuthServiceImpl)
├── common/           exception/ (handler, ApiError, exceções), dto/PageResponse, web/HealthController
├── config/           SecurityConfig, OpenApiConfig, AppProperties, PropertiesConfig, DataInitializer
├── notificacao/      NotificationService (+ ConsoleNotificationService)
└── BjjAcademyApplication.java
```

---

## Próximas fases (roadmap do prompt mestre)

- ✅ **Fase 1** — Fundação (autenticação, segurança, base).
- ✅ **Fase 2** — Núcleo da Academia: Modalidade, Plano, Turma, Aluno, Professor, Matrícula (com capacidade de turma + lista de espera). Admin Angular migrado.
- ✅ **Fase 3** — Frequência (check-in self-service + chamada + indicadores + alertas) e Graduação (faixas configuráveis, histórico, faixa atual derivada, elegibilidade, certificado PDF).
- ✅ **Fase 4** — Financeiro: geração mensal de mensalidades (sem duplicidade), pagamentos via gateway mock, status automáticos com multa/juros, bloqueio por inadimplência, recibo PDF, relatório consolidado.
- ✅ **Fase 5** — E-commerce: catálogo (categorias/produtos/variações/imagens), estoque na variação, carrinho (visitante + logado), checkout com frete/cupom/gateway, webhook, baixa/devolução de estoque, gestão de pedidos.
- ✅ **Fase 6** — Dashboard administrativo + série de receita, exportação CSV, log de auditoria automático, notificações in-app e comunicados em massa. `mvn test` = 34/34 verdes.

**Backend completo (Fases 1–6).** Pendente: frontends das Fases 3/4/5/6 (portal do aluno, loja Angular, telas admin de frequência/graduação/financeiro/dashboard).

> O frontend Angular (raiz do repo) ainda aponta para o backend Node legado
> (`http://localhost:3000/api/admin`). A integração da Landing Page + guards/interceptor
> com este backend (endpoints `/api/v1/auth/*`) faz parte do fechamento da Fase 1 no
> frontend e será feita no próximo passo, mediante sua revisão.
```
