import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Aula } from '../../services/admin.service';

@Component({
  selector: 'app-admin-aulas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="aulas-container admin-aulas-container">
      <div class="aulas-header">
        <div>
          <h1>Gestão de Aulas</h1>
          <p>Cadastre e gerencie as aulas da academia</p>
        </div>
        <button class="btn-novo" (click)="toggleFormNovaAula()">
          + Nova Aula
        </button>
      </div>

      <!-- Formulário Nova Aula -->
      <div class="form-section" *ngIf="mostrarFormNovaAula">
        <h2>{{ editandoId ? 'Editar Aula' : 'Nova Aula' }}</h2>
        <form (ngSubmit)="salvarAula()" #aulaForm="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Título da Aula</label>
              <input
                type="text"
                [(ngModel)]="novaAula.titulo"
                name="titulo"
                required
                placeholder="Ex: BJJ Iniciante">
            </div>
            <div class="form-group">
              <label>Modalidade</label>
              <select [(ngModel)]="novaAula.modalidade" name="modalidade" required>
                <option>Fundamental</option>
                <option>Intermediário</option>
                <option>Avançado</option>
                <option>Kids</option>
                <option>Competição</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Professor</label>
              <input
                type="text"
                [(ngModel)]="novaAula.professor"
                name="professor"
                required
                placeholder="Nome do professor">
            </div>
            <div class="form-group">
              <label>Data e Hora</label>
              <input
                type="datetime-local"
                [(ngModel)]="novaAula.dataHora"
                name="dataHora"
                required>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Duração (minutos)</label>
              <input
                type="number"
                [(ngModel)]="novaAula.duracao"
                name="duracao"
                required
                min="15" max="180">
            </div>
            <div class="form-group">
              <label>Alunos</label>
              <input
                type="number"
                [(ngModel)]="novaAula.alunos"
                name="alunos"
                required
                min="1" max="50">
            </div>
          </div>

          <div class="form-group">
            <label>Status</label>
            <select [(ngModel)]="novaAula.status" name="status" required>
              <option value="ativa">Ativa</option>
              <option value="cancelada">Cancelada</option>
              <option value="concluida">Concluída</option>
            </select>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-salvar" [disabled]="!aulaForm.valid">
              {{ editandoId ? 'Atualizar' : 'Criar' }} Aula
            </button>
            <button type="button" class="btn-cancelar" (click)="cancelarEdicao()">
              Cancelar
            </button>
          </div>
        </form>
      </div>

      <!-- Tabela de Aulas -->
      <div class="tabela-section">
        <table class="aulas-table">
          <thead>
            <tr>
              <th>Título</th>
              <th>Modalidade</th>
              <th>Professor</th>
              <th>Data/Hora</th>
              <th>Duração</th>
              <th>Alunos</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let aula of aulas">
              <td><strong>{{ aula.titulo }}</strong></td>
              <td>{{ aula.modalidade }}</td>
              <td>{{ aula.professor }}</td>
              <td>{{ aula.dataHora }}</td>
              <td>{{ aula.duracao }}min</td>
              <td>{{ aula.alunos }}</td>
              <td>
                <span class="badge" [class]="aula.status">
                  {{ aula.status }}
                </span>
              </td>
              <td class="acoes">
                <button class="btn-editar" (click)="editarAula(aula)" title="Editar">
                  ✏️
                </button>
                <button class="btn-deletar" (click)="confirmarDelecao(aula.id)" title="Deletar">
                  🗑️
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Confirmação de Deleção -->
      <div class="modal-overlay" *ngIf="deletando" (click)="cancelarDelecao()">
        <div class="modal" (click)="$event.stopPropagation()">
          <h3>Confirmar Deleção</h3>
          <p>Tem certeza que deseja deletar esta aula?</p>
          <div class="modal-actions">
            <button class="btn-confirmar" (click)="deletarAula()">Deletar</button>
            <button class="btn-cancelar" (click)="cancelarDelecao()">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .aulas-container {
      padding: 2rem;
      background: #f5f5f5;
      min-height: 100vh;
    }

    .aulas-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .aulas-header h1 {
      font-size: 2rem;
      color: #0a0a0a;
      margin: 0;
      font-weight: 700;
    }

    .aulas-header p {
      color: #666;
      margin: 0.5rem 0 0;
    }

    .btn-novo {
      background: #c9a84c;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-novo:hover {
      background: #b39539;
      transform: translateY(-2px);
    }

    .form-section {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      margin-bottom: 2rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .form-section h2 {
      font-size: 1.5rem;
      color: #0a0a0a;
      margin: 0 0 1.5rem;
      font-weight: 700;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
      margin-bottom: 1.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
    }

    .form-group label {
      font-weight: 600;
      margin-bottom: 0.5rem;
      color: #333;
      font-size: 0.9rem;
    }

    .form-group input,
    .form-group select {
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 0.95rem;
      font-family: inherit;
    }

    .form-group input:focus,
    .form-group select:focus {
      outline: none;
      border-color: #c9a84c;
      box-shadow: 0 0 0 3px rgba(201, 168, 76, 0.1);
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      margin-top: 2rem;
    }

    .btn-salvar {
      background: #22c55e;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-salvar:hover:not(:disabled) {
      background: #16a34a;
    }

    .btn-salvar:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .btn-cancelar {
      background: #ef4444;
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-cancelar:hover {
      background: #dc2626;
    }

    .tabela-section {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      overflow-x: auto;
    }

    .aulas-table {
      width: 100%;
      border-collapse: collapse;
    }

    .aulas-table thead {
      background: #f0f0f0;
      border-bottom: 2px solid #ddd;
    }

    .aulas-table th {
      padding: 1rem;
      text-align: left;
      font-weight: 700;
      color: #333;
      font-size: 0.9rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .aulas-table td {
      padding: 1rem;
      border-bottom: 1px solid #eee;
      color: #333;
    }

    .aulas-table tbody tr:hover {
      background: #f9f9f9;
    }

    .badge {
      display: inline-block;
      padding: 0.4rem 0.8rem;
      border-radius: 20px;
      font-size: 0.85rem;
      font-weight: 600;
    }

    .badge.ativa {
      background: #dcfce7;
      color: #166534;
    }

    .badge.cancelada {
      background: #fee2e2;
      color: #991b1b;
    }

    .badge.concluida {
      background: #dbeafe;
      color: #0c4a6e;
    }

    .acoes {
      display: flex;
      gap: 0.5rem;
    }

    .btn-editar,
    .btn-deletar {
      background: none;
      border: none;
      font-size: 1.2rem;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 4px;
      transition: background 0.3s ease;
    }

    .btn-editar:hover {
      background: #f0f0f0;
    }

    .btn-deletar:hover {
      background: #fee2e2;
    }

    .modal-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .modal {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      max-width: 400px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    }

    .modal h3 {
      font-size: 1.25rem;
      margin: 0 0 1rem;
      color: #0a0a0a;
    }

    .modal p {
      color: #666;
      margin: 0 0 1.5rem;
    }

    .modal-actions {
      display: flex;
      gap: 1rem;
    }

    .btn-confirmar {
      flex: 1;
      background: #ef4444;
      color: white;
      border: none;
      padding: 0.75rem;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.3s ease;
    }

    .btn-confirmar:hover {
      background: #dc2626;
    }

    /* Responsive adjustments */
    .admin-aulas-container .tabela-section { overflow-x: auto; }
    .admin-aulas-container .aulas-table { min-width: 600px; }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
      }

      .aulas-table {
        font-size: 0.9rem;
      }

      .aulas-table th,
      .aulas-table td {
        padding: 0.75rem 0.5rem;
      }
    }

    @media (max-width: 576px) {
      .form-row { grid-template-columns: 1fr; gap: 0.75rem; }
      .form-group { width: 100%; }
      .form-actions { justify-content: stretch; }
      .btn-salvar, .btn-cancelar { width: 100%; }
      .admin-aulas-container .aulas-table { font-size: 0.85rem; }
    }
  `]
})
export class AdminAulasComponent implements OnInit {
  aulas: Aula[] = [];
  novaAula: Partial<Aula> = {};
  mostrarFormNovaAula = false;
  editandoId: number | null = null;
  deletando = false;
  aulaDeletando: number | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.carregarAulas();
  }

  carregarAulas(): void {
    this.adminService.getAulas().subscribe(aulas => {
      this.aulas = aulas;
    });
  }

  toggleFormNovaAula(): void {
    this.mostrarFormNovaAula = !this.mostrarFormNovaAula;
    if (!this.mostrarFormNovaAula) {
      this.resetarForm();
    }
  }

  salvarAula(): void {
    if (this.editandoId) {
      this.adminService.atualizarAula(this.editandoId, this.novaAula);
    } else {
      this.adminService.adicionarAula(this.novaAula as Omit<Aula, 'id'>);
    }
    this.resetarForm();
  }

  editarAula(aula: Aula): void {
    this.novaAula = { ...aula };
    this.editandoId = aula.id;
    this.mostrarFormNovaAula = true;
  }

  cancelarEdicao(): void {
    this.resetarForm();
  }

  resetarForm(): void {
    this.novaAula = {};
    this.editandoId = null;
    this.mostrarFormNovaAula = false;
  }

  confirmarDelecao(id: number): void {
    this.aulaDeletando = id;
    this.deletando = true;
  }

  deletarAula(): void {
    if (this.aulaDeletando) {
      this.adminService.deletarAula(this.aulaDeletando);
    }
    this.cancelarDelecao();
  }

  cancelarDelecao(): void {
    this.deletando = false;
    this.aulaDeletando = null;
  }
}
