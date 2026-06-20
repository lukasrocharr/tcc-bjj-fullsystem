import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificacaoService } from '../../services/notificacao.service';

@Component({
  selector: 'app-portal-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="portal">
      <aside class="sidebar" [class.open]="menuAberto">
        <div class="sidebar-header">
          <span class="logo">🥋 <b>Meu Dojo</b></span>
          <button class="close" (click)="menuAberto = false">✕</button>
        </div>
        <nav>
          <a routerLink="/portal/inicio" routerLinkActive="active" class="item" (click)="menuAberto=false"><span>🏠</span> Início</a>
          <a routerLink="/portal/check-in" routerLinkActive="active" class="item" (click)="menuAberto=false"><span>✅</span> Check-in</a>
          <a routerLink="/portal/graduacoes" routerLinkActive="active" class="item" (click)="menuAberto=false"><span>🥋</span> Graduações</a>
          <a routerLink="/portal/financeiro" routerLinkActive="active" class="item" (click)="menuAberto=false"><span>💰</span> Financeiro</a>
          <a routerLink="/portal/notificacoes" routerLinkActive="active" class="item" (click)="menuAberto=false">
            <span>🔔</span> Notificações
            <b class="badge" *ngIf="naoLidas > 0">{{ naoLidas }}</b>
          </a>
        </nav>
        <div class="footer">
          <div class="user"><span class="avatar">👤</span><div><p>{{ nome }}</p><small>Aluno</small></div></div>
          <button class="logout" (click)="sair()">🚪 Sair</button>
        </div>
      </aside>

      <div class="main">
        <header class="topbar">
          <button class="menu" (click)="menuAberto = true">☰</button>
          <h2>Portal do Aluno</h2>
          <a routerLink="" class="site">Ver site ↗</a>
        </header>
        <main class="content"><router-outlet></router-outlet></main>
      </div>
    </div>
  `,
  styles: [`
    .portal { display: flex; min-height: 100vh; background: #f5f5f5; }
    .sidebar { width: 250px; background: linear-gradient(180deg,#0a0a0a,#1a1a1a); color: #fff; display: flex; flex-direction: column; position: fixed; height: 100vh; left: 0; top: 0; z-index: 1000; transition: transform .3s ease; }
    .sidebar-header { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,.1); display: flex; justify-content: space-between; align-items: center; }
    .logo { color: #c9a84c; font-size: 1.15rem; }
    .close { display: none; background: none; border: none; color: #fff; font-size: 1.3rem; cursor: pointer; }
    nav { flex: 1; padding: 1rem 0; }
    .item { display: flex; align-items: center; gap: .9rem; padding: .9rem 1.5rem; color: #ccc; text-decoration: none; border-left: 3px solid transparent; }
    .item:hover, .item.active { background: rgba(201,168,76,.15); color: #c9a84c; border-left-color: #c9a84c; }
    .item .badge { margin-left: auto; background: #ef4444; color: #fff; border-radius: 10px; padding: .1rem .45rem; font-size: .7rem; }
    .footer { padding: 1.5rem 1rem; border-top: 1px solid rgba(255,255,255,.1); }
    .user { display: flex; gap: .75rem; align-items: center; margin-bottom: 1rem; }
    .avatar { font-size: 1.7rem; }
    .user p { margin: 0; font-weight: 600; font-size: .9rem; }
    .user small { color: #999; }
    .logout { width: 100%; padding: .7rem; background: rgba(201,168,76,.2); border: 1px solid #c9a84c; color: #c9a84c; border-radius: 6px; cursor: pointer; font-weight: 600; }
    .logout:hover { background: #c9a84c; color: #0a0a0a; }
    .main { flex: 1; margin-left: 250px; display: flex; flex-direction: column; }
    .topbar { background: #fff; padding: 1rem 2rem; border-bottom: 1px solid #e0e0e0; display: flex; align-items: center; gap: 1rem; }
    .topbar h2 { margin: 0; font-size: 1.4rem; color: #0a0a0a; flex: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    @media (max-width: 768px) { .topbar h2 { font-size: 1.1rem; white-space: normal; overflow: visible; text-overflow: clip; } }
    .topbar .site { color: #c9a84c; text-decoration: none; font-weight: 600; font-size: .9rem; }
    .menu { display: none; background: none; border: none; font-size: 1.4rem; cursor: pointer; }
    .content { flex: 1; padding: 1rem; }
    @media (max-width: 768px) {
      .sidebar { transform: translateX(-100%); transition: transform .3s ease; width: 280px; }
      .sidebar.open { transform: translateX(0); }
      .close, .menu { display: block; }
      .main { margin-left: 0; }
    }
  `]
})
export class PortalLayoutComponent implements OnInit {
  menuAberto = false;
  nome = 'Aluno';
  naoLidas = 0;

  constructor(private auth: AuthService, private router: Router, private notif: NotificacaoService) {}

  ngOnInit(): void {
    this.nome = this.auth.getCurrentUser()?.nome || 'Aluno';
    if (!this.auth.isAuthenticated()) { this.router.navigate(['/admin-login']); return; }
    this.notif.contarNaoLidas().subscribe({ next: r => this.naoLidas = r.naoLidas, error: () => {} });
  }

  sair(): void {
    this.auth.logout();
    this.router.navigate(['/admin-login']);
  }
}
