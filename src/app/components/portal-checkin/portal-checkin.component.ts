import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademiaService, Turma } from '../../services/academia.service';
import { FrequenciaService, CheckIn } from '../../services/frequencia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-portal-checkin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page portal-checkin">
      <div class="page-header"><div><h1>Check-in</h1><p>Registre sua presença na aula de hoje</p></div></div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <div class="form-section">
        <h2>Fazer check-in</h2>
        <div class="form-group full">
          <label>Turma</label>
          <select [(ngModel)]="turmaId" name="turmaId">
            <option [ngValue]="undefined" disabled>Selecione a turma…</option>
            <option *ngFor="let t of turmas" [ngValue]="t.id">{{ t.nome }} — {{ t.diaSemana }} {{ t.horaInicio }}</option>
          </select>
        </div>
        <div class="form-actions">
          <button class="btn-salvar" (click)="checkIn()" [disabled]="!turmaId || enviando">Registrar presença</button>
        </div>
        <p style="color:#888;font-size:.85rem;margin-top:.5rem;">O check-in só é aceito no dia e dentro da janela de horário da turma em que você está matriculado.</p>
      </div>

      <div class="tabela-section" *ngIf="historico.length">
        <table>
          <thead><tr><th>Data</th><th>Turma</th><th>Origem</th></tr></thead>
          <tbody>
            <tr *ngFor="let c of historico">
              <td>{{ c.data }}</td>
              <td>{{ c.turma.nome }}</td>
              <td><span class="badge info">{{ c.origem }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `.alert.ok{background:#dcfce7;border:1px solid #bbf7d0;color:#166534;padding:.75rem 1rem;border-radius:8px;margin-bottom:1rem;}`,
    `
    /* Portal Check‑in specific responsive styles */
    .portal-checkin .form-section { display: flex; flex-direction: column; gap: 1rem; }
    .portal-checkin .form-group.full { width: 100%; }
    .portal-checkin select, .portal-checkin button { width: 100%; }
    .portal-checkin .form-actions { justify-content: stretch; }
    .portal-checkin .tabela-section { overflow-x: auto; }
    .portal-checkin table { min-width: 600px; }
    @media (max-width: 576px) {
      .portal-checkin .form-section { gap: 0.5rem; }
      .portal-checkin table, .portal-checkin thead, .portal-checkin tbody, .portal-checkin tr, .portal-checkin td, .portal-checkin th { display: block; width: 100%; }
      .portal-checkin thead { display: none; }
      .portal-checkin tr { margin-bottom: 1rem; border: 1px solid #eee; padding: 0.5rem; }
      .portal-checkin td { text-align: right; padding-left: 50%; position: relative; }
      .portal-checkin td::before {
        content: attr(data-label);
        position: absolute;
        left: 0;
        width: 45%;
        padding-left: 0.5rem;
        font-weight: 600;
        text-align: left;
        color: #666;
      }
    }
    `]

})
export class PortalCheckinComponent implements OnInit {
  turmas: Turma[] = [];
  historico: CheckIn[] = [];
  alunoId?: number;
  turmaId?: number;
  enviando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private academia: AcademiaService, private freq: FrequenciaService) {}

  ngOnInit(): void {
    this.academia.grade().subscribe({ next: t => this.turmas = t, error: () => {} });
    this.academia.meuPerfil().subscribe({
      next: a => { this.alunoId = a.id; this.carregarHistorico(); },
      error: e => this.erro = e?.error?.message || 'Não foi possível identificar seu cadastro de aluno.'
    });
  }

  carregarHistorico(): void {
    if (!this.alunoId) return;
    this.freq.historico(this.alunoId).subscribe({ next: h => this.historico = h.slice(0, 10), error: () => {} });
  }

  checkIn(): void {
    if (!this.alunoId || !this.turmaId) return;
    this.enviando = true;
    this.erro = null; this.aviso = null;
    this.freq.checkIn(this.alunoId, this.turmaId).subscribe({
      next: () => { this.enviando = false; this.aviso = 'Presença registrada. Bom treino! 🥋'; this.carregarHistorico(); },
      error: e => { this.enviando = false; this.erro = e?.error?.message || 'Não foi possível registrar o check-in.'; }
    });
  }
}
