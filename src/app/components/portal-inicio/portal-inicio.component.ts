import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademiaService, Aluno } from '../../services/academia.service';
import { FrequenciaService, Frequencia } from '../../services/frequencia.service';
import { GraduacaoService, FaixaAtual } from '../../services/graduacao.service';

@Component({
  selector: 'app-portal-inicio',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

    <ng-container *ngIf="aluno as a">
      <h1 class="ola">Olá, {{ a.nome }}! 🥋</h1>

      <div class="cards">
        <div class="card faixa">
          <span class="lbl">Faixa atual</span>
          <strong>{{ fx?.faixa || a.faixaAtual || '—' }}</strong>
          <small *ngIf="fx?.graus">{{ fx?.graus }} grau(s)</small>
          <small *ngIf="fx?.diasNaFaixa">há {{ fx?.diasNaFaixa }} dias</small>
        </div>
        <div class="card">
          <span class="lbl">Check-ins no mês</span>
          <strong>{{ freq?.checkInsNoMes ?? 0 }}</strong>
          <small>{{ freq?.totalCheckIns ?? 0 }} no total</small>
        </div>
        <div class="card">
          <span class="lbl">Sequência</span>
          <strong>{{ freq?.streakDias ?? 0 }} 🔥</strong>
          <small>dias seguidos</small>
        </div>
        <div class="card">
          <span class="lbl">Último treino</span>
          <strong>{{ freq?.ultimoCheckIn || '—' }}</strong>
          <small>{{ freq?.diasDistintos ?? 0 }} dias distintos</small>
        </div>
      </div>
    </ng-container>
  `,
  styles: [`
    .ola { font-size: 1.8rem; color: #0a0a0a; margin: 0 0 1.5rem; }
    .alert.error { background: #fee2e2; border: 1px solid #fecaca; color: #dc2626; padding: .75rem 1rem; border-radius: 8px; margin-bottom: 1rem; }
    .cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px,1fr)); gap: 1.25rem; }
    .card { background: #fff; border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); border-left: 4px solid #c9a84c; display: flex; flex-direction: column; gap: .35rem; }
    .card.faixa { border-left-color: #8b5cf6; }
    .lbl { font-size: .75rem; color: #666; text-transform: uppercase; font-weight: 600; }
    .card strong { font-size: 1.7rem; color: #0a0a0a; }
    .card small { color: #999; font-size: .8rem; }
  `]
})
export class PortalInicioComponent implements OnInit {
  aluno: Aluno | null = null;
  freq: Frequencia | null = null;
  fx: FaixaAtual | null = null;
  erro: string | null = null;

  constructor(private academia: AcademiaService, private freqSvc: FrequenciaService, private grad: GraduacaoService) {}

  ngOnInit(): void {
    this.academia.meuPerfil().subscribe({
      next: a => {
        this.aluno = a;
        this.freqSvc.indicadores(a.id).subscribe({ next: f => this.freq = f, error: () => {} });
        this.grad.faixaAtual(a.id).subscribe({ next: f => this.fx = f, error: () => {} });
      },
      error: e => this.erro = e?.error?.message || 'Não foi possível carregar seu perfil. Verifique se há um cadastro de aluno vinculado à sua conta.'
    });
  }
}
