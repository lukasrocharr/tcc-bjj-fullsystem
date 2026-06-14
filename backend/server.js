const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Caminho dos arquivos de dados
const dataDir = path.join(__dirname, 'data');
const productsFile = path.join(dataDir, 'produtos.json');
const ordersFile = path.join(dataDir, 'pedidos.json');
const aulasFile = path.join(dataDir, 'aulas.json');
const transacoesFile = path.join(dataDir, 'transacoes.json');
const adminFile = path.join(dataDir, 'admin.json');

// Garantir que a pasta data existe
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

// ====== INICIALIZAÇÃO DE DADOS ======

// Produtos padrão
const defaultProducts = [
  {
    id: 1,
    nome: "Kimono Iniciante",
    descricao: "Kimono de qualidade para iniciantes - 100% algodão",
    preco: 250.00,
    categoria: "kimono",
    estoque: 15,
    imagem: "/assets/images/kimono-1.jpg",
    tamanhos: ["P", "M", "G", "GG"]
  },
  {
    id: 2,
    nome: "Kimono Profissional",
    descricao: "Kimono premium para competições - Pré-encolhido",
    preco: 450.00,
    categoria: "kimono",
    estoque: 8,
    imagem: "/assets/images/kimono-2.jpg",
    tamanhos: ["P", "M", "G", "GG"]
  },
  {
    id: 3,
    nome: "Faixa Colorida Oficial",
    descricao: "Faixa oficial para graduações",
    preco: 80.00,
    categoria: "acessorio",
    estoque: 30,
    imagem: "/assets/images/faixa.jpg",
    cores: ["Branca", "Azul", "Roxa", "Marrom", "Preta"]
  },
  {
    id: 4,
    nome: "Protetor Bucal",
    descricao: "Protetor bucal profissional ajustável",
    preco: 45.00,
    categoria: "acessorio",
    estoque: 25,
    imagem: "/assets/images/protetor.jpg"
  },
  {
    id: 5,
    nome: "Caneleira - Par",
    descricao: "Caneleira com proteção para shinai",
    preco: 120.00,
    categoria: "acessorio",
    estoque: 12,
    imagem: "/assets/images/caneleira.jpg",
    tamanhos: ["P", "M", "G"]
  },
  {
    id: 6,
    nome: "Luva de Treino",
    descricao: "Luva de treino resistente - Par",
    preco: 180.00,
    categoria: "acessorio",
    estoque: 20,
    imagem: "/assets/images/luva.jpg"
  }
];

// Aulas padrão
const defaultAulas = [
  {
    id: 1,
    titulo: "Iniciante - Turma A",
    modalidade: "Kids",
    professor: "João Silva",
    dataHora: "2026-04-18T09:00:00",
    duracao: 60,
    alunos: 12,
    status: "ativa"
  },
  {
    id: 2,
    titulo: "Intermediário - Turma B",
    modalidade: "Adultos",
    professor: "Maria Santos",
    dataHora: "2026-04-18T14:00:00",
    duracao: 90,
    alunos: 18,
    status: "ativa"
  },
  {
    id: 3,
    titulo: "Competição",
    modalidade: "Avançado",
    professor: "Carlos Oliveira",
    dataHora: "2026-04-19T18:00:00",
    duracao: 120,
    alunos: 15,
    status: "ativa"
  }
];

// Transações padrão
const defaultTransacoes = [
  {
    id: 1,
    tipo: "receita",
    categoria: "Mensalidades",
    descricao: "Mensalidades - Abril",
    valor: 3000.00,
    data: "2026-04-01",
    status: "confirmado"
  },
  {
    id: 2,
    tipo: "despesa",
    categoria: "Aluguel",
    descricao: "Aluguel do espaço",
    valor: 2000.00,
    data: "2026-04-05",
    status: "confirmado"
  },
  {
    id: 3,
    tipo: "receita",
    categoria: "Aulas Particulares",
    descricao: "Aulas particulares - Semana 1",
    valor: 500.00,
    data: "2026-04-10",
    status: "confirmado"
  },
  {
    id: 4,
    tipo: "despesa",
    categoria: "Materiais",
    descricao: "Compra de tatames",
    valor: 1500.00,
    data: "2026-04-12",
    status: "confirmado"
  }
];

// Admin padrão
const defaultAdmin = {
  usuarios: [
    {
      id: 1,
      username: "admin",
      password: "admin123",
      email: "admin@bjj.com",
      nome: "Administrador",
      token: uuidv4()
    }
  ],
  sessoes: []
};

