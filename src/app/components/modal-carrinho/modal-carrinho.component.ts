import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LojaApiService, Carrinho, ItemCarrinho } from '../../services/loja-api.service';
import { ModalCarrinhoService } from '../../services/modal-carrinho.service';

@Component({
  selector: 'app-modal-carrinho',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-overlay" *ngIf="mostrarModal" (click)="fecharModal()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2>🛒 Meu Carrinho</h2>
          <button class="btn-fechar" (click)="fecharModal()">✕</button>
        </div>

        <div class="modal-body" *ngIf="itens.length > 0; else carrinhoVazio">
          <!-- Lista de Itens -->
          <div class="carrinho-itens">
            <div class="item-carrinho" *ngFor="let item of itens">
              <div class="item-info">
                <h4>{{ item.produto }}</h4>
                <p *ngIf="item.tamanho" class="opcao">Tamanho: {{ item.tamanho }}</p>
                <p *ngIf="item.cor" class="opcao">Cor: {{ item.cor }}</p>
              </div>
              <div class="item-quantidade">
                <button (click)="diminuirQuantidade(item)">−</button>
                <span>{{ item.quantidade }}</span>
                <button (click)="aumentarQuantidade(item)">+</button>
              </div>
              <div class="item-subtotal">
                R$ {{ item.subtotal | number: '1.2-2' }}
              </div>
              <button class="btn-remover" (click)="removerDoCarrinho(item)">🗑️</button>
            </div>
          </div>

          <!-- Resumo -->
          <div class="carrinho-resumo">
            <div class="resumo-linha total">
              <span>Subtotal:</span>
              <span>R$ {{ total | number: '1.2-2' }}</span>
            </div>
            <p class="frete-nota">Frete e cupom são calculados no checkout.</p>

            <button class="btn-checkout" (click)="irParaCheckout()">
              Prosseguir para Checkout
            </button>
          </div>
        </div>

        <ng-template #carrinhoVazio>
          <div class="carrinho-vazio">
            <p>Seu carrinho está vazio</p>
          </div>
        </ng-template>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.7);
      z-index: 1000;
      display: flex;
      align-items: flex-end;
      animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(100%);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .modal-content {
      background: white;
      width: 100%;
      max-width: 500px;
      border-radius: 16px 16px 0 0;
      max-height: 80vh;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      animation: slideUpContent 0.3s ease;
    }

    @keyframes slideUpContent {
      from {
        transform: translateY(20px);
        opacity: 0;
      }
      to {
        transform: translateY(0);
        opacity: 1;
      }
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem;
      border-bottom: 1px solid #eee;
      position: sticky;
      top: 0;
      background: white;
    }

    .modal-header h2 {
      margin: 0;
      color: #0a0a0a;
      font-size: 1.3rem;
    }

    .btn-fechar {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      padding: 0.25rem 0.5rem;
      transition: transform 0.2s ease;
    }

    .btn-fechar:hover {
      transform: scale(1.2);
    }

    .modal-body {
      padding: 1.5rem;
      flex: 1;
      overflow-y: auto;
    }

    .carrinho-itens {
      display: flex;
      flex-direction: column;
      gap: 1rem;
      margin-bottom: 2rem;
    }

    .item-carrinho {
      display: grid;
      grid-template-columns: 1fr 80px 80px 30px;
      gap: 0.8rem;
      align-items: center;
      padding: 1rem;
      background: #f9f9f9;
      border-radius: 8px;
    }

    .item-info h4 {
      margin: 0;
      color: #0a0a0a;
      font-size: 0.9rem;
      font-weight: 600;
    }

    .opcao {
      font-size: 0.8rem;
      color: #666;
      margin: 0.25rem 0 0;
    }

    .item-quantidade {
      display: flex;
      align-items: center;
      gap: 0.3rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      justify-content: center;
    }

    .item-quantidade button {
      background: none;
      border: none;
      padding: 0.3rem 0.5rem;
      cursor: pointer;
      font-weight: 600;
      font-size: 0.9rem;
    }

    .item-quantidade span {
      min-width: 25px;
      text-align: center;
      font-size: 0.9rem;
    }

    .item-subtotal {
      text-align: right;
      font-weight: 700;
      color: #c9a84c;
      font-size: 0.9rem;
    }

    .btn-remover {
      background: none;
      border: none;
      font-size: 1rem;
      cursor: pointer;
      padding: 0.25rem;
    }

    .carrinho-resumo {
      display: flex;
      flex-direction: column;
      gap: 0.8rem;
      padding-top: 1rem;
      border-top: 1px solid #eee;
    }

    .resumo-linha {
      display: flex;
      justify-content: space-between;
      font-size: 0.95rem;
    }

    .resumo-linha.total {
      font-size: 1.1rem;
      font-weight: 700;
      color: #0a0a0a;
      border-top: 2px solid #c9a84c;
      padding-top: 0.8rem;
      margin-top: 0.5rem;
    }

    .frete-nota {
      font-size: 0.8rem;
      color: #999;
      margin: 0.25rem 0 0;
      text-align: center;
    }

    .btn-checkout {
      background: #c9a84c;
      color: white;
      border: none;
      padding: 0.9rem;
      border-radius: 6px;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.3s ease;
      margin-top: 1rem;
    }

    .btn-checkout:hover {
      background: #b39539;
      transform: translateY(-2px);
    }

    .carrinho-vazio {
      text-align: center;
      padding: 2rem 1rem;
      color: #666;
    }

    .carrinho-vazio p {
      font-size: 1rem;
      margin: 0;
    }

    @media (max-width: 768px) {
      .modal-content {
        max-width: 100%;
      }

      .item-carrinho {
        grid-template-columns: 1fr 60px 60px 25px;
        gap: 0.5rem;
        padding: 0.75rem;
      }
    }
  `]
})
export class ModalCarrinhoComponent implements OnInit {
  mostrarModal = false;
  itens: ItemCarrinho[] = [];
  total = 0;

  constructor(
    private loja: LojaApiService,
    private modalCarrinhoService: ModalCarrinhoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.modalCarrinhoService.mostrarModal$.subscribe(estado => {
      this.mostrarModal = estado;
      // Sempre que abrir, sincroniza com o servidor.
      if (estado) {
        this.loja.verCarrinho().subscribe({ error: () => {} });
      }
    });

    // Observa o estado reativo do carrinho (compartilhado com o catalogo).
    this.loja.carrinho$.subscribe((c: Carrinho | null) => {
      this.itens = c?.itens ?? [];
      this.total = c?.subtotal ?? 0;
    });
  }

  aumentarQuantidade(item: ItemCarrinho): void {
    if (item.quantidade >= item.estoqueDisponivel) return;
    this.loja.atualizarItem(item.id, item.quantidade + 1).subscribe({ error: () => {} });
  }

  diminuirQuantidade(item: ItemCarrinho): void {
    if (item.quantidade <= 1) return;
    this.loja.atualizarItem(item.id, item.quantidade - 1).subscribe({ error: () => {} });
  }

  removerDoCarrinho(item: ItemCarrinho): void {
    this.loja.removerItem(item.id).subscribe({ error: () => {} });
  }

  irParaCheckout(): void {
    this.fecharModal();
    this.router.navigate(['/loja/checkout']);
  }

  fecharModal(): void {
    this.modalCarrinhoService.fecharModal();
  }
}
