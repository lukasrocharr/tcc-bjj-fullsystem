import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  GestaoService, Mensalidade, RelatorioFinanceiro, StatusMensalidade, MetodoPagamento
} from '../../services/gestao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-financeiro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Gestão Financeira</h1>
          <p>Mensalidades, pagamentos e relatório consolidado (mensalidades + loja)</p>
        </div>
        <div class="toolbar">
          <button class="btn-novo" (click)="atualizarAtrasadas()">↻ Reprocessar atrasadas</button>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <!-- Relatorio consolidado -->
      <div class="cards" *ngIf="rel as r">
        <div class="card ok"><span>Recebido</span><strong>R$ {{ r.totalRecebido | number:'1.2-2' }}</strong><small>{{ r.qtdPagas }} pagas</small></div>
        <div class="card warn"><span>Pendente</span><strong>R$ {{ r.totalPendente | number:'1.2-2' }}</strong><small>{{ r.qtdPendentes }} pendentes</small></div>
        <div class="card off"><span>Atrasado</span><strong>R$ {{ r.totalAtrasado | number:'1.2-2' }}</strong><small>{{ r.qtdAtrasadas }} atrasadas</small></div>
        <div class="card info"><span>Loja</span><strong>R$ {{ r.totalLoja | number:'1.2-2' }}</strong><small>vendas pagas</small></div>
        <div class="card total"><span>Total geral</span><strong>R$ {{ r.totalGeral | number:'1.2-2' }}</strong></div>
      </div>

      <!-- Gerar competencia -->
      <div class="form-section">
        <h2>Gerar mensalidades da competência</h2>
        <div class="form-row">
          <div class="form-group">
            <label>Ano</label>
            <input type="number" [(ngModel)]="ano" name="ano">
          </div>
          <div class="form-group">
            <label>Mês</label>
            <select [(ngModel)]="mes" name="mes">
              <option *ngFor="let m of meses; let i = index" [ngValue]="i + 1">{{ m }}</option>
            </select>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn-salvar" (click)="gerar()" [disabled]="gerando">Gerar</button>
          <button class="btn-novo" (click)="aplicarFiltro()">Filtrar competência</button>
        </div>
      </div>

      <!-- Filtros -->
      <div class="toolbar" style="margin-bottom:1rem;">
        <select class="search-input" [(ngModel)]="filtroStatus" (change)="carregar()">
          <option [ngValue]="undefined">Todos os status</option>
          <option *ngFor="let s of statuses" [ngValue]="s">{{ s }}</option>
        </select>
      </div>

      <div class="tabela-section">
        <table>
          <thead>
            <tr>
              <th>Aluno</th><th>Plano</th><th>Comp.</th><th>Valor</th><th>Total</th>
              <th>Vencimento</th><th>Status</th><th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let m of mensalidades">
              <td><strong>{{ m.aluno.nome }}</strong></td>
              <td>{{ m.plano }}</td>
              <td>{{ pad(m.mes) }}/{{ m.ano }}</td>
              <td>R$ {{ m.valor | number:'1.2-2' }}</td>
              <td>R$ {{ m.valorTotal | number:'1.2-2' }}<small *ngIf="m.multa">+multa/juros</small></td>
              <td>{{ m.dataVencimento }}</td>
              <td><span class="badge" [ngClass]="badge(m.status)">{{ m.status }}</span></td>
              <td class="acoes">
                <button class="icon-btn" *ngIf="m.status !== 'PAGA' && m.status !== 'CANCELADA'" title="Pagar" (click)="abrirPagamento(m)">💵</button>
                <button class="icon-btn" *ngIf="m.status === 'PAGA'" title="Recibo" (click)="recibo(m)">🧾</button>
                <button class="icon-btn del" *ngIf="m.status !== 'CANCELADA'" title="Cancelar" (click)="cancelar(m)">✖️</button>
              </td>
            </tr>
            <tr *ngIf="mensalidades.length === 0"><td colspan="8" class="empty">Nenhuma mensalidade encontrada.</td></tr>
          </tbody>
        </table>
      </div>

      <!-- Modal pagamento -->
      <div class="modal-overlay" *ngIf="pagando" (click)="pagando = null">
        <div class="modal" (click)="$event.stopPropagation()">
          <h3>Registrar pagamento</h3>
          <p>{{ pagando.aluno.nome }} — {{ pad(pagando.mes) }}/{{ pagando.ano }} — R$ {{ pagando.valorTotal | number:'1.2-2' }}</p>
          <div class="form-group full">
            <label>Método</label>
            <select [(ngModel)]="metodo" name="metodo">
              <option *ngFor="let mt of metodos" [ngValue]="mt">{{ mt }}</option>
            </select>
          </div>
          <div class="modal-actions">
            <button class="btn-salvar" style="flex:1" (click)="confirmarPagamento()">Confirmar</button>
            <button class="btn-cancelar" (click)="pagando = null">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px,1fr)); gap: 1rem; margin-bottom: 2rem; }
    .card { background: white; border-radius: 12px; padding: 1.25rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); border-left: 4px solid #c9a84c; display: flex; flex-direction: column; gap: .25rem; }
    .card span { font-size: .75rem; color: #666; text-transform: uppercase; font-weight: 600; }
    .card strong { font-size: 1.3rem; color: #0a0a0a; }
    .card small { font-size: .72rem; color: #999; }
    .card.ok { border-left-color: #22c55e; }
    .card.warn { border-left-color: #f59e0b; }
    .card.off { border-left-color: #ef4444; }
    .card.info { border-left-color: #3b82f6; }
    .card.total { border-left-color: #c9a84c; }
    td small { display:block; color:#999; font-size:.7rem; }
  `]
})
export class AdminFinanceiroComponent implements OnInit {
  mensalidades: Mensalidade[] = [];
  rel: RelatorioFinanceiro | null = null;

  meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
  statuses: StatusMensalidade[] = ['PENDENTE', 'PAGA', 'ATRASADA', 'CANCELADA'];
  metodos: MetodoPagamento[] = ['PIX', 'BOLETO', 'CARTAO', 'DINHEIRO'];

  ano = new Date().getFullYear();
  mes = new Date().getMonth() + 1;
  filtroStatus?: StatusMensalidade;

  pagando: Mensalidade | null = null;
  metodo: MetodoPagamento = 'PIX';
  gerando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private gestao: GestaoService) {}

  ngOnInit(): void {
    this.carregar();
    this.carregarRelatorio();
  }

  carregar(): void {
    this.gestao.listarMensalidades(undefined, this.filtroStatus).subscribe({
      next: r => this.mensalidades = r.content,
      error: e => this.erro = this.msg(e)
    });
  }

  carregarRelatorio(ano?: number, mes?: number): void {
    this.gestao.relatorioFinanceiro(ano, mes).subscribe({
      next: r => this.rel = r,
      error: e => this.erro = this.msg(e)
    });
  }

  aplicarFiltro(): void {
    this.carregarRelatorio(this.ano, this.mes);
  }

  gerar(): void {
    this.gerando = true;
    this.erro = null;
    this.gestao.gerarMensalidades(this.ano, this.mes).subscribe({
      next: r => {
        this.gerando = false;
        this.aviso = `${r.geradas} geradas, ${r.ignoradas} já existentes (${this.pad(r.mes)}/${r.ano}).`;
        this.carregar();
        this.carregarRelatorio();
      },
      error: e => { this.gerando = false; this.erro = this.msg(e); }
    });
  }

  abrirPagamento(m: Mensalidade): void {
    this.pagando = m;
    this.metodo = 'PIX';
  }

  confirmarPagamento(): void {
    if (!this.pagando) return;
    this.gestao.pagar(this.pagando.id, this.metodo).subscribe({
      next: () => { this.pagando = null; this.aviso = 'Pagamento registrado.'; this.carregar(); this.carregarRelatorio(); },
      error: e => { this.erro = this.msg(e); this.pagando = null; }
    });
  }

  cancelar(m: Mensalidade): void {
    if (!confirm(`Cancelar a mensalidade de ${m.aluno.nome}?`)) return;
    this.gestao.cancelarMensalidade(m.id).subscribe({
      next: () => { this.carregar(); this.carregarRelatorio(); },
      error: e => this.erro = this.msg(e)
    });
  }

  recibo(m: Mensalidade): void {
    this.gestao.reciboBlob(m.id).subscribe({
      next: blob => window.open(URL.createObjectURL(blob), '_blank'),
      error: e => this.erro = this.msg(e)
    });
  }

  atualizarAtrasadas(): void {
    this.gestao.atualizarAtrasadas().subscribe({
      next: r => { this.aviso = `${r.processadas} mensalidade(s) reprocessada(s).`; this.carregar(); this.carregarRelatorio(); },
      error: e => this.erro = this.msg(e)
    });
  }

  pad(n: number): string { return n < 10 ? '0' + n : '' + n; }

  badge(s: StatusMensalidade): string {
    return s === 'PAGA' ? 'ok' : s === 'PENDENTE' ? 'warn' : s === 'ATRASADA' ? 'off' : 'info';
  }

  private msg(e: any): string { return e?.error?.message || 'Ocorreu um erro ao processar a requisição.'; }
}
