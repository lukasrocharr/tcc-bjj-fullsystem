import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LojaApiService, Produto, Categoria, Variacao } from '../../services/loja-api.service';

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loja.component.html',
  styleUrls: ['./loja.component.css']
})
export class LojaComponent implements OnInit {
  produtos: Produto[] = [];
  categorias: Categoria[] = [];
  categoriaId?: number;
  selecionada: Record<number, number> = {};

  carregando = true;
  adicionando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private loja: LojaApiService) {}

  ngOnInit(): void {
    this.loja.categorias().subscribe({
      next: c => this.categorias = c.filter(x => x.ativo),
      error: () => {}
    });
    this.carregarProdutos();
    // Inicializa o estado do carrinho (atualiza navbar/modal)
    this.loja.verCarrinho().subscribe({ error: () => {} });
  }

  carregarProdutos(): void {
    this.carregando = true;
    this.erro = null;
    this.loja.listarProdutos(this.categoriaId, undefined, 0, 50).subscribe({
      next: r => { this.produtos = r.content; this.carregando = false; },
      error: () => { this.erro = 'Erro ao carregar produtos. Verifique se o servidor está no ar.'; this.carregando = false; }
    });
  }

  filtrar(categoriaId?: number): void {
    this.categoriaId = categoriaId;
    this.carregarProdutos();
  }

  estoqueTotal(p: Produto): number {
    return (p.variacoes || []).reduce((s, v) => s + v.estoque, 0);
  }

  menorPreco(p: Produto): number {
    if (!p.variacoes?.length) return p.preco;
    return Math.min(...p.variacoes.map(v => v.precoEfetivo));
  }

  rotuloVariacao(v: Variacao): string {
    return [v.tamanho, v.cor].filter(x => !!x).join(' / ') || v.sku;
  }

  getEstoqueText(estoque: number): string {
    return estoque > 0 ? `${estoque} em estoque` : 'Fora de estoque';
  }

  selecionarVariacao(produtoId: number, variacaoId: number): void {
    this.selecionada[produtoId] = this.selecionada[produtoId] === variacaoId ? 0 : variacaoId;
  }

  adicionarAoCarrinho(produto: Produto): void {
    const variacaoId = this.selecionada[produto.id];
    if (!variacaoId) return;
    this.adicionando = true;
    this.erro = null;
    this.loja.adicionarItem(variacaoId, 1).subscribe({
      next: () => {
        this.adicionando = false;
        this.aviso = `${produto.nome} adicionado ao carrinho!`;
        this.selecionada[produto.id] = 0;
        setTimeout(() => this.aviso = null, 2500);
      },
      error: e => { this.adicionando = false; this.erro = e?.error?.message || 'Não foi possível adicionar ao carrinho.'; }
    });
  }

  productImage(produto: Produto): string {
    return `assets/loja/${produto.id}.webp`;
  }
}
