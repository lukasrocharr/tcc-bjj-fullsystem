import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademiaService, Aluno, Professor } from '../../services/academia.service';
import { GraduacaoService, Faixa, Graduacao } from '../../services/graduacao.service';
import { AlunoRef } from '../../services/frequencia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-graduacao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Graduações</h1>
          <p>Promoções de faixa, histórico, elegíveis e certificados</p>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <!-- Registrar promocao -->
      <div class="form-section">
        <h2>Registrar promoção</h2>
        <form #f="ngForm" (ngSubmit)="registrar()">
          <div class="form-row">
            <div class="form-group">
              <label>Aluno</label>
              <select [(ngModel)]="form.alunoId" name="alunoId" required (change)="carregarHistorico()">
                <option [ngValue]="undefined" disabled>Selecione…</option>
                <option *ngFor="let a of alunos" [ngValue]="a.id">{{ a.nome }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Faixa</label>
              <select [(ngModel)]="form.faixaId" name="faixaId" required>
                <option [ngValue]="undefined" disabled>Selecione…</option>
                <option *ngFor="let fx of faixas" [ngValue]="fx.id">{{ fx.nome }} ({{ fx.categoria }})</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Graus</label>
              <input type="number" min="0" [(ngModel)]="form.graus" name="graus">
            </div>
            <div class="form-group">
              <label>Data (opcional)</label>
              <input type="date" [(ngModel)]="form.data" name="data">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Professor (opcional)</label>
              <select [(ngModel)]="form.professorId" name="professorId">
                <option [ngValue]="undefined">— nenhum —</option>
                <option *ngFor="let p of professores" [ngValue]="p.id">{{ p.nome }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Observação</label>
              <input [(ngModel)]="form.observacao" name="observacao" maxlength="500">
            </div>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn-salvar" [disabled]="!f.valid || salvando">Promover</button>
          </div>
        </form>
      </div>

      <div class="duas-colunas">
        <!-- Historico do aluno selecionado -->
        <div class="tabela-section">
          <table>
            <thead>
              <tr><th colspan="4">Histórico {{ nomeAlunoSelecionado ? '— ' + nomeAlunoSelecionado : '' }}</th></tr>
              <tr><th>Data</th><th>Faixa</th><th>Graus</th><th>Cert.</th></tr>
            </thead>
            <tbody>
              <tr *ngFor="let g of historico">
                <td>{{ g.data }}</td>
                <td><strong>{{ g.faixa.nome }}</strong></td>
                <td>{{ g.graus }}</td>
                <td><button class="icon-btn" title="Certificado" (click)="certificado(g)">🎓</button></td>
              </tr>
              <tr *ngIf="historico.length === 0"><td colspan="4" class="empty">Selecione um aluno para ver o histórico.</td></tr>
            </tbody>
          </table>
        </div>

        <!-- Elegiveis -->
        <div class="tabela-section">
          <table>
            <thead>
              <tr><th colspan="2">Elegíveis a nova graduação</th></tr>
              <tr><th>Aluno</th><th>E-mail</th></tr>
            </thead>
            <tbody>
              <tr *ngFor="let a of elegiveis">
                <td><strong>{{ a.nome }}</strong></td>
                <td>{{ a.email }}</td>
              </tr>
              <tr *ngIf="elegiveis.length === 0"><td colspan="2" class="empty">Nenhum aluno elegível.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    .duas-colunas { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    @media (max-width: 900px) { .duas-colunas { grid-template-columns: 1fr; } }
  `]
})
export class AdminGraduacaoComponent implements OnInit {
  alunos: Aluno[] = [];
  professores: Professor[] = [];
  faixas: Faixa[] = [];
  elegiveis: AlunoRef[] = [];
  historico: Graduacao[] = [];

  form: any = { alunoId: undefined, faixaId: undefined, graus: 0, data: undefined, professorId: undefined, observacao: '' };
  salvando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private academia: AcademiaService, private grad: GraduacaoService) {}

  ngOnInit(): void {
    this.academia.listarAlunos().subscribe({ next: r => this.alunos = r.content });
    this.academia.listarProfessores().subscribe({ next: r => this.professores = r.content });
    this.grad.faixas().subscribe({ next: f => this.faixas = f });
    this.grad.elegiveis().subscribe({ next: e => this.elegiveis = e, error: e => this.erro = this.msg(e) });
  }

  get nomeAlunoSelecionado(): string {
    return this.alunos.find(a => a.id === this.form.alunoId)?.nome || '';
  }

  carregarHistorico(): void {
    if (!this.form.alunoId) { this.historico = []; return; }
    this.grad.historico(this.form.alunoId).subscribe({ next: h => this.historico = h });
  }

  registrar(): void {
    this.salvando = true;
    this.erro = null;
    this.grad.registrar({
      alunoId: this.form.alunoId,
      faixaId: this.form.faixaId,
      graus: this.form.graus ?? 0,
      data: this.form.data || undefined,
      professorId: this.form.professorId || undefined,
      observacao: this.form.observacao || undefined
    }).subscribe({
      next: () => {
        this.salvando = false;
        this.aviso = 'Promoção registrada e faixa atual atualizada.';
        this.carregarHistorico();
        this.grad.elegiveis().subscribe({ next: e => this.elegiveis = e });
      },
      error: e => { this.salvando = false; this.erro = this.msg(e); }
    });
  }

  certificado(g: Graduacao): void {
    this.grad.certificadoBlob(g.id).subscribe({
      next: blob => window.open(URL.createObjectURL(blob), '_blank'),
      error: e => this.erro = this.msg(e)
    });
  }

  private msg(e: any): string { return e?.error?.message || 'Ocorreu um erro ao processar a requisição.'; }
}
