import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Produto {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  categoria: 'kimono' | 'acessorio';
  estoque: number;
  imagem: string;
  tamanhos?: string[];
  cores?: string[];
}

export interface ItemPedido {
  id: number;
  quantidade: number;
  tamanho?: string;
  cor?: string;
}

export interface Pedido {
  id: string;
  itens: ItemPedido[];
  total: number;
  cliente: {
    nome: string;
    email: string;
    telefone: string;
  };
  endereco: {
    rua: string;
    numero: string;
    bairro: string;
    cidade: string;
    estado: string;
    cep: string;
  };
  status: 'pendente' | 'confirmado' | 'enviado' | 'entregue' | 'cancelado';
  data: string;
}

@Injectable({
  providedIn: 'root'
})
export class LojaService {
  private apiUrl = 'http://localhost:3000/api';
  private carrinhoSubject = new BehaviorSubject<ItemPedido[]>([]);
  public carrinho$ = this.carrinhoSubject.asObservable();

  constructor(private http: HttpClient) {
    this.carregarCarrinhoDoLocalStorage();
  }

  // ====== PRODUTOS ======
  obterProdutos(categoria?: string): Observable<Produto[]> {
    let url = `${this.apiUrl}/produtos`;
    if (categoria) {
      url += `?categoria=${categoria}`;
    }
    return this.http.get<Produto[]>(url);
  }

  obterProduto(id: number): Observable<Produto> {
    return this.http.get<Produto>(`${this.apiUrl}/produtos/${id}`);
  }

  // ====== CARRINHO ======
  adicionarAoCarrinho(item: ItemPedido): void {
    const carrinho = this.carrinhoSubject.value;
    const itemExistente = carrinho.find(i => i.id === item.id);

    if (itemExistente) {
      itemExistente.quantidade += item.quantidade;
    } else {
      carrinho.push(item);
    }

    this.carrinhoSubject.next(carrinho);
    this.salvarCarrinhoNoLocalStorage();
  }

  removerDoCarrinho(id: number): void {
    const carrinho = this.carrinhoSubject.value.filter(i => i.id !== id);
    this.carrinhoSubject.next(carrinho);
    this.salvarCarrinhoNoLocalStorage();
  }

  limparCarrinho(): void {
    this.carrinhoSubject.next([]);
    this.salvarCarrinhoNoLocalStorage();
  }

  obterCarrinho(): ItemPedido[] {
    return this.carrinhoSubject.value;
  }

  private salvarCarrinhoNoLocalStorage(): void {
    localStorage.setItem('carrinho-bjj', JSON.stringify(this.carrinhoSubject.value));
  }

  private carregarCarrinhoDoLocalStorage(): void {
    const carrinho = localStorage.getItem('carrinho-bjj');
    if (carrinho) {
      try {
        this.carrinhoSubject.next(JSON.parse(carrinho));
      } catch (e) {
        console.error('Erro ao carregar carrinho:', e);
      }
    }
  }

  // ====== PEDIDOS ======
  criarPedido(pedido: Omit<Pedido, 'id' | 'data' | 'status'>): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.apiUrl}/pedidos`, pedido);
  }

  obterPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/pedidos`);
  }

  atualizarPedido(id: string, status: string): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/pedidos/${id}`, { status });
  }

  calcularTotal(carrinho: ItemPedido[], produtos: Produto[]): number {
    return carrinho.reduce((total, item) => {
      const produto = produtos.find(p => p.id === item.id);
      return total + (produto ? produto.preco * item.quantidade : 0);
    }, 0);
  }
}