// Inicializar arquivos se não existirem
function initializeData() {
  if (!fs.existsSync(productsFile)) {
    fs.writeFileSync(productsFile, JSON.stringify(defaultProducts, null, 2));
  }
  if (!fs.existsSync(ordersFile)) {
    fs.writeFileSync(ordersFile, JSON.stringify([], null, 2));
  }
  if (!fs.existsSync(aulasFile)) {
    fs.writeFileSync(aulasFile, JSON.stringify(defaultAulas, null, 2));
  }
  if (!fs.existsSync(transacoesFile)) {
    fs.writeFileSync(transacoesFile, JSON.stringify(defaultTransacoes, null, 2));
  }
  if (!fs.existsSync(adminFile)) {
    fs.writeFileSync(adminFile, JSON.stringify(defaultAdmin, null, 2));
  }
}

// ====== FUNÇÕES AUXILIARES ======

function readProducts() {
  try {
    return JSON.parse(fs.readFileSync(productsFile, 'utf8'));
  } catch (e) {
    return defaultProducts;
  }
}

function writeProducts(products) {
  fs.writeFileSync(productsFile, JSON.stringify(products, null, 2));
}

function readOrders() {
  try {
    return JSON.parse(fs.readFileSync(ordersFile, 'utf8'));
  } catch (e) {
    return [];
  }
}

function writeOrders(orders) {
  fs.writeFileSync(ordersFile, JSON.stringify(orders, null, 2));
}

function readAulas() {
  try {
    return JSON.parse(fs.readFileSync(aulasFile, 'utf8'));
  } catch (e) {
    return defaultAulas;
  }
}

function writeAulas(aulas) {
  fs.writeFileSync(aulasFile, JSON.stringify(aulas, null, 2));
}

function readTransacoes() {
  try {
    return JSON.parse(fs.readFileSync(transacoesFile, 'utf8'));
  } catch (e) {
    return defaultTransacoes;
  }
}

function writeTransacoes(transacoes) {
  fs.writeFileSync(transacoesFile, JSON.stringify(transacoes, null, 2));
}

function readAdmin() {
  try {
    return JSON.parse(fs.readFileSync(adminFile, 'utf8'));
  } catch (e) {
    return defaultAdmin;
  }
}

function writeAdmin(admin) {
  fs.writeFileSync(adminFile, JSON.stringify(admin, null, 2));
}

// ====== MIDDLEWARE DE AUTENTICAÇÃO ======

function authMiddleware(req, res, next) {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ erro: "Token não fornecido" });
  }
  
  const admin = readAdmin();
  const usuario = admin.usuarios.find(u => u.token === token);
  
  if (!usuario) {
    return res.status(401).json({ erro: "Token inválido" });
  }
  
  req.usuario = usuario;
  next();
}

// ====== MIDDLEWARE DE AUTENTICAÇÃO ======

function authMiddleware(req, res, next) {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ erro: "Token não fornecido" });
  }
  
  const admin = readAdmin();
  const usuario = admin.usuarios.find(u => u.token === token);
  
  if (!usuario) {
    return res.status(401).json({ erro: "Token inválido" });
  }
  
  req.usuario = usuario;
  next();
}

// ====== ROTAS: AUTENTICAÇÃO ======

// Login
app.post('/api/admin/login', (req, res) => {
  const { username, password } = req.body;
  const admin = readAdmin();
  
  const usuario = admin.usuarios.find(u => u.username === username);
  
  if (!usuario || usuario.password !== password) {
    return res.status(401).json({ erro: "Credenciais inválidas" });
  }
  
  res.json({
    token: usuario.token,
    usuario: {
      id: usuario.id,
      username: usuario.username,
      email: usuario.email,
      nome: usuario.nome
    }
  });
});

// Verificar token
app.get('/api/admin/me', authMiddleware, (req, res) => {
  res.json({
    usuario: {
      id: req.usuario.id,
      username: req.usuario.username,
      email: req.usuario.email,
      nome: req.usuario.nome
    }
  });
});

// ====== ROTAS: AULAS ======

// Listar todas as aulas
app.get('/api/aulas', (req, res) => {
  const aulas = readAulas();
  res.json(aulas);
});

// Buscar aula por ID
app.get('/api/aulas/:id', (req, res) => {
  const aulas = readAulas();
  const aula = aulas.find(a => a.id === parseInt(req.params.id));
  
  if (aula) {
    res.json(aula);
  } else {
    res.status(404).json({ erro: "Aula não encontrada" });
  }
});

// Criar nova aula (requer autenticação)
app.post('/api/aulas', authMiddleware, (req, res) => {
  const aulas = readAulas();
  const newAula = {
    id: Math.max(...aulas.map(a => a.id), 0) + 1,
    ...req.body,
    alunos: req.body.alunos || 0,
    status: req.body.status || 'ativa'
  };
  
  aulas.push(newAula);
  writeAulas(aulas);
  
  res.status(201).json(newAula);
});

