import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LojaApiService } from '../../services/loja-api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-store-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="store">
      <header class="store-header">
        <a routerLink="/loja" class="brand">🥋 BJJ Store</a>
        <nav>
          <a routerLink="/loja" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Catálogo</a>
          <a routerLink="/loja/meus-pedidos" routerLinkActive="active" *ngIf="logado">Meus pedidos</a>
          <a routerLink="" class="site">Site</a>
          <a routerLink="/loja/carrinho" routerLinkActive="active" class="cart">
            🛒 <b *ngIf="totalItens > 0">{{ totalItens }}</b>
          </a>
        </nav>
      </header>
      <main><router-outlet></router-outlet></main>
      <footer class="store-footer">© {{ ano }} Academia BJJ — Loja oficial</footer>
    </div>
  `,
  styles: [`
    .store { min-height: 100vh; display: flex; flex-direction: column; background: #f6f6f6; }
    .store-header { background: #0a0a0a; color: #fff; padding: 1rem 2rem; display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 100; }
    .brand { color: #c9a84c; font-weight: 800; font-size: 1.3rem; text-decoration: none; }
    nav { display: flex; align-items: center; gap: 1.5rem; }
    nav a { color: #ccc; text-decoration: none; font-weight: 600; }
    nav a:hover, nav a.active { color: #c9a84c; }
    .cart b { background: #c9a84c; color: #0a0a0a; border-radius: 10px; padding: .05rem .45rem; font-size: .75rem; margin-left: .2rem; }
    main { flex: 1; padding: 2rem 1rem; }
    .store-footer { background: #0a0a0a; color: #888; text-align: center; padding: 1.5rem; font-size: .85rem; }
    @media (max-width: 600px) { .store-header { flex-direction: column; gap: .75rem; } nav { gap: 1rem; } }
  `]
})
export class StoreLayoutComponent implements OnInit {
  totalItens = 0;
  ano = new Date().getFullYear();

  constructor(private loja: LojaApiService, private auth: AuthService) {}

  get logado(): boolean { return this.auth.isAuthenticated(); }

  ngOnInit(): void {
    this.loja.verCarrinho().subscribe({ next: c => this.totalItens = c.totalItens, error: () => {} });
  }
}
