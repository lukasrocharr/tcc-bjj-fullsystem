import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AcademiaService, Turma, Modalidade, Professor, DiaSemana, NivelTurma
} from '../../services/academia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-turmas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Gestão de Turmas</h1>
          <p>Turmas semanais da academia (modalidade, professor, horário, capacidade)</p>
        </div>
        <button class="btn-novo" (click)="abrirNova()">+ Nova Turma</button>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="form-section" *ngIf="mostrarForm">
        <h2>{{ editandoId ? 'Editar' : 'Nova' }} Turma</h2>
        <form (ngSubmit)="salvar()" #f="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Nome</label>
              <input [(ngModel)]="form.nome" name="nome" required placeholder="Ex: Iniciante - Manhã">
            </div>
            <div class="form-group">
              <label>Modalidade</label>
              <select [(ngModel)]="form.modalidadeId" name="modalidadeId" required>
                <option [ngValue]="undefined" disabled>Selecione...</option>
                <option *ngFor="let m of modalidades" [ngValue]="m.id">{{ m.nome }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Professor (opcional)</label>
              <select [(ngModel)]="form.professorId" name="professorId">
                <option [ngValue]="undefined">— sem professor —</option>
                <option *ngFor="let p of professores" [ngValue]="p.id">{{ p.nome }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Dia da semana</label>
              <select [(ngModel)]="form.diaSemana" name="diaSemana" required>
                <option *ngFor="let d of dias" [ngValue]="d">{{ d }}</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Hora início</label>
              <input type="time" [(ngModel)]="form.horaInicio" name="horaInicio" required>
            </div>
            <div class="form-group">
              <label>Hora fim</label>
              <input type="time" [(ngModel)]="form.horaFim" name="horaFim" required>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Capacidade (0 = ilimitada)</label>
              <input type="number" [(ngModel)]="form.capacidade" name="capacidade" min="0" required>
            </div>
            <div class="form-group">
              <label>Nível</label>
              <select [(ngModel)]="form.nivel" name="nivel" required>
                <option *ngFor="let n of niveis" [ngValue]="n">{{ n }}</option>
              </select>
            </div>
          </div>

          <div class="form-group full checks">
            <label><input type="checkbox" [(ngModel)]="form.ativo" name="ativo"> Ativa</label>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-salvar" [disabled]="!f.valid || salvando">
              {{ editandoId ? 'Atualizar' : 'Criar' }}
            </button>
            <button type="button" class="btn-cancelar" (click)="cancelar()">Cancelar</button>
          </div>
        </form>
      </div>

      <div class="tabela-section">
        <table>
          <thead>
            <tr>
              <th>Nome</th><th>Modalidade</th><th>Professor</th><th>Dia</th>
              <th>Horário</th><th>Ocupação</th><th>Nível</th><th>Status</th><th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let t of turmas">
              <td><strong>{{ t.nome }}</strong></td>
              <td>{{ t.modalidade?.nome }}</td>
              <td>{{ t.professor?.nome || '—' }}</td>
              <td>{{ t.diaSemana }}</td>
              <td>{{ t.horaInicio }} – {{ t.horaFim }}</td>
              <td>
                <span class="badge" [class.off]="lotada(t)" [class.ok]="!lotada(t)">
                  {{ t.vagasOcupadas }}/{{ t.capacidade === 0 ? '∞' : t.capacidade }}
                </span>
              </td>
              <td>{{ t.nivel }}</td>
              <td><span class="badge" [class.ok]="t.ativo" [class.off]="!t.ativo">{{ t.ativo ? 'ativa' : 'inativa' }}</span></td>
              <td class="acoes">
                <button class="icon-btn" (click)="editar(t)" title="Editar">✏️</button>
                <button class="icon-btn del" (click)="confirmarDelecao(t.id)" title="Excluir">🗑️</button>
              </td>
            </tr>
            <tr *ngIf="turmas.length === 0"><td colspan="9" class="empty">Nenhuma turma cadastrada.</td></tr>
          </tbody>
        </table>
      </div>

      <div class="modal-overlay" *ngIf="deletandoId" (click)="deletandoId = null">
        <div class="modal" (click)="$event.stopPropagation()">
          <h3>Confirmar exclusão</h3>
          <p>Deseja realmente excluir esta turma?</p>
          <div class="modal-actions">
            <button class="btn-confirmar" (click)="deletar()">Excluir</button>
            <button class="btn-cancelar" (click)="deletandoId = null">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES]
})
export class AdminTurmasComponent implements OnInit {
  turmas: Turma[] = [];
  modalidades: Modalidade[] = [];
  professores: Professor[] = [];

  dias: DiaSemana[] = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'];
  niveis: NivelTurma[] = ['INICIANTE', 'INTERMEDIARIO', 'AVANCADO', 'KIDS', 'TODOS'];

  mostrarForm = false;
  editandoId: number | null = null;
  deletandoId: number | null = null;
  salvando = false;
  erro: string | null = null;

  form: any = this.formVazio();

  constructor(private api: AcademiaService) {}

  ngOnInit(): void {
    this.carregar();
    this.api.listarModalidades().subscribe(r => this.modalidades = r.content);
    this.api.listarProfessores().subscribe(r => this.professores = r.content);
  }

  formVazio() {
    return {
      nome: '', modalidadeId: undefined, professorId: undefined,
      diaSemana: 'SEGUNDA', horaInicio: '19:00', horaFim: '20:00',
      capacidade: 20, nivel: 'INICIANTE', ativo: true
    };
  }

  lotada(t: Turma): boolean {
    return t.capacidade > 0 && t.vagasOcupadas >= t.capacidade;
  }

  carregar(): void {
    this.api.listarTurmas().subscribe({
      next: r => this.turmas = r.content,
      error: e => this.erro = this.msg(e)
    });
  }

  abrirNova(): void {
    this.form = this.formVazio();
    this.editandoId = null;
    this.mostrarForm = true;
    this.erro = null;
  }

  editar(t: Turma): void {
    this.form = {
      nome: t.nome,
      modalidadeId: t.modalidade?.id,
      professorId: t.professor?.id,
      diaSemana: t.diaSemana,
      horaInicio: t.horaInicio?.substring(0, 5),
      horaFim: t.horaFim?.substring(0, 5),
      capacidade: t.capacidade,
      nivel: t.nivel,
      ativo: t.ativo
    };
    this.editandoId = t.id;
    this.mostrarForm = true;
    this.erro = null;
  }

  salvar(): void {
    this.salvando = true;
    this.erro = null;
    const body = { ...this.form };
    const req = this.editandoId
      ? this.api.atualizarTurma(this.editandoId, body)
      : this.api.criarTurma(body);
    req.subscribe({
      next: () => { this.salvando = false; this.cancelar(); this.carregar(); },
      error: e => { this.salvando = false; this.erro = this.msg(e); }
    });
  }

  cancelar(): void {
    this.mostrarForm = false;
    this.editandoId = null;
    this.form = this.formVazio();
  }

  confirmarDelecao(id: number): void { this.deletandoId = id; }

  deletar(): void {
    if (!this.deletandoId) return;
    this.api.removerTurma(this.deletandoId).subscribe({
      next: () => { this.deletandoId = null; this.carregar(); },
      error: e => { this.erro = this.msg(e); this.deletandoId = null; }
    });
  }

  private msg(e: any): string {
    return e?.error?.message || 'Ocorreu um erro ao processar a requisição.';
  }
}
