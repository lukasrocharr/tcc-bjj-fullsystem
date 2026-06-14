import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademiaService, Turma, Aluno } from '../../services/academia.service';
import { FrequenciaService, AlunoRef } from '../../services/frequencia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-frequencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Frequência</h1>
          <p>Chamada em lote pelo professor e alertas de baixa frequência</p>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <!-- Chamada -->
      <div class="form-section">
        <h2>Registrar chamada</h2>
        <div class="form-row">
          <div class="form-group">
            <label>Turma</label>
            <select [(ngModel)]="turmaId" name="turmaId">
              <option [ngValue]="undefined" disabled>Selecione…</option>
              <option *ngFor="let t of turmas" [ngValue]="t.id">{{ t.nome }} ({{ t.diaSemana }} {{ t.horaInicio }})</option>
            </select>
          </div>
          <div class="form-group">
            <label>Data (opcional, padrão hoje)</label>
            <input type="date" [(ngModel)]="data" name="data">
          </div>
        </div>

        <div class="form-group full">
          <label>Alunos presentes</label>
          <div class="checks alunos-grid">
            <label *ngFor="let a of alunos">
              <input type="checkbox" [checked]="presentes.has(a.id)" (change)="toggle(a.id)"> {{ a.nome }}
            </label>
            <span class="empty" *ngIf="alunos.length === 0">Nenhum aluno cadastrado.</span>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-salvar" (click)="registrarChamada()" [disabled]="!turmaId || presentes.size === 0 || salvando">
            Registrar ({{ presentes.size }})
          </button>
        </div>
      </div>

      <!-- Alertas -->
      <div class="tabela-section">
        <table>
          <thead>
            <tr><th colspan="3">⚠️ Alunos com baixa frequência</th></tr>
            <tr><th>Aluno</th><th>E-mail</th><th></th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let a of alertas">
              <td><strong>{{ a.nome }}</strong></td>
              <td>{{ a.email }}</td>
              <td><span class="badge off">baixa frequência</span></td>
            </tr>
            <tr *ngIf="alertas.length === 0"><td colspan="3" class="empty">Nenhum alerta no momento. 🎉</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .alunos-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: .5rem; max-height: 320px; overflow-y: auto; padding: .5rem; border: 1px solid #eee; border-radius: 8px; }
  `]
})
export class AdminFrequenciaComponent implements OnInit {
  turmas: Turma[] = [];
  alunos: Aluno[] = [];
  alertas: AlunoRef[] = [];
  presentes = new Set<number>();

  turmaId?: number;
  data?: string;
  salvando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private academia: AcademiaService, private freq: FrequenciaService) {}

  ngOnInit(): void {
    this.academia.listarTurmas().subscribe({ next: r => this.turmas = r.content });
    this.academia.listarAlunos().subscribe({ next: r => this.alunos = r.content });
    this.carregarAlertas();
  }

  carregarAlertas(): void {
    this.freq.alertasBaixa().subscribe({
      next: a => this.alertas = a,
      error: e => this.erro = this.msg(e)
    });
  }

  toggle(id: number): void {
    if (this.presentes.has(id)) this.presentes.delete(id);
    else this.presentes.add(id);
  }

  registrarChamada(): void {
    if (!this.turmaId) return;
    this.salvando = true;
    this.erro = null;
    this.freq.chamada(this.turmaId, Array.from(this.presentes), this.data || undefined).subscribe({
      next: r => {
        this.salvando = false;
        this.aviso = `${r.length} presença(s) registrada(s).`;
        this.presentes.clear();
        this.carregarAlertas();
      },
      error: e => { this.salvando = false; this.erro = this.msg(e); }
    });
  }

  private msg(e: any): string { return e?.error?.message || 'Ocorreu um erro ao processar a requisição.'; }
}