// Atualizar aula (requer autenticação)
app.put('/api/aulas/:id', authMiddleware, (req, res) => {
  const aulas = readAulas();
  const index = aulas.findIndex(a => a.id === parseInt(req.params.id));
  
  if (index !== -1) {
    aulas[index] = { ...aulas[index], ...req.body };
    writeAulas(aulas);
    res.json(aulas[index]);
  } else {
    res.status(404).json({ erro: "Aula não encontrada" });
  }
});

// Deletar aula (requer autenticação)
app.delete('/api/aulas/:id', authMiddleware, (req, res) => {
  let aulas = readAulas();
  const index = aulas.findIndex(a => a.id === parseInt(req.params.id));
  
  if (index !== -1) {
    const deleted = aulas.splice(index, 1);
    writeAulas(aulas);
    res.json(deleted[0]);
  } else {
    res.status(404).json({ erro: "Aula não encontrada" });
  }
});

// ====== ROTAS: TRANSAÇÕES FINANCEIRAS ======

// Listar todas as transações
app.get('/api/transacoes', (req, res) => {
  const transacoes = readTransacoes();
  res.json(transacoes);
});

// Buscar transação por ID
app.get('/api/transacoes/:id', (req, res) => {
  const transacoes = readTransacoes();
  const transacao = transacoes.find(t => t.id === parseInt(req.params.id));
  
  if (transacao) {
    res.json(transacao);
  } else {
    res.status(404).json({ erro: "Transação não encontrada" });
  }
});

// Criar nova transação (requer autenticação)
app.post('/api/transacoes', authMiddleware, (req, res) => {
  const transacoes = readTransacoes();
  const newTransacao = {
    id: Math.max(...transacoes.map(t => t.id), 0) + 1,
    ...req.body,
    status: req.body.status || 'pendente',
    data: req.body.data || new Date().toISOString().split('T')[0]
  };
  
  transacoes.push(newTransacao);
  writeTransacoes(transacoes);
  
  res.status(201).json(newTransacao);
});

// Atualizar transação (requer autenticação)
app.put('/api/transacoes/:id', authMiddleware, (req, res) => {
  const transacoes = readTransacoes();
  const index = transacoes.findIndex(t => t.id === parseInt(req.params.id));
  
  if (index !== -1) {
    transacoes[index] = { ...transacoes[index], ...req.body };
    writeTransacoes(transacoes);
    res.json(transacoes[index]);
  } else {
    res.status(404).json({ erro: "Transação não encontrada" });
  }
});

// Deletar transação (requer autenticação)
app.delete('/api/transacoes/:id', authMiddleware, (req, res) => {
  let transacoes = readTransacoes();
  const index = transacoes.findIndex(t => t.id === parseInt(req.params.id));
  
  if (index !== -1) {
    const deleted = transacoes.splice(index, 1);
    writeTransacoes(transacoes);
    res.json(deleted[0]);
  } else {
    res.status(404).json({ erro: "Transação não encontrada" });
  }
});

// ====== ROTAS: RELATÓRIOS FINANCEIROS ======

// Relatório geral
app.get('/api/relatorios/geral', authMiddleware, (req, res) => {
  const transacoes = readTransacoes();
  
  const receitas = transacoes
    .filter(t => t.tipo === 'receita' && t.status === 'confirmado')
    .reduce((sum, t) => sum + (t.valor || 0), 0);
    
  const despesas = transacoes
    .filter(t => t.tipo === 'despesa' && t.status === 'confirmado')
    .reduce((sum, t) => sum + (t.valor || 0), 0);
  
  const saldo = receitas - despesas;
  
  res.json({
    receitas,
    despesas,
    saldo,
    transacoes: transacoes.length
  });
});

// Relatório mensal
app.get('/api/relatorios/mensal/:mes', authMiddleware, (req, res) => {
  const mes = req.params.mes;
  const transacoes = readTransacoes();
  
  const menseisFiltered = transacoes.filter(t => t.data.startsWith(mes));
  
  const receitas = menseisFiltered
    .filter(t => t.tipo === 'receita' && t.status === 'confirmado')
    .reduce((sum, t) => sum + (t.valor || 0), 0);
    
  const despesas = menseisFiltered
    .filter(t => t.tipo === 'despesa' && t.status === 'confirmado')
    .reduce((sum, t) => sum + (t.valor || 0), 0);
  
  const saldo = receitas - despesas;
  
  res.json({
    mes,
    receitas,
    despesas,
    saldo,
    detalhes: menseisFiltered
  });
})

