# 📁 Estrutura Completa do Projeto

## Visualização da Árvore de Pastas

```
TCC/
│
├── 📁 src/
│   ├── 📁 app/
│   │   ├── 📁 components/
│   │   │   ├── 📁 navbar/
│   │   │   │   ├── navbar.component.ts
│   │   │   │   ├── navbar.component.html
│   │   │   │   └── navbar.component.css
│   │   │   ├── 📁 hero/
│   │   │   │   ├── hero.component.ts
│   │   │   │   ├── hero.component.html
│   │   │   │   └── hero.component.css
│   │   │   ├── 📁 divider-marquee/
│   │   │   │   ├── divider-marquee.component.ts
│   │   │   │   ├── divider-marquee.component.html
│   │   │   │   └── divider-marquee.component.css
│   │   │   ├── 📁 sobre/
│   │   │   │   ├── sobre.component.ts
│   │   │   │   ├── sobre.component.html
│   │   │   │   └── sobre.component.css
│   │   │   ├── 📁 modalidades/
│   │   │   │   ├── modalidades.component.ts
│   │   │   │   ├── modalidades.component.html
│   │   │   │   └── modalidades.component.css
│   │   │   ├── 📁 galeria/
│   │   │   │   ├── galeria.component.ts
│   │   │   │   ├── galeria.component.html
│   │   │   │   └── galeria.component.css
│   │   │   ├── 📁 professor/
│   │   │   │   ├── professor.component.ts
│   │   │   │   ├── professor.component.html
│   │   │   │   └── professor.component.css
│   │   │   ├── 📁 contato/
│   │   │   │   ├── contato.component.ts
│   │   │   │   ├── contato.component.html
│   │   │   │   └── contato.component.css
│   │   │   ├── 📁 footer/
│   │   │   │   ├── footer.component.ts
│   │   │   │   ├── footer.component.html
│   │   │   │   └── footer.component.css
│   │   │   ├── 📁 loader/
│   │   │   │   ├── loader.component.ts
│   │   │   │   ├── loader.component.html
│   │   │   │   └── loader.component.css
│   │   │   └── 📁 back-to-top/
│   │   │       ├── back-to-top.component.ts
│   │   │       ├── back-to-top.component.html
│   │   │       └── back-to-top.component.css
│   │   │
│   │   ├── 📁 services/
│   │   │   ├── scroll.service.ts
│   │   │   └── reveal.service.ts
│   │   │
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   └── app.component.css
│   │
│   ├── 📁 environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   │
│   ├── 📁 assets/
│   │   ├── 📁 images/
│   │   ├── 📁 icons/
│   │   └── 📁 fonts/
│   │
│   ├── main.ts
│   ├── index.html
│   ├── styles.css
│   └── favicon.ico
│
├── 📁 dist/                    # Build output (gerado)
│   └── 📁 bjj-landing/
│
├── 📁 node_modules/            # Dependências (gerado)
│
├── 📁 .angular/                # Cache do Angular CLI (gerado)
│
├── 📁 .vscode/                 # VS Code config (opcional)
│   ├── settings.json
│   └── launch.json
│
├── 📄 angular.json             # Config do Angular CLI
├── 📄 tsconfig.json           # Config TypeScript
├── 📄 tsconfig.app.json       # Config TS para app
├── 📄 package.json            # Dependências npm
├── 📄 package-lock.json       # Lock file (gerado)
├── 📄 README.md               # Info do projeto
├── 📄 SETUP.md                # Como configurar
├── 📄 EXEMPLOS.md             # Exemplos de customização
├── 📄 CHECKLIST.md            # Checklist de funcionalidades
├── 📄 TROUBLESHOOTING.md      # Guia de problemas
├── 📄 VSCODE_CONFIG.md        # Config VS Code
├── 📄 STRUCTURE.md            # Este arquivo
├── 📄 .gitignore              # Git ignore
└── 📄 index.html              # Original (opcional)
```

