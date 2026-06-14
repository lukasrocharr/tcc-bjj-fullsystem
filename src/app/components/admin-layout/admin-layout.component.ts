import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="admin-layout">
      <!-- Sidebar -->
      <aside class="admin-sidebar" [class.open]="menuAberto">
        <div class="sidebar-header">
          <div class="logo-container">
            <img src="/assets/brand/logo.png" alt="BJJ Admin" class="admin-logo-img">
            <span class="logo-text">BJJ Admin</span>
          </div>
          <button class="menu-toggle-close" (click)="menuAberto = false">
            ✕
          </button>
        </div>

        <nav class="sidebar-nav">
          <a
            routerLink="/admin/dashboard"
            routerLinkActive="active"
            [routerLinkActiveOptions]="{ exact: true }"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">📊</span>
            <span class="label">Dashboard</span>
          </a>
          <a
            routerLink="/admin/turmas"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">📚</span>
            <span class="label">Turmas</span>
          </a>
          <a
            routerLink="/admin/alunos"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">🧑‍🎓</span>
            <span class="label">Alunos</span>
          </a>
          <a
            routerLink="/admin/matriculas"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">📝</span>
            <span class="label">Matrículas</span>
          </a>
          <a
            routerLink="/admin/frequencia"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">✅</span>
            <span class="label">Frequência</span>
          </a>
          <a
            routerLink="/admin/graduacao"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">🥋</span>
            <span class="label">Graduação</span>
          </a>
          <a
            routerLink="/admin/financeiro"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">💰</span>
            <span class="label">Financeiro</span>
          </a>
          <a
            routerLink="/admin/pedidos"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">📦</span>
            <span class="label">Pedidos</span>
          </a>
          <a
            routerLink="/admin/comunicados"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">📢</span>
            <span class="label">Comunicados</span>
          </a>
          <a
            routerLink="/admin/auditoria"
            routerLinkActive="active"
            class="nav-item"
            (click)="menuAberto = false"
          >
            <span class="icon">🔎</span>
            <span class="label">Auditoria</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <div class="user-info">
            <div class="user-avatar">👤</div>
            <div class="user-details">
              <p class="user-name">{{ usuarioNome }}</p>
              <p class="user-role">Administrador</p>
            </div>
          </div>
          <button class="btn-logout" (click)="fazerLogout()">
            🚪 Sair
          </button>
        </div>
      </aside>

      <!-- Main Content -->
      <div class="admin-main">
        <header class="admin-header">
          <button class="menu-toggle" (click)="menuAberto = true">
            ☰
          </button>
          <div class="header-title">
            <h2>Academia de BJJ - Painel Administrativo</h2>
          </div>
        </header>

        <main class="admin-content">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .admin-layout {
      display: flex;
      min-height: 100vh;
      background: #f5f5f5;
    }

    .admin-sidebar {
      width: 250px;
      background: linear-gradient(180deg, #0a0a0a 0%, #1a1a1a 100%);
      color: white;
      display: flex;
      flex-direction: column;
      overflow-y: auto;
      position: fixed;
      height: 100vh;
      left: 0;
      top: 0;
      z-index: 1000;
      box-shadow: 2px 0 10px rgba(0, 0, 0, 0.3);
    }

    .sidebar-header {
      padding: 1.5rem 1rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .logo-container {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }

    .logo-icon {
      font-size: 1.75rem;
    }

    .admin-logo-img {
      height: 36px;
      border-radius: 4px;
      object-fit: contain;
    }

    .logo-text {
      font-size: 1.25rem;
      font-weight: 700;
      color: #c9a84c;
    }

    .menu-toggle-close {
      display: none;
      background: none;
      border: none;
      color: white;
      font-size: 1.5rem;
      cursor: pointer;
    }

    .sidebar-nav {
      flex: 1;
      padding: 1rem 0;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem 1.5rem;
      color: #ccc;
      text-decoration: none;
      transition: all 0.3s ease;
      border-left: 3px solid transparent;
    }

    .nav-item:hover {
      background: rgba(201, 168, 76, 0.1);
      color: #c9a84c;
      border-left-color: #c9a84c;
    }

    .nav-item.active {
      background: rgba(201, 168, 76, 0.2);
      color: #c9a84c;
      border-left-color: #c9a84c;
    }

    .nav-item .icon {
      font-size: 1.25rem;
      min-width: 30px;
    }

    .sidebar-footer {
      padding: 1.5rem 1rem;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
    }

    .user-info {
      display: flex;
      gap: 0.75rem;
      margin-bottom: 1rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }

    .user-avatar {
      font-size: 2rem;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      background: rgba(201, 168, 76, 0.2);
      border-radius: 50%;
    }

    .user-details {
      flex: 1;
    }

    .user-name {
      margin: 0;
      font-weight: 600;
      color: white;
      font-size: 0.9rem;
    }

    .user-role {
      margin: 0.25rem 0 0;
      font-size: 0.75rem;
      color: #999;
    }

    .btn-logout {
      width: 100%;
      padding: 0.75rem 1rem;
      background: rgba(201, 168, 76, 0.2);
      border: 1px solid #c9a84c;
      color: #c9a84c;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      transition: all 0.3s ease;
      font-size: 0.9rem;
    }

    .btn-logout:hover {
      background: #c9a84c;
      color: #0a0a0a;
    }

    .admin-main {
      flex: 1;
      margin-left: 250px;
      display: flex;
      flex-direction: column;
    }

    .admin-header {
      background: white;
      padding: 1rem 2rem;
      border-bottom: 1px solid #e0e0e0;
      display: flex;
      align-items: center;
      gap: 1rem;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }

    .menu-toggle {
      display: none;
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: #0a0a0a;
    }

    .header-title h2 {
      margin: 0;
      font-size: 1.5rem;
      color: #0a0a0a;
      font-weight: 700;
    }

    .admin-content {
      flex: 1;
      padding: 2rem;
      overflow-y: auto;
    }

    @media (max-width: 768px) {
      .admin-sidebar {
        width: 280px;
        transform: translateX(-100%);
        transition: transform 0.3s ease;
      }

      .admin-sidebar.open {
        transform: translateX(0);
      }

      .menu-toggle-close {
        display: block;
      }

      .menu-toggle {
        display: block;
      }

      .admin-main {
        margin-left: 0;
      }

      .header-title h2 {
        font-size: 1.25rem;
      }

      .admin-content {
        padding: 1.5rem;
      }
    }

    @media (max-width: 480px) {
      .admin-sidebar {
        width: 100%;
      }

      .logo-text {
        display: none;
      }

      .header-title h2 {
        font-size: 1rem;
      }
    }

    /* Scrollbar styling */
    .admin-sidebar::-webkit-scrollbar {
      width: 6px;
    }

    .admin-sidebar::-webkit-scrollbar-track {
      background: rgba(255, 255, 255, 0.05);
    }

    .admin-sidebar::-webkit-scrollbar-thumb {
      background: rgba(201, 168, 76, 0.5);
      border-radius: 3px;
    }

    .admin-sidebar::-webkit-scrollbar-thumb:hover {
      background: rgba(201, 168, 76, 0.7);
    }
  `]
})
export class AdminLayoutComponent implements OnInit {
  menuAberto = false;
  usuarioNome = 'Admin';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const usuario = this.authService.getCurrentUser();
    if (usuario) {
      this.usuarioNome = usuario.nome || 'Administrador';
    }

    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/admin-login']);
    }
  }

  fazerLogout(): void {
    this.authService.logout();
    this.router.navigate(['/admin-login']);
  }
}
