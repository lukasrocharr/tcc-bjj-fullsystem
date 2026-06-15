# BJJ Full System

Sistema completo para academia de Brazilian Jiu-Jitsu com landing page Angular e backend de gestão.

Este repositório reúne:

- **Frontend Angular 18**: landing page responsiva com animações, formulário e galeria.
- **Backend Node.js/Express**: API simples com dados em JSON para loja, produtos, pedidos e aulas.
- **Backend Spring Boot**: versão Java avançada do backend para gestão completa de academia e e-commerce.

## 🚀 Visão Geral

### Frontend
- Angular 18
- Componentes standalone
- Responsividade para mobile/tablet/desktop
- Scroll reveal e animações suaves
- Formulário de contato e menu móvel

### Backend Node.js
- Express + CORS + body-parser
- Armazenamento em arquivos JSON em `backend/data/`
- Endpoints de produtos, pedidos, aulas, transações e admin
- Projeto leve para desenvolvimento local

### Backend Spring Boot
- Backend Java moderno em `backend-springboot/`
- Spring Boot 3 + Spring Security + JWT + Flyway
- APIs para autenticação, gestão de usuários, turmas, finanças, loja e relatórios
- Perfil H2 para desenvolvimento rápido

## ⚙️ Pré-requisitos

- Node.js 18+ / npm 9+
- Angular CLI 18+ (opcional se usar `npm start`)
- Java 17+ (para backend Spring Boot)
- Maven (para backend Spring Boot)

## 💻 Como executar

### 1. Frontend Angular

No diretório do projeto principal:

```bash
cd tcc-bjj-fullsystem-main
npm install
npm start
```

Abra em:

```text
http://localhost:4200
```

### 2. Backend Node.js (opcional)

```bash
cd backend
npm install
npm start
```

API disponível em:

```text
http://localhost:3000
```

### 3. Backend Spring Boot (opcional)

```bash
cd backend-springboot
mvn spring-boot:run -Dspring-boot.run.profiles=dev-h2
```

API disponível em:

```text
http://localhost:8080
```

## 📁 Estrutura do projeto

```text
.
├── backend/                  # API Node.js com dados JSON
│   ├── data/                 # JSON de produtos, pedidos, aulas, transações e admin
│   ├── package.json
│   └── server.js
├── backend-springboot/       # Backend Java Spring Boot avançado
├── src/                      # Frontend Angular
│   ├── app/
│   │   ├── components/       # Componentes da landing page
│   │   ├── services/         # Serviços de scroll e reveal
│   │   ├── app.component.*
│   ├── assets/
│   ├── environments/
│   ├── index.html
│   └── main.ts
├── angular.json
├── package.json
├── tsconfig.json
├── README.md
├── SETUP.md
├── QUICKSTART.md
└── STRUCTURE.md
```

## ✅ Funcionalidades principais

- Menu fixo e responsivo
- Navegação mobile com hamburguer
- Animações de scroll e efeito reveal
- Botão voltar ao topo
- Loader inicial animado
- Seções de hero, sobre, modalidades, galeria, professor e contato
- Formulário de contato com validação
- Backend de loja, pedidos e gestão escolar

## 🚧 Comandos úteis

```bash
npm install
npm start
npm run build
ng test
```

### Backend Node.js

```bash
cd backend
npm install
npm start
```

### Backend Spring Boot

```bash
cd backend-springboot
mvn clean test
mvn spring-boot:run -Dspring-boot.run.profiles=dev-h2
```

## 🔧 Dependências principais

### Frontend
- `@angular/core` ^18.0.0
- `@angular/common` ^18.0.0
- `@angular/forms` ^18.0.0
- `rxjs` ^7.8.0
- `zone.js` ^0.14.0

### Backend Node.js
- `express` ^4.18.2
- `cors` ^2.8.5
- `body-parser` ^1.20.2
- `uuid` ^9.0.0

## 📝 Observações

- O backend Node.js em `backend/` é ideal para desenvolvimento local leve.
- O backend Spring Boot em `backend-springboot/` é a implementação mais completa e atual.
- Se quiser apenas ver o frontend, execute somente o Angular em `src/`.

## 📌 Personalização rápida

- Mude cores em `src/styles.css`
- Atualize textos e imagens em `src/app/components/*`
- Modifique logo e títulos em `src/index.html`
- Adicione novos produtos/serviços nos arquivos JSON do backend Node.js

## 📚 Documentação adicional

- `SETUP.md` — passo a passo de setup
- `QUICKSTART.md` — início rápido
- `STRUCTURE.md` — organização da aplicação
- `TROUBLESHOOTING.md` — solução de problemas
- `BACKEND_GUIDE.md` — detalhes do backend e e-commerce

---

Se quiser, posso também gerar um `README.md` mais focado apenas no frontend ou apenas no backend Spring Boot.
