import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AcademiaService, Matricula, Aluno, Plano, Turma, StatusMatricula
} from '../../services/academia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-matriculas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Matrículas</h1>
          <p>Vincule alunos a planos e turmas; gerencie o status</p>
        </div>
        <div class="toolbar">
          <select class="search-input" [(ngModel)]="filtroStatus" (change)="carregar()">
            <option [ngValue]="undefined">Todos os status</option>
            <option *ngFor="let s of statuses" [ngValue]="s">{{ s }}</option>
          </select>
          <button class="btn-novo" (click)="abrirNova()">+ Nova Matrícula</button>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="form-section" *ngIf="mostrarForm">
        <h2>Nova Matrícula</h2>
        <form (ngSubmit)="salvar()" #f="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Aluno</label>
              <select [(ngModel)]="form.alunoId" name="alunoId" required>
                <option [ngValue]="undefined" disabled>Selecione...</option>
                <option *ngFor="let a of alunos" [ngValue]="a.id">{{ a.nome }} ({{ a.email }})</option>
              </select>
            </div>
            <div class="form-group">
              <label>Plano</label>
              <select [(ngModel)]="form.planoId" name="planoId" required>
                <option [ngValue]="undefined" disabled>Selecione...</option>
                <option *ngFor="let p of planos" [ngValue]="p.id">{{ p.nome }} — R$ {{ p.valor }}</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Data de início</label>
              <input type="date" [(ngModel)]="form.dataInicio" name="dataInicio" required>
            </div>
            <div class="form-group">
              <label>Observação</label>
              <input [(ngModel)]="form.observacao" name="observacao" placeholder="(opcional)">
            </div>
          </div>
          <div class="form-group full">
            <label>Turmas (selecione ao menos uma)</label>
            <div class="checks">
              <label *ngFor="let t of turmas">
                <input type="checkbox" [checked]="form.turmaIds.has(t.id)" (change)="toggleTurma(t.id)">
                {{ t.nome }} · {{ t.diaSemana }} {{ t.horaInicio }}
                <span class="badge" [class.off]="lotada(t)" [class.ok]="!lotada(t)">
                  {{ t.vagasOcupadas }}/{{ t.capacidade === 0 ? '∞' : t.capacidade }}
                </span>
              </label>
            </div>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn-salvar" [disabled]="!f.valid || form.turmaIds.size === 0 || salvando">Criar</button>
            <button type="button" class="btn-cancelar" (click)="cancelar()">Cancelar</button>
          </div>
        </form>
      </div>

      <div class="tabela-section">
        <table>
          <thead>
            <tr><th>Aluno</th><th>Plano</th><th>Turmas</th><th>Início</th><th>Status</th><th>Ações</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let m of matriculas">
              <td><strong>{{ m.aluno?.nome }}</strong></td>
              <td>{{ m.plano?.nome }}</td>
              <td>{{ nomesTurmas(m) }}</td>
              <td>{{ m.dataInicio }}</td>
              <td>
                <span class="badge"
                      [class.ok]="m.status === 'ATIVA'"
                      [class.warn]="m.status === 'SUSPENSA'"
                      [class.off]="m.status === 'CANCELADA'">{{ m.status }}</span>
              </td>
              <td class="acoes">
                <button class="icon-btn" *ngIf="m.status !== 'ATIVA'" title="Reativar"
                        (click)="mudarStatus(m, 'ATIVA')">▶️</button>
                <button class="icon-btn" *ngIf="m.status === 'ATIVA'" title="Suspender"
                        (click)="mudarStatus(m, 'SUSPENSA')">⏸️</button>
                <button class="icon-btn del" *ngIf="m.status !== 'CANCELADA'" title="Cancelar"
                        (click)="mudarStatus(m, 'CANCELADA')">⛔</button>
              </td>
            </tr>
            <tr *ngIf="matriculas.length === 0"><td colspan="6" class="empty">Nenhuma matrícula encontrada.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES]
})
export class AdminMatriculasComponent implements OnInit {
  matriculas: Matricula[] = [];
  alunos: Aluno[] = [];
  planos: Plano[] = [];
  turmas: Turma[] = [];

  statuses: StatusMatricula[] = ['ATIVA', 'SUSPENSA', 'CANCELADA'];
  filtroStatus?: StatusMatricula;

  mostrarForm = false;
  salvando = false;
  erro: string | null = null;
  form: any = this.formVazio();

  constructor(private api: AcademiaService) {}

  ngOnInit(): void {
    this.carregar();
    this.api.listarAlunos().subscribe(r => this.alunos = r.content);
    this.api.listarPlanos().subscribe(r => this.planos = r.content);
    this.api.listarTurmas(undefined, true).subscribe(r => this.turmas = r.content);
  }

  formVazio() {
    return { alunoId: undefined, planoId: undefined, dataInicio: new Date().toISOString().substring(0, 10), observacao: '', turmaIds: new Set<number>() };
  }

  lotada(t: Turma): boolean { return t.capacidade > 0 && t.vagasOcupadas >= t.capacidade; }

  nomesTurmas(m: Matricula): string {
    return (m.turmas || []).map(t => t.nome).join(', ') || '—';
  }

  toggleTurma(id: number): void {
    if (this.form.turmaIds.has(id)) this.form.turmaIds.delete(id);
    else this.form.turmaIds.add(id);
  }

  carregar(): void {
    this.api.listarMatriculas(undefined, this.filtroStatus).subscribe({
      next: r => this.matriculas = r.content,
      error: e => this.erro = this.msg(e)
    });
  }

  abrirNova(): void { this.form = this.formVazio(); this.mostrarForm = true; this.erro = null; }
  cancelar(): void { this.mostrarForm = false; this.form = this.formVazio(); }

  salvar(): void {
    this.salvando = true;
    this.erro = null;
    const body = {
      alunoId: this.form.alunoId,
      planoId: this.form.planoId,
      dataInicio: this.form.dataInicio,
      observacao: this.form.observacao || null,
      turmaIds: Array.from(this.form.turmaIds)
    };
    this.api.criarMatricula(body).subscribe({
      next: () => { this.salvando = false; this.cancelar(); this.recarregarTudo(); },
      error: e => { this.salvando = false; this.erro = this.msg(e); }
    });
  }

  mudarStatus(m: Matricula, status: StatusMatricula): void {
    this.api.alterarStatusMatricula(m.id, status).subscribe({
      next: () => this.recarregarTudo(),
      error: e => this.erro = this.msg(e)
    });
  }

  private recarregarTudo(): void {
    this.carregar();
    // Recarrega turmas para refletir ocupação atualizada.
    this.api.listarTurmas(undefined, true).subscribe(r => this.turmas = r.content);
  }

  private msg(e: any): string {
    return e?.error?.message || 'Ocorreu um erro ao processar a requisição.';
  }
}
