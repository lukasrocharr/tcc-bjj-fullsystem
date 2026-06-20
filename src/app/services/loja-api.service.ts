import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { PageResponse } from './academia.service';

// ===== Tipos espelhando os DTOs do backend (Fase 5) =====
export interface Categoria { id: number; nome: string; ativo: boolean; }
export interface CategoriaRef { id: number; nome: string; }

export interface Variacao {
  id: number;
  sku: string;
  tamanho?: string;
  cor?: string;
  precoAdicional: number;
  precoEfetivo: number;
  estoque: number;
}

export interface Produto {  imagem?: string;
  id: number;
  nome: string;
  descricao?: string;
  categoria: CategoriaRef;
  preco: number;
  ativo: boolean;
  imagens: string[];
  variacoes: Variacao[];
}

export interface ItemCarrinho {
  id: number;
  variacaoId: number;
  sku: string;
  produto: string;
  tamanho?: string;
  cor?: string;
  precoUnitario: number;
  quantidade: number;
  subtotal: number;
  estoqueDisponivel: number;
}

export interface Carrinho {
  id: number;
  itens: ItemCarrinho[];
  totalItens: number;
  subtotal: number;
}

export type StatusPedido = 'AGUARDANDO_PAGAMENTO' | 'PAGO' | 'ENVIADO' | 'ENTREGUE' | 'CANCELADO';
export type MetodoPagamento = 'PIX' | 'BOLETO' | 'CARTAO' | 'DINHEIRO';

export interface Endereco {
  cep: string; logradouro: string; numero: string; complemento?: string;
  bairro?: string; cidade: string; uf: string;
}

export interface ItemPedido {
  nomeProduto: string; sku: string; precoUnitario: number; quantidade: number; subtotal: number;
}

export interface Pedido {
  id: number;
  numero: string;
  status: StatusPedido;
  subtotal: number;
  frete: number;
  desconto: number;
  total: number;
  cupomCodigo?: string;
  endereco: Endereco;
  rastreio?: string;
  itens: ItemPedido[];
  criadoEm: string;
}

const SESSION_KEY = 'loja_session_id';

/**
 * Cliente HTTP da loja Spring (catalogo, carrinho, checkout, pedidos - Fase 5).
 * Visitantes usam um X-Session-Id persistido em localStorage; usuarios
 * autenticados usam o proprio carrinho (o JWT vai pelo AuthInterceptor).
 */
@Injectable({ providedIn: 'root' })
export class LojaApiService {
  private api = environment.apiUrl;

  /** Estado reativo do carrinho, compartilhado por navbar/modal/catalogo. */
  private carrinhoSubject = new BehaviorSubject<Carrinho | null>(null);
  carrinho$ = this.carrinhoSubject.asObservable();
  totalItens$ = this.carrinho$.pipe(map(c => c?.totalItens ?? 0));

  constructor(private http: HttpClient) {}

  get carrinhoAtual(): Carrinho | null { return this.carrinhoSubject.value; }

  /** Id de sessao de visitante (gerado uma vez e reutilizado). */
  getSessionId(): string {
    let id = localStorage.getItem(SESSION_KEY);
    if (!id) {
      id = 'guest-' + Math.random().toString(36).slice(2) + Date.now().toString(36);
      localStorage.setItem(SESSION_KEY, id);
    }
    return id;
  }

  private sessionHeaders(): HttpHeaders {
    return new HttpHeaders({ 'X-Session-Id': this.getSessionId() });
  }

