import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GestaoService, Auditoria } from '../../services/gestao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-auditoria',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page admin-auditoria-container">
      <div class="page-header">
        <div>
          <h1>Auditoria</h1>
          <p>Log de ações sensíveis (criações, alterações e exclusões autenticadas)</p>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="tabela-section">
        <table>
          <thead>
            <tr><th>Quando</th><th>Usuário</th><th>Método</th><th>Caminho</th><th>Status</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let a of itens">
              <td>{{ a.criadoEm | date:'dd/MM/yyyy HH:mm:ss' }}</td>
              <td>{{ a.usuarioEmail || '—' }}</td>
              <td><span class="badge info">{{ a.metodo }}</span></td>
              <td><code>{{ a.caminho }}</code></td>
              <td><span class="badge" [ngClass]="a.status < 400 ? 'ok' : 'off'">{{ a.status }}</span></td>
            </tr>
            <tr *ngIf="itens.length === 0"><td colspan="5" class="empty">Nenhum registro de auditoria.</td></tr>
          </tbody>
        </table>
      </div>

      <div class="paginacao" *ngIf="totalPages > 1">
        <button class="btn-novo" (click)="ir(page - 1)" [disabled]="page === 0">‹ Anterior</button>
        <span>Página {{ page + 1 }} de {{ totalPages }}</span>
        <button class="btn-novo" (click)="ir(page + 1)" [disabled]="page + 1 >= totalPages">Próxima ›</button>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    code { background: #f0f0f0; padding: .2rem .4rem; border-radius: 4px; font-size: .85rem; }
    .paginacao { display: flex; align-items: center; gap: 1rem; justify-content: center; margin-top: 1.5rem; }
  `]
})
export class AdminAuditoriaComponent implements OnInit {
  itens: Auditoria[] = [];
  page = 0;
  totalPages = 0;
  erro: string | null = null;

  constructor(private gestao: GestaoService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.gestao.listarAuditoria(this.page, 30).subscribe({
      next: r => { this.itens = r.content; this.totalPages = r.totalPages; },
      error: e => this.erro = e?.error?.message || 'Erro ao carregar auditoria.'
    });
  }

  ir(p: number): void {
    if (p < 0 || p >= this.totalPages) return;
    this.page = p;
    this.carregar();
  }
}
