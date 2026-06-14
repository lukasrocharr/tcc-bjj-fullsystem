import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { LojaApiService, Carrinho, ItemCarrinho } from '../../services/loja-api.service';
import { STORE_STYLES } from '../store-shared.styles';

@Component({
  selector: 'app-store-carrinho',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="store-page">
      <h1 class="store-title">Carrinho</h1>
      <p class="store-sub">Revise seus itens antes de finalizar</p>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <ng-container *ngIf="carrinho && carrinho.itens.length; else vazio">
        <table>
          <thead><tr><th>Produto</th><th>Preço</th><th>Qtd.</th><th>Subtotal</th><th></th></tr></thead>
          <tbody>
            <tr *ngFor="let i of carrinho.itens">
              <td>
                <strong>{{ i.produto }}</strong><br>
                <small style="color:#999">{{ variante(i) }} · SKU {{ i.sku }}</small>
              </td>
              <td>R$ {{ i.precoUnitario | number:'1.2-2' }}</td>
              <td>
                <input class="qty" type="number" min="1" [max]="i.estoqueDisponivel"
                       [ngModel]="i.quantidade" (change)="atualizar(i.id, $any($event.target).value)">
              </td>
              <td>R$ {{ i.subtotal | number:'1.2-2' }}</td>
              <td><button class="btn danger" (click)="remover(i.id)">✕</button></td>
            </tr>
          </tbody>
        </table>

        <div class="resumo" style="margin-top:1.5rem; max-width:360px; margin-left:auto;">
          <div class="linha total"><span>Subtotal</span><span>R$ {{ carrinho.subtotal | number:'1.2-2' }}</span></div>
          <button class="btn" style="width:100%; margin-top:1rem;" (click)="irCheckout()">Finalizar compra →</button>
        </div>
      </ng-container>

      <ng-template #vazio>
        <div class="empty">Seu carrinho está vazio. <a routerLink="/loja">Ver catálogo</a></div>
      </ng-template>
    </div>
  `,
  styles: [STORE_STYLES]
})
export class StoreCarrinhoComponent implements OnInit {
  carrinho: Carrinho | null = null;
  erro: string | null = null;

  constructor(private loja: LojaApiService, private router: Router) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.loja.verCarrinho().subscribe({
      next: c => this.carrinho = c,
      error: e => this.erro = e?.error?.message || 'Erro ao carregar o carrinho.'
    });
  }

  atualizar(itemId: number, valor: string): void {
    const q = parseInt(valor, 10);
    if (!q || q < 1) return;
    this.loja.atualizarItem(itemId, q).subscribe({
      next: c => this.carrinho = c,
      error: e => { this.erro = e?.error?.message || 'Erro ao atualizar.'; this.carregar(); }
    });
  }

  remover(itemId: number): void {
    this.loja.removerItem(itemId).subscribe({
      next: c => this.carrinho = c,
      error: e => this.erro = e?.error?.message || 'Erro ao remover item.'
    });
  }

  variante(i: ItemCarrinho): string {
    return [i.tamanho, i.cor].filter(v => !!v).join(' / ');
  }

  irCheckout(): void { this.router.navigate(['/loja/checkout']); }
}
