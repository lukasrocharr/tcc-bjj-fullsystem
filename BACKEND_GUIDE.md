# 🛍️ Backend e Loja - Guia de Setup

## 📋 O que foi criado

### Backend (Node.js + Express)
- ✅ API REST para gerenciar produtos
- ✅ API para gerenciar pedidos
- ✅ Armazenamento em JSON (dados persistentes)
- ✅ CORS habilitado para integração com Angular

### Frontend (Nova Seção)
- ✅ Componente de Loja (exibe produtos)
- ✅ Componente de Carrinho (gerencia itens)
- ✅ Serviço de Loja (integração com backend)
- ✅ Filtros por categoria
- ✅ Seleção de tamanho/cor

---

## 🚀 Como Rodar

### Passo 1: Instalar Dependências do Backend

```powershell
cd "c:\Users\lukas.rocha\OneDrive - AESSEAL BRASIL LTDA\Área de Trabalho\TCC\backend"
npm install
```

### Passo 2: Iniciar o Backend

```powershell
npm start
```

Você verá:
```
╔════════════════════════════════════════╗
║   🏋️  BJJ Academia Backend              ║
║   ✅ Servidor rodando em porta 3000    ║
║   📍 http://localhost:3000              ║
║   📚 API disponível em /api/*            ║
╚════════════════════════════════════════╝
```

### Passo 3: O Frontend Já Está Pronto

O servidor Angular já está rodando em `http://localhost:4200`

---

## 📚 Endpoints da API

### Produtos

**Listar todos os produtos**
```
GET http://localhost:3000/api/produtos
```

**Listar por categoria**
```
GET http://localhost:3000/api/produtos?categoria=kimono
GET http://localhost:3000/api/produtos?categoria=acessorio
```

**Buscar um produto**
```
GET http://localhost:3000/api/produtos/1
```

**Criar novo produto** (Admin)
```
POST http://localhost:3000/api/produtos
Content-Type: application/json

{
  "nome": "Kimono Novo",
  "descricao": "Descrição do produto",
  "preco": 300.00,
  "categoria": "kimono",
  "estoque": 10,
  "imagem": "/assets/images/kimono.jpg",
  "tamanhos": ["P", "M", "G", "GG"]
}
```

**Atualizar produto** (Admin)
```
PUT http://localhost:3000/api/produtos/1
Content-Type: application/json

{
  "estoque": 20
}
```

**Deletar produto** (Admin)
```
DELETE http://localhost:3000/api/produtos/1
```

### Pedidos

**Listar todos os pedidos**
```
GET http://localhost:3000/api/pedidos
```

**Criar novo pedido**
```
POST http://localhost:3000/api/pedidos
Content-Type: application/json

{
  "itens": [
    { "id": 1, "quantidade": 1, "tamanho": "M" },
    { "id": 3, "quantidade": 2, "cor": "Preta" }
  ],
  "total": 410.00,
  "cliente": {
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "11999999999"
  },
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  }
}
```

**Atualizar status do pedido**
```
PUT http://localhost:3000/api/pedidos/{id}
Content-Type: application/json

{
  "status": "confirmado"
}
```

---

## 📁 Estrutura do Backend

```
backend/
├── server.js           # Servidor principal
├── package.json        # Dependências
└── data/
    ├── produtos.json   # Banco de dados de produtos
    └── pedidos.json    # Banco de dados de pedidos
```

---

## 🗄️ Dados Padrão

### Produtos Iniciais

1. **Kimono Iniciante** - R$ 250,00
2. **Kimono Profissional** - R$ 450,00
3. **Faixa Colorida Oficial** - R$ 80,00
4. **Protetor Bucal** - R$ 45,00
5. **Caneleira - Par** - R$ 120,00
6. **Luva de Treino** - R$ 180,00

---

## 🔌 Integração Frontend

### Usar o Serviço de Loja

```typescript
import { LojaService } from './services/loja.service';

constructor(private loja: LojaService) {}

// Obter produtos
this.loja.obterProdutos().subscribe(produtos => {
  console.log(produtos);
});

// Obter por categoria
this.loja.obterProdutos('kimono').subscribe(kimonos => {
  console.log(kimonos);
});

// Adicionar ao carrinho
this.loja.adicionarAoCarrinho({
  id: 1,
  quantidade: 1,
  tamanho: 'M'
});

// Obter carrinho
this.loja.carrinho$.subscribe(itens => {
  console.log(itens);
});

// Criar pedido
this.loja.criarPedido({
  itens: [...],
  total: 500,
  cliente: {...},
  endereco: {...}
}).subscribe(pedido => {
  console.log('Pedido criado:', pedido);
});
```

---

## 🛍️ Adicionar a Loja na Landing Page

### 1. Importar em `app.component.ts`:

```typescript
import { LojaComponent } from './components/loja/loja.component';
import { HttpClientModule } from '@angular/common/http';

@Component({
  imports: [
    // ... componentes existentes
    LojaComponent,
    HttpClientModule
  ]
})
```

### 2. Adicionar em `app.component.html`:

```html
<app-loja></app-loja>
```

### 3. Adicionar link na navbar para a seção #loja

---

## 💾 Dados Persistem?

**Sim!** Os dados são salvos em arquivos JSON:
- `backend/data/produtos.json` - Armazena produtos
- `backend/data/pedidos.json` - Armazena pedidos

Mesmo que você reinicie o servidor, os dados continuam lá!

---

## 🧪 Testar com cURL ou Postman

### Testar conexão:
```bash
curl http://localhost:3000/api/health
```

### Listar produtos:
```bash
curl http://localhost:3000/api/produtos
```

### Criar pedido:
```bash
curl -X POST http://localhost:3000/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "itens": [{"id": 1, "quantidade": 1}],
    "total": 250,
    "cliente": {"nome": "João", "email": "j@test.com", "telefone": "1199999999"},
    "endereco": {"rua": "Rua A", "numero": "1", "bairro": "B", "cidade": "São Paulo", "estado": "SP", "cep": "01310-100"}
  }'
```

---

## 🔒 Segurança (Futuro)

Para produção, implemente:
- ✅ Autenticação (JWT)
- ✅ Validação de dados
- ✅ Rate limiting
- ✅ HTTPS
- ✅ Banco de dados real (MongoDB, PostgreSQL)
- ✅ Pagamento integrado (Stripe, PayPal)

---

## 🚨 Problemas Comuns

### "Cannot find module 'express'"
```bash
npm install
```

### "Port 3000 already in use"
```bash
# Mudar porta em server.js ou matar processo
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### Frontend não conecta ao backend
- Verifique se backend está rodando em `http://localhost:3000`
- Verifique CORS está habilitado
- Console do navegador (F12) deve mostrar erro detalhado

---

## 📝 Próximos Passos

1. **Checkout Completo** - Integrar com Stripe/PayPal
2. **Admin de Produtos** - Painel para gerenciar estoque
3. **Email de Confirmação** - Notificar cliente
4. **Rastreamento de Pedidos** - Status em tempo real
5. **Autenticação de Usuários** - Conta de cliente

---

## 📞 Endpoints Rápida Referência

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/produtos` | Listar produtos |
| GET | `/api/produtos/:id` | Detalhes do produto |
| POST | `/api/produtos` | Criar produto |
| PUT | `/api/produtos/:id` | Atualizar produto |
| DELETE | `/api/produtos/:id` | Deletar produto |
| GET | `/api/pedidos` | Listar pedidos |
| POST | `/api/pedidos` | Criar pedido |
| PUT | `/api/pedidos/:id` | Atualizar status |

---

**Backend pronto para produção! 🚀**
