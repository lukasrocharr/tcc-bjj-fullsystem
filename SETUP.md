# 🚀 Guia de Setup - BJJ Landing Page Angular

## Pré-requisitos

- **Node.js**: v18+ (v20 recomendado)
- **npm**: v9+ ou **yarn** v3+
- **Angular CLI**: ^18.0.0 (será instalado via npm)

## Instalação Passo a Passo

### 1️⃣ Abra o terminal

Navegue até a pasta do projeto:

```bash
cd "caminho/do/projeto/TCC"
```

### 2️⃣ Instale o Angular CLI globalmente (se não tiver)

```bash
npm install -g @angular/cli@18
```

### 3️⃣ Instale as dependências do projeto

```bash
npm install
```

Isto vai instalar:
- Angular 18
- RxJS 7
- Zone.js
- Dependências de desenvolvimento

### 4️⃣ Inicie o servidor de desenvolvimento

```bash
npm start
```

Ou use o comando direto:

```bash
ng serve
```

### 5️⃣ Abra no navegador

```
http://localhost:4200
```

Você deve ver a landing page carregando com as animações!

## 🏗️ Estrutura do Projeto Explicada

```
TCC/
├── src/
│   ├── app/
│   │   ├── components/           # Componentes reutilizáveis
│   │   │   ├── navbar/           # Barra de navegação
│   │   │   ├── hero/             # Seção hero
│   │   │   ├── sobre/            # Seção sobre
│   │   │   ├── modalidades/      # Seções de modalidades
│   │   │   ├── galeria/          # Galeria de imagens
│   │   │   ├── professor/        # Seção de professores
│   │   │   ├── contato/          # Seção e formulário de contato
│   │   │   ├── footer/           # Rodapé
│   │   │   ├── loader/           # Animação de carregamento
│   │   │   ├── back-to-top/      # Botão voltar ao topo
│   │   │   └── divider-marquee/  # Seção com texto deslizante
│   │   ├── services/             # Serviços
│   │   │   ├── scroll.service.ts # Gerencia eventos de scroll
│   │   │   └── reveal.service.ts # Gerencia animações de reveal
│   │   ├── app.component.*       # Componente raiz
│   ├── styles.css                # Estilos globais
│   ├── main.ts                   # Ponto de entrada
│   ├── index.html                # HTML principal
│   └── environments/             # Configurações por ambiente
├── angular.json                  # Configuração do Angular CLI
├── tsconfig.json                 # Configuração TypeScript
├── package.json                  # Dependências do projeto
└── README.md                      # Documentação geral
```

## 🎯 Componentes Standalone

Este projeto usa **Angular Standalone Components** (Angular 14+), ou seja:

- ✅ Não usa `NgModule`
- ✅ Cada componente é independente
- ✅ Imports diretos nos decoradores `@Component`
- ✅ Mais performático e moderno

Exemplo:

```typescript
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
```

## 🔧 Tarefas Comuns

### Adicionar um novo componente

```bash
ng g c components/novo-componente --standalone
```

### Compilar para produção

```bash
ng build --configuration production
```

O resultado estará em `dist/bjj-landing/`

### Executar testes (se tiver)

```bash
ng test
```

### Criar um novo serviço

```bash
ng g s services/novo-servico
```

## 🎨 Personalizações Rápidas

### Alterar cores

Edite `src/styles.css`:

```css
:root {
  --gold: #c9a84c;      /* Cor principal */
  --black: #0a0a0a;     /* Fundo */
  --white: #f0ede6;     /* Texto */
}
```

### Mudar título da página

Edite `src/index.html`:

```html
<title>Sua Logo – Brazilian Jiu-Jitsu</title>
```

### Adicionar WhatsApp/Contato

Edite `src/app/components/contato/contato.component.html`:

```html
<a href="https://wa.me/5511999999999" target="_blank" class="btn btn-gold">
  💬 WhatsApp
</a>
```

### Adicionar imagens

Coloque suas imagens em `src/assets/`

Depois use no template:

```html
<img src="assets/seu-professor.jpg" alt="Professor" />
```

## 🐛 Troubleshooting

### Erro: "ng: command not found"

```bash
npm install -g @angular/cli@18
```

### Erro: "Module not found"

```bash
npm install
rm -rf node_modules package-lock.json
npm install
```

### Porta 4200 já em uso

```bash
ng serve --port 4201
```

### Problemas com TypeScript

```bash
npm install -D typescript@5.4
```

## 📱 Testar Responsividade

Use o DevTools do navegador (F12):

1. Clique no ícone de dispositivo móvel
2. Teste em diferentes resoluções:
   - **Mobile**: 360px
   - **Tablet**: 768px
   - **Desktop**: 1200px+

## 🚀 Deploy

### Deploy na Vercel (Recomendado)

1. Instale Vercel CLI: `npm i -g vercel`
2. Na pasta do projeto: `vercel`
3. Siga as instruções

### Deploy no GitHub Pages

Edite `angular.json`:

```json
"outputPath": "docs/"
```

Compile:

```bash
ng build --configuration production
```

Faça commit e push. Ative GitHub Pages nas settings.

## 📚 Referências

- [Angular Docs](https://angular.io/docs)
- [RxJS Documentation](https://rxjs.dev/)
- [Standalone Components Guide](https://angular.io/guide/standalone-components)

---

**Desenvolvido com ❤️ para o tatame** 🥋
