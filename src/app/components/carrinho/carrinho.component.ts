import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { LojaService, ItemPedido, Produto } from '../../services/loja.service';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="carrinho-container">
      <h2>🛒 Meu Carrinho</h2>

      <div class="carrinho-content" *ngIf="itens.length > 0; else carrinhoVazio">
        <!-- Lista de Itens -->
        <div class="carrinho-itens">
          <div class="item-carrinho" *ngFor="let item of itens">
            <div class="item-info">
              <h4>{{ obterNomeProduto(item.id) }}</h4>
              <p *ngIf="item.tamanho" class="opcao">Tamanho: {{ item.tamanho }}</p>
              <p *ngIf="item.cor" class="opcao">Cor: {{ item.cor }}</p>
            </div>
            <div class="item-quantidade">
              <button (click)="diminuirQuantidade(item.id)">−</button>
              <span>{{ item.quantidade }}</span>
              <button (click)="aumentarQuantidade(item.id)">+</button>
            </div>
            <div class="item-subtotal">
              R$ {{ calcularSubtotal(item) | number: '1.2-2' }}
            </div>
            <button class="btn-remover" (click)="removerDoCarrinho(item.id)">
              🗑️
            </button>
          </div>
        </div>

        <!-- Resumo -->
        <div class="carrinho-resumo">
          <div class="resumo-linha">
            <span>Subtotal:</span>
            <span>R$ {{ total | number: '1.2-2' }}</span>
          </div>
          <div class="resumo-linha">
            <span>Frete:</span>
            <span>R$ {{ frete | number: '1.2-2' }}</span>
          </div>
          <div class="resumo-linha total">
            <span>Total:</span>
            <span>R$ {{ (total + frete) | number: '1.2-2' }}</span>
          </div>

          <button class="btn-checkout" (click)="irParaCheckout()">
            Prosseguir para Checkout
          </button>
          <button class="btn-continuar" (click)="fechar()">
            Continuar Comprando
          </button>
        </div>
      </div>

      <ng-template #carrinhoVazio>
        <div class="carrinho-vazio">
          <p>Seu carrinho está vazio</p>
          <button (click)="fechar()" class="btn-voltar">Voltar para Loja</button>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .carrinho-container {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      max-width: 600px;
      margin: 0 auto;
    }

    h2 {
      margin: 0 0 2rem;
      color: #0a0a0a;
      font-size: 1.5rem;
    }

    .carrinho-content {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }

    .carrinho-itens {
      display: flex;
      flex-direction: column;
      gap: 1rem;
      border-bottom: 1px solid #eee;
      padding-bottom: 1rem;
    }

    .item-carrinho {
      display: grid;
      grid-template-columns: 1fr 100px 100px 40px;
      gap: 1rem;
      align-items: center;
      padding: 1rem;
      background: #f9f9f9;
      border-radius: 8px;
    }

    .item-info h4 {
      margin: 0;
      color: #0a0a0a;
      font-size: 0.95rem;
    }

    .opcao {
      font-size: 0.85rem;
      color: #666;
      margin: 0.25rem 0 0;
    }

    .item-quantidade {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      justify-content: center;
    }

    .item-quantidade button {
      background: none;
      border: none;
      padding: 0.4rem 0.6rem;
      cursor: pointer;
      font-weight: 600;
    }

    .item-quantidade span {
      min-width: 30px;
      text-align: center;
    }

    .item-subtotal {
      text-align: right;
      font-weight: 700;
      color: #c9a84c;
    }

    .btn-remover {
      background: none;
      border: none;
      font-size: 1.2rem;
      cursor: pointer;
      padding: 0.4rem;
    }

    .carrinho-resumo {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .resumo-linha {
      display: flex;
      justify-content: space-between;
      padding: 0.75rem 0;
      border-bottom: 1px solid #eee;
    }

    .resumo-linha.total {
      font-size: 1.25rem;
      font-weight: 700;
      color: #0a0a0a;
      border: none;
      border-top: 2px solid #c9a84c;
      padding-top: 1rem;
    }

    .btn-checkout {
      background: #c9a84c;
      color: white;
      border: none;
      padding: 1rem;
      border-radius: 6px;
      font-weight: 700;
      font-size: 1rem;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-checkout:hover {
      background: #b39539;
    }

    .btn-continuar {
      background: white;
      color: #c9a84c;
      border: 2px solid #c9a84c;
      padding: 1rem;
      border-radius: 6px;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-continuar:hover {
      background: #f9f9f9;
    }

    .carrinho-vazio {
      text-align: center;
      padding: 3rem 1rem;
      color: #666;
    }

    .carrinho-vazio p {
      font-size: 1.1rem;
      margin-bottom: 1rem;
    }

    .btn-voltar {
      background: #c9a84c;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
    }

    .btn-voltar:hover {
      background: #b39539;
    }

    @media (max-width: 768px) {
      .item-carrinho {
        grid-template-columns: 1fr 50px 50px 30px;
        gap: 0.5rem;
        padding: 0.75rem;
      }
    }
  `]
})
export class CarrinhoComponent implements OnInit {
  itens: ItemPedido[] = [];
  total = 0;
  frete = 50; // Frete fixo
  produtos: Produto[] = [];

  constructor(private lojaService: LojaService) {}

  ngOnInit(): void {
    this.lojaService.carrinho$.subscribe(itens => {
      this.itens = itens;
      this.calcularTotal();
    });

    this.lojaService.obterProdutos().subscribe(produtos => {
      this.produtos = produtos;
      this.calcularTotal();
    });
  }

  obterNomeProduto(id: number): string {
    return this.produtos.find(p => p.id === id)?.nome || 'Produto';
  }

  calcularSubtotal(item: ItemPedido): number {
    const produto = this.produtos.find(p => p.id === item.id);
    return produto ? produto.preco * item.quantidade : 0;
  }

  calcularTotal(): void {
    this.total = this.lojaService.calcularTotal(this.itens, this.produtos);
  }

  aumentarQuantidade(id: number): void {
    const item = this.itens.find(i => i.id === id);
    if (item) {
      item.quantidade++;
      this.calcularTotal();
    }
  }

  diminuirQuantidade(id: number): void {
    const item = this.itens.find(i => i.id === id);
    if (item && item.quantidade > 1) {
      item.quantidade--;
      this.calcularTotal();
    }
  }

  removerDoCarrinho(id: number): void {
    this.lojaService.removerDoCarrinho(id);
  }

  irParaCheckout(): void {
    alert('Checkout integrado virá em breve! 🚀');
  }

  fechar(): void {
    alert('Voltando para loja...');
  }
}
