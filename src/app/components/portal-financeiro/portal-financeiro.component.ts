import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GestaoService, Mensalidade, StatusMensalidade } from '../../services/gestao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-portal-financeiro',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page portal-financeiro-container">
      <div class="page-header"><div><h1>Financeiro</h1><p>Suas mensalidades e recibos</p></div></div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="tabela-section">
        <table>
          <thead><tr><th>Competência</th><th>Plano</th><th>Valor</th><th>Total</th><th>Vencimento</th><th>Status</th><th>Recibo</th></tr></thead>
          <tbody>
            <tr *ngFor="let m of mensalidades">
              <td>{{ pad(m.mes) }}/{{ m.ano }}</td>
              <td>{{ m.plano }}</td>
              <td>R$ {{ m.valor | number:'1.2-2' }}</td>
              <td>R$ {{ m.valorTotal | number:'1.2-2' }}</td>
              <td>{{ m.dataVencimento }}</td>
              <td><span class="badge" [ngClass]="badge(m.status)">{{ m.status }}</span></td>
              <td>
                <button class="icon-btn" *ngIf="m.status === 'PAGA'" title="Baixar recibo" (click)="recibo(m)">🧾</button>
                <span *ngIf="m.status !== 'PAGA'">—</span>
              </td>
            </tr>
            <tr *ngIf="mensalidades.length === 0"><td colspan="7" class="empty">Nenhuma mensalidade encontrada.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES, `
    .portal-financeiro-container .tabela-section { overflow-x:auto; }
    .portal-financeiro-container .icon-btn { font-size:1.2rem; padding:0.6rem; }
    .portal-financeiro-container table { min-width:600px; }
    @media (max-width:768px) {
      .portal-financeiro-container .icon-btn { font-size:1.4rem; padding:0.8rem; }
    }
    @media (max-width:576px) {
      .portal-financeiro-container .tabela-section { padding:0.5rem; }
      .portal-financeiro-container .icon-btn { width:100%; text-align:center; }
    }
    `]
})
export class PortalFinanceiroComponent implements OnInit {
  mensalidades: Mensalidade[] = [];
  erro: string | null = null;

  constructor(private gestao: GestaoService) {}

  ngOnInit(): void {
    this.gestao.minhasMensalidades().subscribe({
      next: r => this.mensalidades = r.content,
      error: e => this.erro = e?.error?.message || 'Não foi possível carregar suas mensalidades.'
    });
  }

  recibo(m: Mensalidade): void {
    this.gestao.meuReciboBlob(m.id).subscribe({
      next: blob => window.open(URL.createObjectURL(blob), '_blank'),
      error: e => this.erro = e?.error?.message || 'Não foi possível abrir o recibo.'
    });
  }

  pad(n: number): string { return n < 10 ? '0' + n : '' + n; }
  badge(s: StatusMensalidade): string {
    return s === 'PAGA' ? 'ok' : s === 'PENDENTE' ? 'warn' : s === 'ATRASADA' ? 'off' : 'info';
  }
}
