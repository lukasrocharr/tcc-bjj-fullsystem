# BJJ Landing Page - Angular

Landing page e Sistema completo de gestão para academia de BJJ

## 📋 Funcionalidades

✅ **Componentes Standalone** - Arquitetura modular e limpa  
✅ **Navbar Responsivo** - Menu fixo com efeito de scroll  
✅ **Mobile Menu** - Menu hambúrguer para dispositivos móveis  
✅ **Scroll Animations** - Animações reveal ao fazer scroll  
✅ **Back-to-Top Button** - Botão flutuante para voltar ao topo  
✅ **Loader Animado** - Animação de carregamento no início  
✅ **Formulário de Contato** - Formulário de inscrição com validação  
✅ **Galeria** - Grid responsivo de imagens  
✅ **Marquee Animado** - Faixa com texto deslizante  
✅ **Totalmente Responsivo** - Mobile, tablet e desktop  

## 🛠️ Estrutura do Projeto

```
src/
├── app/
│   ├── components/
│   │   ├── navbar/
│   │   ├── hero/
│   │   ├── sobre/
│   │   ├── modalidades/
│   │   ├── galeria/
│   │   ├── professor/
│   │   ├── contato/
│   │   ├── footer/
│   │   ├── loader/
│   │   ├── back-to-top/
│   │   └── divider-marquee/
│   ├── services/
│   │   ├── scroll.service.ts
│   │   └── reveal.service.ts
│   ├── app.component.*
├── styles.css (Global)
├── index.html
└── main.ts
```

## 🚀 Como Usar

### Instalação

1. **Instale as dependências:**
```bash
npm install
```

### Desenvolvimento

2. **Inicie o servidor de desenvolvimento:**
```bash
ng serve
```

3. **Acesse no navegador:**
```
http://localhost:4200
```

## ⚡ Performance

- Componentes standalone para melhor otimização
- OnPush change detection onde possível
- RxJS para gerenciar eventos de scroll
- AnimationsModule para transições suaves

## 📦 Dependências Principais

- `@angular/core`: ^18.0.0
- `@angular/common`: ^18.0.0
- `@angular/forms`: ^18.0.0
- `rxjs`: ^7.8.0

## 🔧 Principais Serviços

### `ScrollService`
Gerencia a posição do scroll e emite eventos para atualizar componentes.

### `RevealService`
Implement Intersection Observer para animar elementos quando entram na viewport.

---

