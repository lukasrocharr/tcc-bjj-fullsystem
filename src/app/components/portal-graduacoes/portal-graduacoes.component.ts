import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademiaService } from '../../services/academia.service';
import { GraduacaoService, Graduacao, FaixaAtual } from '../../services/graduacao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-portal-graduacoes',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header"><div><h1>Minhas Graduações</h1><p>Sua faixa atual e o histórico de promoções</p></div></div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="faixa-atual" *ngIf="fx as f">
        <span>Faixa atual</span>
        <strong>{{ f.faixa || '—' }} <em *ngIf="f.graus">· {{ f.graus }} grau(s)</em></strong>
        <small *ngIf="f.desde">Desde {{ f.desde }} ({{ f.diasNaFaixa }} dias)</small>
      </div>

      <div class="tabela-section">
        <table>
          <thead><tr><th>Data</th><th>Faixa</th><th>Graus</th><th>Professor</th><th>Obs.</th></tr></thead>
          <tbody>
            <tr *ngFor="let g of historico">
              <td>{{ g.data }}</td>
              <td><strong>{{ g.faixa.nome }}</strong></td>
              <td>{{ g.graus }}</td>
              <td>{{ g.professor?.nome || '—' }}</td>
              <td>{{ g.observacao || '—' }}</td>
            </tr>
            <tr *ngIf="historico.length === 0"><td colspan="5" class="empty">Nenhuma graduação registrada ainda.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .faixa-atual { background: linear-gradient(135deg,#0a0a0a,#1a1a1a); color: #fff; border-radius: 12px; padding: 1.5rem 2rem; margin-bottom: 2rem; display: flex; flex-direction: column; gap: .35rem; }
    .faixa-atual span { color: #c9a84c; text-transform: uppercase; font-size: .75rem; font-weight: 700; letter-spacing: .5px; }
    .faixa-atual strong { font-size: 1.8rem; }
    .faixa-atual em { color: #c9a84c; font-style: normal; font-size: 1.1rem; }
    .faixa-atual small { color: #aaa; }
  `]
})
export class PortalGraduacoesComponent implements OnInit {
  historico: Graduacao[] = [];
  fx: FaixaAtual | null = null;
  erro: string | null = null;

  constructor(private academia: AcademiaService, private grad: GraduacaoService) {}

  ngOnInit(): void {
    this.academia.meuPerfil().subscribe({
      next: a => {
        this.grad.faixaAtual(a.id).subscribe({ next: f => this.fx = f, error: () => {} });
        this.grad.historico(a.id).subscribe({ next: h => this.historico = h, error: () => {} });
      },
      error: e => this.erro = e?.error?.message || 'Não foi possível identificar seu cadastro de aluno.'
    });
  }
}
