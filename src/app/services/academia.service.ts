import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// ===== Tipos espelhando os DTOs do backend (Fase 2) =====

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface Ref {
  id: number;
  nome: string;
}

export type Periodicidade = 'MENSAL' | 'TRIMESTRAL' | 'SEMESTRAL' | 'ANUAL';
export type DiaSemana = 'SEGUNDA' | 'TERCA' | 'QUARTA' | 'QUINTA' | 'SEXTA' | 'SABADO' | 'DOMINGO';
export type NivelTurma = 'INICIANTE' | 'INTERMEDIARIO' | 'AVANCADO' | 'KIDS' | 'TODOS';
export type StatusMatricula = 'ATIVA' | 'SUSPENSA' | 'CANCELADA';

export interface Modalidade {
  id: number;
  nome: string;
  descricao?: string;
  ativo: boolean;
}

export interface Plano {
  id: number;
  nome: string;
  descricao?: string;
  valor: number;
  periodicidade: Periodicidade;
  aulasPorSemana: number;
  ativo: boolean;
  modalidades: Ref[];
}

export interface Professor {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  faixa?: string;
  bio?: string;
  ativo: boolean;
  usuarioId?: number;
}

export interface Turma {
  id: number;
  nome: string;
  modalidade: Ref;
  professor?: Ref;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFim: string;
  capacidade: number;
  vagasOcupadas: number;
  nivel: NivelTurma;
  ativo: boolean;
}

export interface Aluno {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  dataNascimento?: string;
  cpf?: string;
  contatoEmergencia?: string;
  observacoesSaude?: string;
  faixaAtual?: string;
  ativo: boolean;
  usuarioId?: number;
}

export interface TurmaRef {
  id: number;
  nome: string;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFim: string;
}

export interface Matricula {
  id: number;
  aluno: { id: number; nome: string; email: string };
  plano: { id: number; nome: string; valor: number };
  status: StatusMatricula;
  dataInicio: string;
  dataFim?: string;
  observacao?: string;
  turmas: TurmaRef[];
}

/**
 * Cliente HTTP do nucleo da academia (backend Spring Boot, Fase 2).
 * O JWT e anexado automaticamente pelo AuthInterceptor.
 */
@Injectable({ providedIn: 'root' })
export class AcademiaService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private pageParams(page = 0, size = 20, extra: Record<string, any> = {}): HttpParams {
    let params = new HttpParams().set('page', page).set('size', size);
    for (const [k, v] of Object.entries(extra)) {
      if (v !== undefined && v !== null && v !== '') {
        params = params.set(k, v);
      }
    }
    return params;
  }

  // ===== Modalidades =====
  listarModalidades(page = 0, size = 100): Observable<PageResponse<Modalidade>> {
    return this.http.get<PageResponse<Modalidade>>(`${this.api}/modalidades`, { params: this.pageParams(page, size) });
  }
  criarModalidade(body: Partial<Modalidade>): Observable<Modalidade> {
    return this.http.post<Modalidade>(`${this.api}/modalidades`, body);
  }
  atualizarModalidade(id: number, body: Partial<Modalidade>): Observable<Modalidade> {
    return this.http.put<Modalidade>(`${this.api}/modalidades/${id}`, body);
  }
  removerModalidade(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/modalidades/${id}`);
  }

  // ===== Planos =====
  listarPlanos(page = 0, size = 100): Observable<PageResponse<Plano>> {
    return this.http.get<PageResponse<Plano>>(`${this.api}/planos`, { params: this.pageParams(page, size) });
  }
  criarPlano(body: any): Observable<Plano> {
    return this.http.post<Plano>(`${this.api}/planos`, body);
  }
  atualizarPlano(id: number, body: any): Observable<Plano> {
    return this.http.put<Plano>(`${this.api}/planos/${id}`, body);
  }
  removerPlano(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/planos/${id}`);
  }

  // ===== Professores =====
  listarProfessores(nome?: string, page = 0, size = 100): Observable<PageResponse<Professor>> {
    return this.http.get<PageResponse<Professor>>(`${this.api}/professores`, { params: this.pageParams(page, size, { nome }) });
  }
  criarProfessor(body: Partial<Professor>): Observable<Professor> {
    return this.http.post<Professor>(`${this.api}/professores`, body);
  }
  atualizarProfessor(id: number, body: Partial<Professor>): Observable<Professor> {
    return this.http.put<Professor>(`${this.api}/professores/${id}`, body);
  }
  removerProfessor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/professores/${id}`);
  }

  // ===== Turmas =====
  listarTurmas(modalidadeId?: number, ativo?: boolean, page = 0, size = 100): Observable<PageResponse<Turma>> {
    return this.http.get<PageResponse<Turma>>(`${this.api}/turmas`, { params: this.pageParams(page, size, { modalidadeId, ativo }) });
  }
  grade(): Observable<Turma[]> {
    return this.http.get<Turma[]>(`${this.api}/turmas/grade`);
  }
  criarTurma(body: any): Observable<Turma> {
    return this.http.post<Turma>(`${this.api}/turmas`, body);
  }
  atualizarTurma(id: number, body: any): Observable<Turma> {
    return this.http.put<Turma>(`${this.api}/turmas/${id}`, body);
  }
  removerTurma(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/turmas/${id}`);
  }

  // ===== Alunos =====
  /** Cadastro de aluno do usuario autenticado (portal do aluno). */
  meuPerfil(): Observable<Aluno> {
    return this.http.get<Aluno>(`${this.api}/alunos/me`);
  }
  listarAlunos(nome?: string, page = 0, size = 100): Observable<PageResponse<Aluno>> {
    return this.http.get<PageResponse<Aluno>>(`${this.api}/alunos`, { params: this.pageParams(page, size, { nome }) });
  }
  criarAluno(body: Partial<Aluno>): Observable<Aluno> {
    return this.http.post<Aluno>(`${this.api}/alunos`, body);
  }
  atualizarAluno(id: number, body: Partial<Aluno>): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.api}/alunos/${id}`, body);
  }
  removerAluno(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/alunos/${id}`);
  }

  // ===== Matriculas =====
  listarMatriculas(alunoId?: number, status?: StatusMatricula, page = 0, size = 100): Observable<PageResponse<Matricula>> {
    return this.http.get<PageResponse<Matricula>>(`${this.api}/matriculas`, { params: this.pageParams(page, size, { alunoId, status }) });
  }
  criarMatricula(body: any): Observable<Matricula> {
    return this.http.post<Matricula>(`${this.api}/matriculas`, body);
  }
  alterarStatusMatricula(id: number, status: StatusMatricula): Observable<Matricula> {
    return this.http.patch<Matricula>(`${this.api}/matriculas/${id}/status`, { status });
  }
}
