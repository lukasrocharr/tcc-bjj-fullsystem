import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { LojaApiService, Carrinho, MetodoPagamento, Pedido } from '../../services/loja-api.service';
import { AuthService } from '../../services/auth.service';
import { STORE_STYLES } from '../store-shared.styles';

@Component({
  selector: 'app-store-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="store-page" style="max-width:760px;">
      <h1 class="store-title">Finalizar compra</h1>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="alert ok" *ngIf="!logado">
        Você está comprando como visitante. <a routerLink="/admin-login">Entre na sua conta</a> para acompanhar seus pedidos.
      </div>

      <ng-container *ngIf="!pedido; else confirmado">
        <ng-container *ngIf="carrinho && carrinho.itens.length; else vazio">
          <form #f="ngForm" (ngSubmit)="finalizar()">
            <h2 style="font-size:1.2rem;color:#0a0a0a;">Endereço de entrega</h2>
            <div class="form-grid" style="margin-bottom:1.5rem;">
              <div class="fg"><label>CEP</label><input [(ngModel)]="end.cep" name="cep" required maxlength="9"></div>
              <div class="fg"><label>Cidade</label><input [(ngModel)]="end.cidade" name="cidade" required></div>
              <div class="fg full"><label>Logradouro</label><input [(ngModel)]="end.logradouro" name="logradouro" required></div>
              <div class="fg"><label>Número</label><input [(ngModel)]="end.numero" name="numero" required></div>
              <div class="fg"><label>Complemento</label><input [(ngModel)]="end.complemento" name="complemento"></div>
              <div class="fg"><label>Bairro</label><input [(ngModel)]="end.bairro" name="bairro"></div>
              <div class="fg"><label>UF</label><input [(ngModel)]="end.uf" name="uf" required maxlength="2"></div>
            </div>

            <h2 style="font-size:1.2rem;color:#0a0a0a;">Pagamento</h2>
            <div class="form-grid" style="margin-bottom:1.5rem;">
              <div class="fg"><label>Método</label>
                <select [(ngModel)]="metodo" name="metodo">
                  <option *ngFor="let m of metodos" [ngValue]="m">{{ m }}</option>
                </select>
              </div>
              <div class="fg"><label>Cupom (opcional)</label><input [(ngModel)]="cupom" name="cupom"></div>
            </div>

            <div class="resumo">
              <div class="linha"><span>Itens ({{ carrinho.totalItens }})</span><span>R$ {{ carrinho.subtotal | number:'1.2-2' }}</span></div>
              <div class="linha total"><span>Subtotal</span><span>R$ {{ carrinho.subtotal | number:'1.2-2' }}</span></div>
              <small style="color:#999">Frete e desconto são calculados no servidor.</small>
              <button class="btn" style="width:100%;margin-top:1rem;" type="submit" [disabled]="!f.valid || enviando">
                {{ enviando ? 'Processando…' : 'Confirmar pedido' }}
              </button>
            </div>
          </form>
        </ng-container>
        <ng-template #vazio><div class="empty">Carrinho vazio. <a routerLink="/loja">Voltar ao catálogo</a></div></ng-template>
      </ng-container>

      <ng-template #confirmado>
        <div class="resumo" style="text-align:center;">
          <div style="font-size:3rem;">✅</div>
          <h2>Pedido {{ pedido!.numero }} confirmado!</h2>
          <p>Status: <span class="badge ok">{{ pedido!.status }}</span></p>
          <div class="linha"><span>Subtotal</span><span>R$ {{ pedido!.subtotal | number:'1.2-2' }}</span></div>
          <div class="linha"><span>Frete</span><span>R$ {{ pedido!.frete | number:'1.2-2' }}</span></div>
          <div class="linha" *ngIf="pedido!.desconto"><span>Desconto</span><span>- R$ {{ pedido!.desconto | number:'1.2-2' }}</span></div>
          <div class="linha total"><span>Total</span><span>R$ {{ pedido!.total | number:'1.2-2' }}</span></div>
          <a class="btn" routerLink="/loja" style="display:inline-block;margin-top:1rem;text-decoration:none;">Continuar comprando</a>
        </div>
      </ng-template>
    </div>
  `,
  styles: [STORE_STYLES]
})
export class StoreCheckoutComponent implements OnInit {
  carrinho: Carrinho | null = null;
  pedido: Pedido | null = null;
  metodos: MetodoPagamento[] = ['PIX', 'BOLETO', 'CARTAO', 'DINHEIRO'];
  metodo: MetodoPagamento = 'PIX';
  cupom = '';
  end = { cep: '', logradouro: '', numero: '', complemento: '', bairro: '', cidade: '', uf: '' };
  enviando = false;
  erro: string | null = null;

  constructor(private loja: LojaApiService, private auth: AuthService, private router: Router) {}

  get logado(): boolean { return this.auth.isAuthenticated(); }

  ngOnInit(): void {
    this.loja.verCarrinho().subscribe({ next: c => this.carrinho = c, error: () => {} });
  }

  finalizar(): void {
    this.enviando = true;
    this.erro = null;
    this.loja.checkout({ endereco: this.end, cupomCodigo: this.cupom || undefined, metodo: this.metodo }).subscribe({
      next: p => { this.enviando = false; this.pedido = p; },
      error: e => { this.enviando = false; this.erro = e?.error?.message || 'Não foi possível finalizar o pedido.'; }
    });
  }
}