// Listar todos os produtos
app.get('/api/produtos', (req, res) => {
  const categoria = req.query.categoria;
  let produtos = readProducts();
  
  if (categoria) {
    produtos = produtos.filter(p => p.categoria === categoria);
  }
  
  res.json(produtos);
});

// Buscar produto por ID
app.get('/api/produtos/:id', (req, res) => {
  const produtos = readProducts();
  const produto = produtos.find(p => p.id === parseInt(req.params.id));
  
  if (produto) {
    res.json(produto);
  } else {
    res.status(404).json({ erro: "Produto não encontrado" });
  }
});

// Criar novo produto
app.post('/api/produtos', (req, res) => {
  const produtos = readProducts();
  const newProduct = {
    id: Math.max(...produtos.map(p => p.id), 0) + 1,
    ...req.body,
    estoque: req.body.estoque || 0
  };
  
  produtos.push(newProduct);
  writeProducts(produtos);
  
  res.status(201).json(newProduct);
});

// Atualizar produto
app.put('/api/produtos/:id', (req, res) => {
  const produtos = readProducts();
  const index = produtos.findIndex(p => p.id === parseInt(req.params.id));
  
  if (index !== -1) {
    produtos[index] = { ...produtos[index], ...req.body };
    writeProducts(produtos);
    res.json(produtos[index]);
  } else {
    res.status(404).json({ erro: "Produto não encontrado" });
  }
});

// Deletar produto
app.delete('/api/produtos/:id', (req, res) => {
  let produtos = readProducts();
  const index = produtos.findIndex(p => p.id === parseInt(req.params.id));
  
  if (index !== -1) {
    const deleted = produtos.splice(index, 1);
    writeProducts(produtos);
    res.json(deleted[0]);
  } else {
    res.status(404).json({ erro: "Produto não encontrado" });
  }
});

// ====== ROTAS: PEDIDOS ======

// Listar todos os pedidos
app.get('/api/pedidos', (req, res) => {
  const pedidos = readOrders();
  res.json(pedidos);
});

// Criar novo pedido
app.post('/api/pedidos', (req, res) => {
  const pedidos = readOrders();
  const produtos = readProducts();
  
  // Validar produtos e atualizar estoque
  for (let item of req.body.itens) {
    const produto = produtos.find(p => p.id === item.id);
    if (!produto) {
      return res.status(400).json({ erro: `Produto ${item.id} não encontrado` });
    }
    if (produto.estoque < item.quantidade) {
      return res.status(400).json({ erro: `Estoque insuficiente para ${produto.nome}` });
    }
    produto.estoque -= item.quantidade;
  }
  
  const newOrder = {
    id: uuidv4(),
    ...req.body,
    data: new Date().toISOString(),
    status: 'pendente',
    total: req.body.total || 0
  };
  
  pedidos.push(newOrder);
  writeOrders(pedidos);
  writeProducts(produtos);
  
  res.status(201).json(newOrder);
});

// Atualizar status do pedido
app.put('/api/pedidos/:id', (req, res) => {
  const pedidos = readOrders();
  const index = pedidos.findIndex(p => p.id === req.params.id);
  
  if (index !== -1) {
    pedidos[index] = { ...pedidos[index], ...req.body };
    writeOrders(pedidos);
    res.json(pedidos[index]);
  } else {
    res.status(404).json({ erro: "Pedido não encontrado" });
  }
});

// ====== ROTAS: VERIFICAÇÃO DE SAÚDE ======

app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', timestamp: new Date().toISOString() });
});

// ====== INICIAR SERVIDOR ======

initializeData();

const admin = readAdmin();
const adminToken = admin.usuarios[0]?.token || 'token-desconhecido';

app.listen(PORT, () => {
  console.log(`
╔═══════════════════════════════════════════════════╗
║   🏋️  BJJ Academia Backend                        ║
║   ✅ Servidor rodando em porta ${PORT}              ║
║   📍 http://localhost:${PORT}                        ║
║   📚 API disponível em /api/*                      ║
║                                                   ║
║   👤 ADMIN                                        ║
║   • Username: admin                               ║
║   • Password: admin123                            ║
║   • Token: ${adminToken.substring(0, 20)}...       ║
║                                                   ║
║   🔐 Endpoints Protegidos:                        ║
║   • POST /api/admin/login                         ║
║   • GET /api/admin/me                             ║
║   • POST/PUT/DELETE /api/aulas                    ║
║   • POST/PUT/DELETE /api/transacoes               ║
║   • GET /api/relatorios/*                         ║
╚═══════════════════════════════════════════════════╝
  `);
});

module.exports = app;