## Detalhamento de Pastas

### `/src`
Código-fonte principal da aplicação.

### `/src/app`
Componentes, serviços e lógica da aplicação Angular.

### `/src/app/components`
Componentes reutilizáveis organizados por funcionalidade.

Cada componente tem:
- `*.ts` - Lógica TypeScript
- `*.html` - Template
- `*.css` - Estilos específicos

### `/src/app/services`
Serviços que gerenciam lógica compartilhada:
- `scroll.service.ts` - Eventos de scroll da página
- `reveal.service.ts` - Animações de reveal (Intersection Observer)

### `/src/environments`
Configurações específicas por ambiente:
- `environment.ts` - Desenvolvimento
- `environment.prod.ts` - Produção

### `/src/assets`
Recursos estáticos:
- Imagens
- Ícones  
- Fontes customizadas

## Fluxo de Dados

```
main.ts                  # Entry point
    ↓
bootstrapApplication()   # Inicializa app
    ↓
app.component.ts         # Componente raiz
    ↓
app.component.html       # Template raiz
    ↓
Componentes filhos:
├── navbar            (escuta: scroll$)
├── hero
├── divider-marquee
├── sobre            (reveal animations)
├── modalidades      (reveal animations)
├── galeria          (reveal animations)
├── professor        (reveal animations)
├── contato
├── footer
├── loader
└── back-to-top      (escuta: scroll$)
```

## Lifecycle dos Serviços

### ScrollService
```
Window scroll event
    ↓
ScrollService emite scroll$
    ↓
Components escutam scroll$
    ↓
Update UI (navbar style, back-to-top visibility)
```

### RevealService
```
NgOnInit do app
    ↓
RevealService inicia IntesectionObserver
    ↓
Quando elemento entra na viewport
    ↓
Add class "visible"
    ↓
CSS animation ativa
```

## Tamanhos Tipicamente Esperados

```
node_modules/           ~500 MB (primeira vez)
dist/                   ~1-2 MB (production build)
src/app/               ~50-100 KB
styles.css             ~20 KB
```

## Importações Principais

Cada componente importa:

```typescript
// Padrão
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Específico para cada componente
import { FormsModule } from '@angular/forms';      // contato
import { ScrollService } from '../../services/'; // navbar, back-to-top
```

## Ordem de Carregamento

1. `main.ts` → Inicializa a app
2. `app.component.ts` → Carrega componentes
3. Componentes → Inicializam estilos
4. Services → Inicializam listeners
5. HTML renderiza e CSS aplica

## Build Output

```
dist/bjj-landing/
├── index.html
├── main.xxx.js          # JavaScript principal
├── polyfills.xxx.js     # Polyfills
├── styles.xxx.css       # CSS compilado
└── assets/              # Arquivos copiados
```

## Convention Names

O projeto segue essas convenções:

- **Componentes**: `app-component-name`
- **Serviços**: `component.service.ts`
- **Arquivos HTML**: `component.component.html`
- **Arquivos CSS**: `component.component.css`
- **Pastas**: `kebab-case` (lowercase com hífens)
- **Classes**: `CamelCase`
- **Variáveis**: `camelCase`
- **Constantes**: `SCREAMING_SNAKE_CASE`

## Como Adicionar um Novo Componente

1. Crie a pasta:
```bash
src/app/components/novo-componente/
```

2. Crie os arquivos:
```bash
novo-componente.component.ts
novo-componente.component.html
novo-componente.component.css
```

3. Use o template:
```typescript
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-novo-componente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './novo-componente.component.html',
  styleUrls: ['./novo-componente.component.css']
})
export class NovoComponenteComponent {}
```

4. Importe em `app.component.ts`:
```typescript
import { NovoComponenteComponent } from './components/novo-componente/novo-componente.component';
```

5. Use no template:
```html
<app-novo-componente></app-novo-componente>
```

---

**Estrutura pronta para desenvolvimento! 🚀**
