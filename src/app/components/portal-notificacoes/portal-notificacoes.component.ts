import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacaoService, Notificacao } from '../../services/notificacao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-portal-notificacoes',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>Notificações</h1><p>Comunicados e avisos da academia</p></div>
        <button class="btn-novo" (click)="marcarTodas()" *ngIf="itens.length">Marcar todas como lidas</button>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="lista">
        <div class="notif" *ngFor="let n of itens" [class.lida]="n.lida" (click)="abrir(n)">
          <div class="dot" [class.on]="!n.lida"></div>
          <div class="body">
            <div class="head"><strong>{{ n.titulo }}</strong><small>{{ n.criadoEm | date:'dd/MM/yyyy HH:mm' }}</small></div>
            <p>{{ n.mensagem }}</p>
          </div>
        </div>
        <div class="empty" *ngIf="itens.length === 0">Nenhuma notificação.</div>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .lista { display: flex; flex-direction: column; gap: .75rem; }
    .notif { background: #fff; border-radius: 10px; padding: 1rem 1.25rem; box-shadow: 0 2px 8px rgba(0,0,0,.06); display: flex; gap: 1rem; cursor: pointer; border-left: 4px solid #c9a84c; }
    .notif.lida { opacity: .65; border-left-color: #ddd; }
    .dot { width: 10px; height: 10px; border-radius: 50%; background: transparent; margin-top: .4rem; flex-shrink: 0; }
    .dot.on { background: #ef4444; }
    .head { display: flex; justify-content: space-between; gap: 1rem; }
    .head small { color: #999; font-size: .8rem; }
    .body p { margin: .35rem 0 0; color: #555; }
  `]
})
export class PortalNotificacoesComponent implements OnInit {
  itens: Notificacao[] = [];
  erro: string | null = null;

  constructor(private notif: NotificacaoService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.notif.listar().subscribe({
      next: r => this.itens = r.content,
      error: e => this.erro = e?.error?.message || 'Erro ao carregar notificações.'
    });
  }

  abrir(n: Notificacao): void {
    if (n.lida) return;
    this.notif.marcarLida(n.id).subscribe({ next: () => n.lida = true, error: () => {} });
  }

  marcarTodas(): void {
    this.notif.marcarTodasLidas().subscribe({ next: () => this.carregar(), error: () => {} });
  }
}
