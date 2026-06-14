import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LojaApiService, Pedido, StatusPedido } from '../../services/loja-api.service';
import { STORE_STYLES } from '../store-shared.styles';

@Component({
  selector: 'app-store-pedidos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="store-page">
      <h1 class="store-title">Meus Pedidos</h1>
      <p class="store-sub">Histórico das suas compras</p>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="resumo" *ngFor="let p of pedidos" style="margin-bottom:1rem;">
        <div class="linha" style="align-items:center;">
          <strong>{{ p.numero }}</strong>
          <span class="badge" [ngClass]="badge(p.status)">{{ p.status }}</span>
        </div>
        <small style="color:#999">{{ p.criadoEm | date:'dd/MM/yyyy HH:mm' }}</small>
        <ul style="margin:.75rem 0; padding-left:1.25rem; color:#555;">
          <li *ngFor="let i of p.itens">{{ i.quantidade }}× {{ i.nomeProduto }} — R$ {{ i.subtotal | number:'1.2-2' }}</li>
        </ul>
        <div class="linha total"><span>Total</span><span>R$ {{ p.total | number:'1.2-2' }}</span></div>
        <small *ngIf="p.rastreio" style="color:#555">Rastreio: {{ p.rastreio }}</small>
      </div>

      <div class="empty" *ngIf="pedidos.length === 0 && !carregando">
        Você ainda não fez pedidos. <a routerLink="/loja">Ver catálogo</a>
      </div>
    </div>
  `,
  styles: [STORE_STYLES]
})
export class StorePedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  carregando = true;
  erro: string | null = null;

  constructor(private loja: LojaApiService) {}

  ngOnInit(): void {
    this.loja.meusPedidos().subscribe({
      next: r => { this.pedidos = r.content; this.carregando = false; },
      error: e => { this.erro = e?.error?.message || 'Erro ao carregar seus pedidos.'; this.carregando = false; }
    });
  }

  badge(s: StatusPedido): string {
    return s === 'PAGO' || s === 'ENTREGUE' ? 'ok' : s === 'CANCELADO' ? 'off' : s === 'ENVIADO' ? 'info' : 'warn';
  }
}