  // ===== Catalogo (publico) =====
  categorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.api}/loja/categorias`);
  }

  listarProdutos(categoriaId?: number, busca?: string, page = 0, size = 12): Observable<PageResponse<Produto>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (categoriaId) params = params.set('categoriaId', categoriaId);
    if (busca) params = params.set('busca', busca);
    return this.http.get<PageResponse<Produto>>(`${this.api}/loja/produtos`, { params });
  }

  buscarProduto(id: number): Observable<Produto> {
    return this.http.get<Produto>(`${this.api}/loja/produtos/${id}`);
  }

  // ===== Catalogo (admin) =====
  criarCategoria(body: { nome: string; ativo?: boolean }): Observable<Categoria> {
    return this.http.post<Categoria>(`${this.api}/loja/categorias`, body);
  }
  atualizarCategoria(id: number, body: { nome: string; ativo?: boolean }): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.api}/loja/categorias/${id}`, body);
  }
  removerCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/loja/categorias/${id}`);
  }
  criarProduto(body: any): Observable<Produto> {
    return this.http.post<Produto>(`${this.api}/loja/produtos`, body);
  }
  atualizarProduto(id: number, body: any): Observable<Produto> {
    return this.http.put<Produto>(`${this.api}/loja/produtos/${id}`, body);
  }
  removerProduto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/loja/produtos/${id}`);
  }
  adicionarVariacao(produtoId: number, body: any): Observable<Variacao> {
    return this.http.post<Variacao>(`${this.api}/loja/produtos/${produtoId}/variacoes`, body);
  }
  atualizarVariacao(id: number, body: any): Observable<Variacao> {
    return this.http.put<Variacao>(`${this.api}/loja/variacoes/${id}`, body);
  }
  removerVariacao(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/loja/variacoes/${id}`);
  }
  movimentarEstoque(variacaoId: number, tipo: 'ENTRADA' | 'SAIDA', quantidade: number, motivo?: string): Observable<Variacao> {
    return this.http.post<Variacao>(`${this.api}/loja/variacoes/${variacaoId}/estoque`, { tipo, quantidade, motivo });
  }

  // ===== Carrinho =====
  /** Carrega o carrinho do servidor e atualiza o estado reativo. */
  verCarrinho(): Observable<Carrinho> {
    return this.http.get<Carrinho>(`${this.api}/loja/carrinho`, { headers: this.sessionHeaders() })
      .pipe(tap(c => this.carrinhoSubject.next(c)));
  }
  adicionarItem(variacaoId: number, quantidade: number): Observable<Carrinho> {
    return this.http.post<Carrinho>(`${this.api}/loja/carrinho/itens`, { variacaoId, quantidade }, { headers: this.sessionHeaders() })
      .pipe(tap(c => this.carrinhoSubject.next(c)));
  }
  atualizarItem(itemId: number, quantidade: number): Observable<Carrinho> {
    return this.http.put<Carrinho>(`${this.api}/loja/carrinho/itens/${itemId}`, {}, {
      headers: this.sessionHeaders(),
      params: new HttpParams().set('quantidade', quantidade)
    }).pipe(tap(c => this.carrinhoSubject.next(c)));
  }
  removerItem(itemId: number): Observable<Carrinho> {
    return this.http.delete<Carrinho>(`${this.api}/loja/carrinho/itens/${itemId}`, { headers: this.sessionHeaders() })
      .pipe(tap(c => this.carrinhoSubject.next(c)));
  }

  // ===== Checkout / pedidos =====
  checkout(body: { endereco: Endereco; cupomCodigo?: string; metodo: MetodoPagamento }): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.api}/loja/checkout`, body, { headers: this.sessionHeaders() })
      .pipe(tap(() => this.verCarrinho().subscribe({ error: () => {} })));
  }
  meusPedidos(page = 0, size = 10): Observable<PageResponse<Pedido>> {
    return this.http.get<PageResponse<Pedido>>(`${this.api}/loja/meus-pedidos`, { params: { page, size } as any });
  }

  // ===== Pedidos (admin) =====
  listarPedidos(status?: StatusPedido, page = 0, size = 20): Observable<PageResponse<Pedido>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Pedido>>(`${this.api}/loja/admin/pedidos`, { params });
  }
  buscarPedido(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.api}/loja/admin/pedidos/${id}`);
  }
  atualizarStatusPedido(id: number, status: StatusPedido, rastreio?: string): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.api}/loja/admin/pedidos/${id}/status`, { status, rastreio });
  }
}
