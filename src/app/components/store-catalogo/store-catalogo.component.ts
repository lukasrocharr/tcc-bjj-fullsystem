import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LojaApiService, Produto, Categoria, Variacao } from '../../services/loja-api.service';
import { STORE_STYLES } from '../store-shared.styles';

@Component({
  selector: 'app-store-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="store-page">
      <h1 class="store-title">Kimonos & Acessórios</h1>
      <p class="store-sub">Equipe-se com o melhor para o tatame</p>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <div class="filtros">
        <span class="chip" [class.active]="!categoriaId" (click)="filtrar(undefined)">Todos</span>
        <span class="chip" *ngFor="let c of categorias" [class.active]="categoriaId === c.id" (click)="filtrar(c.id)">{{ c.nome }}</span>
      </div>

      <div class="grid" *ngIf="produtos.length">
        <div class="prod" *ngFor="let p of produtos">
          <div class="img" [style.backgroundImage]="'url(/assets/loja/' + p.id + '.webp)'">
            <span>🥋</span>
          </div>
          <div class="info">
            <span class="cat">{{ p.categoria.nome }}</span>
            <h3>{{ p.nome }}</h3>
            <p class="desc">{{ p.descricao }}</p>
            <span class="preco">R$ {{ menorPreco(p) | number:'1.2-2' }}</span>
            <div class="vars">
              <button class="var" *ngFor="let v of p.variacoes"
                      [class.sel]="selecionada[p.id] === v.id"
                      [disabled]="v.estoque <= 0"
                      (click)="selecionada[p.id] = v.id">
                {{ rotulo(v) }}
              </button>
            </div>
            <button class="btn" [disabled]="!selecionada[p.id] || adicionando" (click)="adicionar(p)">Adicionar ao carrinho</button>
          </div>
        </div>
      </div>

      <div class="empty" *ngIf="!produtos.length && !carregando">Nenhum produto encontrado.</div>
      <div class="empty" *ngIf="carregando">Carregando…</div>
    </div>
  `,
  styles: [STORE_STYLES]
})
export class StoreCatalogoComponent implements OnInit {
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
    this.loja.categorias().subscribe({ next: c => this.categorias = c.filter(x => x.ativo), error: () => {} });
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.loja.listarProdutos(this.categoriaId).subscribe({
      next: r => { this.produtos = r.content; this.carregando = false; },
      error: e => { this.erro = e?.error?.message || 'Erro ao carregar produtos.'; this.carregando = false; }
    });
  }

  filtrar(id?: number): void { this.categoriaId = id; this.carregar(); }

  menorPreco(p: Produto): number {
    if (!p.variacoes?.length) return p.preco;
    return Math.min(...p.variacoes.map(v => v.precoEfetivo));
  }

  rotulo(v: Variacao): string {
    return [v.tamanho, v.cor].filter(Boolean).join(' / ') || v.sku;
  }

  adicionar(p: Produto): void {
    const variacaoId = this.selecionada[p.id];
    if (!variacaoId) return;
    this.adicionando = true;
    this.erro = null;
    this.loja.adicionarItem(variacaoId, 1).subscribe({
      next: () => { this.adicionando = false; this.aviso = `${p.nome} adicionado ao carrinho!`; },
      error: e => { this.adicionando = false; this.erro = e?.error?.message || 'Não foi possível adicionar ao carrinho.'; }
    });
  }
}
