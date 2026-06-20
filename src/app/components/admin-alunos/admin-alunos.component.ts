import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademiaService, Aluno } from '../../services/academia.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-alunos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page admin-alunos-container">
      <div class="page-header">
        <div>
          <h1>Gestão de Alunos</h1>
          <p>Cadastro de alunos (dados pessoais, contato e saúde)</p>
        </div>
        <div class="toolbar">
          <input class="search-input" [(ngModel)]="busca" (keyup.enter)="carregar()"
                 placeholder="Buscar por nome...">
          <button class="btn-novo" (click)="abrirNovo()">+ Novo Aluno</button>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>

      <div class="form-section" *ngIf="mostrarForm">
        <h2>{{ editandoId ? 'Editar' : 'Novo' }} Aluno</h2>
        <form (ngSubmit)="salvar()" #f="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Nome</label>
              <input [(ngModel)]="form.nome" name="nome" required placeholder="Nome completo">
            </div>
            <div class="form-group">
              <label>E-mail</label>
              <input type="email" [(ngModel)]="form.email" name="email" required placeholder="aluno@email.com">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Telefone</label>
              <input [(ngModel)]="form.telefone" name="telefone" placeholder="(00) 00000-0000">
            </div>
            <div class="form-group">
              <label>Data de nascimento</label>
              <input type="date" [(ngModel)]="form.dataNascimento" name="dataNascimento">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>CPF</label>
              <input [(ngModel)]="form.cpf" name="cpf" placeholder="000.000.000-00">
            </div>
            <div class="form-group">
              <label>Faixa atual</label>
              <input [(ngModel)]="form.faixaAtual" name="faixaAtual" placeholder="Ex: Branca">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Contato de emergência</label>
              <input [(ngModel)]="form.contatoEmergencia" name="contatoEmergencia" placeholder="Nome / telefone">
            </div>
            <div class="form-group">
              <label>&nbsp;</label>
              <label class="checks"><input type="checkbox" [(ngModel)]="form.ativo" name="ativo"> Ativo</label>
            </div>
          </div>
          <div class="form-group full">
            <label>Observações de saúde</label>
            <textarea rows="2" [(ngModel)]="form.observacoesSaude" name="observacoesSaude"
                      placeholder="Lesões, restrições, etc."></textarea>
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
            <tr><th>Nome</th><th>E-mail</th><th>Telefone</th><th>Faixa</th><th>Status</th><th>Ações</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let a of alunos">
              <td><strong>{{ a.nome }}</strong></td>
              <td>{{ a.email }}</td>
              <td>{{ a.telefone || '—' }}</td>
              <td>{{ a.faixaAtual || '—' }}</td>
              <td><span class="badge" [class.ok]="a.ativo" [class.off]="!a.ativo">{{ a.ativo ? 'ativo' : 'inativo' }}</span></td>
              <td class="acoes">
                <button class="icon-btn" (click)="editar(a)" title="Editar">✏️</button>
                <button class="icon-btn del" (click)="deletandoId = a.id" title="Excluir">🗑️</button>
              </td>
            </tr>
            <tr *ngIf="alunos.length === 0"><td colspan="6" class="empty">Nenhum aluno encontrado.</td></tr>
          </tbody>
        </table>
      </div>

      <div class="modal-overlay" *ngIf="deletandoId" (click)="deletandoId = null">
        <div class="modal" (click)="$event.stopPropagation()">
          <h3>Confirmar exclusão</h3>
          <p>Deseja realmente excluir este aluno?</p>
          <div class="modal-actions">
            <button class="btn-confirmar" (click)="deletar()">Excluir</button>
            <button class="btn-cancelar" (click)="deletandoId = null">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES, `
    .admin-alunos-container .tabela-section { overflow-x:auto; }
    .admin-alunos-container table { min-width:600px; }
    @media (max-width: 768px) {
      .admin-alunos-container .tabela-section { padding:0.5rem; }
    }
    @media (max-width: 576px) {
      .admin-alunos-container .form-row { flex-direction: column; gap:0.75rem; }
      .admin-alunos-container .form-group { width:100%; }
      .admin-alunos-container .form-actions { justify-content: stretch; }
      .admin-alunos-container .btn-salvar, .admin-alunos-container .btn-cancelar { width:100%; }
    }
    `]
})
export class AdminAlunosComponent implements OnInit {
  alunos: Aluno[] = [];
  busca = '';
  mostrarForm = false;
  editandoId: number | null = null;
  deletandoId: number | null = null;
  salvando = false;
  erro: string | null = null;
  form: any = this.formVazio();

  constructor(private api: AcademiaService) {}

  ngOnInit(): void { this.carregar(); }

  formVazio() {
    return {
      nome: '', email: '', telefone: '', dataNascimento: null, cpf: '',
      contatoEmergencia: '', observacoesSaude: '', faixaAtual: '', ativo: true
    };
  }

  carregar(): void {
    this.api.listarAlunos(this.busca || undefined).subscribe({
      next: r => this.alunos = r.content,
      error: e => this.erro = this.msg(e)
    });
  }

  abrirNovo(): void { this.form = this.formVazio(); this.editandoId = null; this.mostrarForm = true; this.erro = null; }

  editar(a: Aluno): void {
    this.form = { ...this.formVazio(), ...a };
    this.editandoId = a.id;
    this.mostrarForm = true;
    this.erro = null;
  }

  salvar(): void {
    this.salvando = true;
    this.erro = null;
    const body = { ...this.form };
    const req = this.editandoId ? this.api.atualizarAluno(this.editandoId, body) : this.api.criarAluno(body);
    req.subscribe({
      next: () => { this.salvando = false; this.cancelar(); this.carregar(); },
      error: e => { this.salvando = false; this.erro = this.msg(e); }
    });
  }

  cancelar(): void { this.mostrarForm = false; this.editandoId = null; this.form = this.formVazio(); }

  deletar(): void {
    if (!this.deletandoId) return;
    this.api.removerAluno(this.deletandoId).subscribe({
      next: () => { this.deletandoId = null; this.carregar(); },
      error: e => { this.erro = this.msg(e); this.deletandoId = null; }
    });
  }

  private msg(e: any): string {
    return e?.error?.message || 'Ocorreu um erro ao processar a requisição.';
  }
}
