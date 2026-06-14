import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { GestaoService, Dashboard } from '../../services/gestao.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Dashboard Administrativo</h1>
        <p>Visão geral da academia e da loja</p>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="loading" *ngIf="carregando">Carregando indicadores…</div>

      <ng-container *ngIf="d as dash">
        <div class="stats-grid">
          <div class="stat-card receita">
            <div class="card-icon">💰</div>
            <div class="card-content">
              <h3>Receita do mês</h3>
              <p class="stat-value">R$ {{ dash.receitaTotalMes | number: '1.2-2' }}</p>
              <span class="card-period">Mensalidades R$ {{ dash.receitaMensalidadesMes | number: '1.2-2' }} · Loja R$ {{ dash.receitaLojaMes | number: '1.2-2' }}</span>
            </div>
          </div>

          <div class="stat-card alunos">
            <div class="card-icon">🧑‍🎓</div>
            <div class="card-content">
              <h3>Alunos ativos</h3>
              <p class="stat-value">{{ dash.alunosAtivos }}</p>
              <span class="card-period">+{{ dash.novasMatriculasMes }} matrículas este mês</span>
            </div>
          </div>

          <div class="stat-card inadimplencia">
            <div class="card-icon">⚠️</div>
            <div class="card-content">
              <h3>Inadimplência</h3>
              <p class="stat-value">R$ {{ dash.inadimplenciaValor | number: '1.2-2' }}</p>
              <span class="card-period">{{ dash.inadimplenciaQtd }} mensalidade(s) em atraso</span>
            </div>
          </div>

          <div class="stat-card evasao">
            <div class="card-icon">📉</div>
            <div class="card-content">
              <h3>Risco de evasão</h3>
              <p class="stat-value">{{ dash.riscoEvasao }}</p>
              <span class="card-period">Alunos com baixa frequência</span>
            </div>
          </div>

          <div class="stat-card turmas">
            <div class="card-icon">📚</div>
            <div class="card-content">
              <h3>Turmas</h3>
              <p class="stat-value">{{ dash.turmas }}</p>
              <span class="card-period">Ativas na grade</span>
            </div>
          </div>

          <div class="stat-card pedidos">
            <div class="card-icon">🛒</div>
            <div class="card-content">
              <h3>Pedidos pendentes</h3>
              <p class="stat-value">{{ dash.pedidosPendentes }}</p>
              <span class="card-period">{{ dash.produtos }} produtos no catálogo</span>
            </div>
          </div>
        </div>

        <div class="chart-section">
          <h2>Evolução da receita (últimos 6 meses)</h2>
          <div class="bar-chart">
            <div class="bar-col" *ngFor="let p of dash.serieReceita">
              <div class="bar-wrap">
                <span class="bar-value">{{ p.valor | number: '1.0-0' }}</span>
                <div class="bar" [style.height.%]="alturaBarra(p.valor)"></div>
              </div>
              <span class="bar-label">{{ p.competencia }}</span>
            </div>
            <div class="empty" *ngIf="dash.serieReceita.length === 0">Sem dados de receita.</div>
          </div>
        </div>

        <div class="quick-links">
          <a routerLink="/admin/financeiro" class="quick">💰 Financeiro</a>
          <a routerLink="/admin/frequencia" class="quick">✅ Frequência</a>
          <a routerLink="/admin/graduacao" class="quick">🥋 Graduação</a>
          <a routerLink="/admin/pedidos" class="quick">📦 Pedidos</a>
          <a routerLink="/admin/comunicados" class="quick">📢 Comunicados</a>
          <a routerLink="/admin/auditoria" class="quick">🔎 Auditoria</a>
        </div>
      </ng-container>
    </div>
  `,
  styles: [`
    .dashboard-container { padding: 0; }
    .dashboard-header { margin-bottom: 2rem; }
    .dashboard-header h1 { font-size: 2rem; color: #0a0a0a; margin: 0 0 .5rem; font-weight: 700; }
    .dashboard-header p { color: #888; margin: 0; }
    .alert.error { background: #fee2e2; border: 1px solid #fecaca; color: #dc2626; padding: .75rem 1rem; border-radius: 8px; margin-bottom: 1rem; }
    .loading { color: #888; padding: 1rem 0; }

    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin-bottom: 2.5rem; }
    .stat-card { background: white; border-radius: 12px; padding: 1.5rem; display: flex; align-items: center; gap: 1rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); border-left: 4px solid #c9a84c; transition: transform .3s ease; }
    .stat-card:hover { transform: translateY(-4px); }
    .stat-card.receita { border-left-color: #22c55e; }
    .stat-card.alunos { border-left-color: #3b82f6; }
    .stat-card.inadimplencia { border-left-color: #ef4444; }
    .stat-card.evasao { border-left-color: #f59e0b; }
    .stat-card.turmas { border-left-color: #8b5cf6; }
    .stat-card.pedidos { border-left-color: #06b6d4; }
    .card-icon { font-size: 2rem; }
    .card-content h3 { font-size: .8rem; color: #666; margin: 0 0 .4rem; font-weight: 600; text-transform: uppercase; letter-spacing: .5px; }
    .stat-value { font-size: 1.6rem; font-weight: 700; color: #0a0a0a; margin: .15rem 0; }
    .card-period { font-size: .72rem; color: #999; display: block; }

    .chart-section { background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); margin-bottom: 2rem; }
    .chart-section h2 { font-size: 1.2rem; color: #0a0a0a; margin: 0 0 1.5rem; font-weight: 700; }
    .bar-chart { display: flex; align-items: flex-end; gap: 1rem; height: 240px; padding-top: 1rem; }
    .bar-col { flex: 1; display: flex; flex-direction: column; align-items: center; height: 100%; }
    .bar-wrap { flex: 1; width: 100%; display: flex; flex-direction: column; justify-content: flex-end; align-items: center; gap: .35rem; }
    .bar { width: 70%; max-width: 60px; background: linear-gradient(180deg, #c9a84c, #b39539); border-radius: 6px 6px 0 0; min-height: 4px; transition: height .4s ease; }
    .bar-value { font-size: .72rem; color: #666; font-weight: 600; }
    .bar-label { margin-top: .5rem; font-size: .8rem; color: #888; }
    .empty { color: #999; }

    .quick-links { display: flex; flex-wrap: wrap; gap: 1rem; }
    .quick { background: white; border: 1px solid #eee; border-radius: 10px; padding: 1rem 1.5rem; text-decoration: none; color: #0a0a0a; font-weight: 600; box-shadow: 0 2px 8px rgba(0,0,0,.05); transition: all .3s ease; }
    .quick:hover { border-color: #c9a84c; color: #b39539; transform: translateY(-2px); }

    @media (max-width: 768px) { .stats-grid { grid-template-columns: 1fr; } .bar-value { display: none; } }
  `]
})
export class AdminDashboardComponent implements OnInit {
  d: Dashboard | null = null;
  carregando = true;
  erro: string | null = null;

  constructor(private gestao: GestaoService) {}

  ngOnInit(): void {
    this.gestao.dashboard().subscribe({
      next: d => { this.d = d; this.carregando = false; },
      error: e => { this.erro = e?.error?.message || 'Erro ao carregar o dashboard.'; this.carregando = false; }
    });
  }

  /** Altura percentual da barra relativa ao maior valor da serie. */
  alturaBarra(valor: number): number {
    if (!this.d || this.d.serieReceita.length === 0) return 0;
    const max = Math.max(...this.d.serieReceita.map(p => p.valor), 1);
    return Math.max(2, Math.round((valor / max) * 100));
  }
}
