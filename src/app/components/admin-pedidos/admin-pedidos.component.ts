import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LojaApiService, Pedido, StatusPedido } from '../../services/loja-api.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Pedidos da Loja</h1>
          <p>Acompanhe e atualize o status dos pedidos (cancelar devolve estoque)</p>
        </div>
        <div class="toolbar">
          <select class="search-input" [(ngModel)]="filtroStatus" (change)="carregar()">
            <option [ngValue]="undefined">Todos os status</option>
            <option *ngFor="let s of statuses" [ngValue]="s">{{ s }}</option>
          </select>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="tabela-section">
        <table>
          <thead>
            <tr><th>Pedido</th><th>Data</th><th>Itens</th><th>Total</th><th>Status</th><th>Ações</th></tr>
          </thead>
          <tbody>
            <ng-container *ngFor="let p of pedidos">
              <tr>
                <td><strong>{{ p.numero }}</strong></td>
                <td>{{ p.criadoEm | date:'dd/MM/yyyy HH:mm' }}</td>
                <td>{{ p.itens.length }} item(ns)</td>
                <td>R$ {{ p.total | number:'1.2-2' }}</td>
                <td><span class="badge" [ngClass]="badge(p.status)">{{ p.status }}</span></td>
                <td class="acoes">
                  <button class="icon-btn" title="Detalhes" (click)="expandido = expandido === p.id ? null : p.id">👁️</button>
                </td>
              </tr>
              <tr *ngIf="expandido === p.id">
                <td colspan="6">
                  <div class="detalhe">
                    <div>
                      <strong>Entrega:</strong>
                      {{ p.endereco.logradouro }}, {{ p.endereco.numero }}
                      {{ p.endereco.complemento }} — {{ p.endereco.bairro }},
                      {{ p.endereco.cidade }}/{{ p.endereco.uf }} — {{ p.endereco.cep }}
                    </div>
                    <ul>
                      <li *ngFor="let i of p.itens">{{ i.quantidade }}× {{ i.nomeProduto }} ({{ i.sku }}) — R$ {{ i.subtotal | number:'1.2-2' }}</li>
                    </ul>
                    <div class="resumo">
                      Subtotal R$ {{ p.subtotal | number:'1.2-2' }} · Frete R$ {{ p.frete | number:'1.2-2' }}
                      <span *ngIf="p.desconto"> · Desconto R$ {{ p.desconto | number:'1.2-2' }} ({{ p.cupomCodigo }})</span>
                    </div>
                    <div class="form-row">
                      <div class="form-group">
                        <label>Atualizar status</label>
                        <select [(ngModel)]="novoStatus[p.id]" name="st{{p.id}}">
                          <option *ngFor="let s of statuses" [ngValue]="s">{{ s }}</option>
                        </select>
                      </div>
                      <div class="form-group">
                        <label>Rastreio (opcional)</label>
                        <input [(ngModel)]="rastreio[p.id]" name="rt{{p.id}}" [placeholder]="p.rastreio || ''">
                      </div>
                    </div>
                    <button class="btn-salvar" (click)="atualizar(p)">Salvar status</button>
                  </div>
                </td>
              </tr>
            </ng-container>
            <tr *ngIf="pedidos.length === 0"><td colspan="6" class="empty">Nenhum pedido encontrado.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .detalhe { padding: 1rem; background: #fafafa; border-radius: 8px; display: flex; flex-direction: column; gap: .75rem; }
    .detalhe ul { margin: 0; padding-left: 1.25rem; }
    .resumo { color: #555; font-size: .9rem; }
  `]
})
export class AdminPedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  statuses: StatusPedido[] = ['AGUARDANDO_PAGAMENTO', 'PAGO', 'ENVIADO', 'ENTREGUE', 'CANCELADO'];
  filtroStatus?: StatusPedido;
  expandido: number | null = null;
  novoStatus: Record<number, StatusPedido> = {};
  rastreio: Record<number, string> = {};
  erro: string | null = null;

  constructor(private loja: LojaApiService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.loja.listarPedidos(this.filtroStatus).subscribe({
      next: r => {
        this.pedidos = r.content;
        r.content.forEach(p => this.novoStatus[p.id] = p.status);
      },
      error: e => this.erro = this.msg(e)
    });
  }

  atualizar(p: Pedido): void {
    this.loja.atualizarStatusPedido(p.id, this.novoStatus[p.id], this.rastreio[p.id] || undefined).subscribe({
      next: () => this.carregar(),
      error: e => this.erro = this.msg(e)
    });
  }

  badge(s: StatusPedido): string {
    return s === 'PAGO' || s === 'ENTREGUE' ? 'ok' : s === 'CANCELADO' ? 'off' : s === 'ENVIADO' ? 'info' : 'warn';
  }

  private msg(e: any): string { return e?.error?.message || 'Ocorreu um erro ao processar a requisição.'; }
}
