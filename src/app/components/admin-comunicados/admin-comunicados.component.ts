import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificacaoService, PapelAlvo } from '../../services/notificacao.service';
import { ADMIN_CRUD_STYLES } from '../admin-shared.styles';

@Component({
  selector: 'app-admin-comunicados',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page admin-comunicados-container">
      <div class="page-header">
        <div>
          <h1>Comunicados</h1>
          <p>Envie um comunicado em massa (in-app e, opcionalmente, por e-mail)</p>
        </div>
      </div>

      <div class="alert error" *ngIf="erro">⚠️ {{ erro }}</div>
      <div class="alert ok" *ngIf="aviso">✅ {{ aviso }}</div>

      <div class="form-section">
        <h2>Novo comunicado</h2>
        <form #f="ngForm" (ngSubmit)="enviar()">
          <div class="form-group full">
            <label>Título</label>
            <input [(ngModel)]="form.titulo" name="titulo" required maxlength="140" placeholder="Ex: Feriado — sem treino">
          </div>
          <div class="form-group full">
            <label>Mensagem</label>
            <textarea [(ngModel)]="form.mensagem" name="mensagem" required maxlength="2000" rows="5"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Destinatários</label>
              <select [(ngModel)]="form.papel" name="papel">
                <option [ngValue]="null">Todos</option>
                <option *ngFor="let p of papeis" [ngValue]="p">{{ p }}</option>
              </select>
            </div>
            <div class="form-group full checks" style="align-self:end">
              <label><input type="checkbox" [(ngModel)]="form.enviarEmail" name="enviarEmail"> Enviar também por e-mail</label>
            </div>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn-salvar" [disabled]="!f.valid || enviando">Enviar comunicado</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [ADMIN_CRUD_STYLES + `
    @media (max-width: 576px) { .form-row { grid-template-columns: 1fr; } }
  `]
})
export class AdminComunicadosComponent {
  papeis: PapelAlvo[] = ['ALUNO', 'PROFESSOR', 'ADMIN'];
  form: { titulo: string; mensagem: string; papel: PapelAlvo | null; enviarEmail: boolean } = {
    titulo: '', mensagem: '', papel: null, enviarEmail: false
  };
  enviando = false;
  erro: string | null = null;
  aviso: string | null = null;

  constructor(private notif: NotificacaoService) {}

  enviar(): void {
    this.enviando = true;
    this.erro = null;
    this.notif.enviarComunicado(this.form).subscribe({
      next: r => {
        this.enviando = false;
        this.aviso = `Comunicado enviado para ${r.destinatarios} destinatário(s).`;
        this.form = { titulo: '', mensagem: '', papel: null, enviarEmail: false };
      },
      error: e => { this.enviando = false; this.erro = e?.error?.message || 'Erro ao enviar comunicado.'; }
    });
  }
}
