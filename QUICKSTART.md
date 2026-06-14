# ⚡ Quick Start

## 30 Segundos para Começar

### 1. Instale dependências
```bash
npm install
```

### 2. Rode o projeto
```bash
npm start
```

### 3. Abra no navegador
```
http://localhost:4200
```

**Pronto! ✅**

---

## Próximos Passos

### Personalizar Logo/Cores
Edit `src/styles.css`:
```css
--gold: #c9a84c;  /* Mude a cor principal */
```

### Mudar Telefone WhatsApp
Edit `src/app/components/contato/contato.component.html`:
```html
<a href="https://wa.me/SEU_NUMERO">...</a>
```

### Adicionar Imagens
1. Coloque em `src/assets/images/`
2. Use no template:
```html
<img src="assets/images/seu-professor.jpg" alt="Professor" />
```

### Build para Produção
```bash
npm run build
```

---

## Principais Arquivos

| Arquivo | Descrição |
|---------|-----------|
| `src/styles.css` | Estilos globais + variáveis |
| `src/app/app.component.html` | Layout principal |
| `src/app/components/*/` | Componentes individuais |
| `src/app/services/` | Scrolling e animações |

---

## Estrutura de Um Componente

```typescript
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-meu-componente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meu-componente.component.html',
  styleUrls: ['./meu-componente.component.css']
})
export class MeuComponenteComponent {}
```

---

## Comandos Úteis

```bash
npm start                 # Rodear dev
npm run build             # Build production
npm install               # Instalar deps
ng generate component     # Criar novo componente
ng serve --port 4201      # Port diferente
```

---

## Documentação

| Arquivo | Conteúdo |
|---------|----------|
| `README.md` | Info geral do projeto |
| `SETUP.md` | Setup detalhado |
| `EXEMPLOS.md` | Exemplo de customizações |
| `STRUCTURE.md` | Estrutura de pastas |
| `TROUBLESHOOTING.md` | Problemas comuns |

---

## Checklist de Setup

- [ ] `npm install` rodar sem erros?
- [ ] `npm start` abrir no navegador?
- [ ] Ver página com conteúdo?
- [ ] Mobile menu funciona?
- [ ] Scroll desce a página?

✅ Se tudo passar, você está pronto!

---

## Próximo: Personalize!

1. Mude o logo em `src/app/components/navbar/`
2. Atualize cores em `src/styles.css`
3. Adicione suas imagens em `src/assets/`
4. Configure seu WhatsApp/telefone
5. Build: `npm run build`

---

**Bom desenvolvimento! 🚀🥋**

[Documentação Completa →](README.md)
