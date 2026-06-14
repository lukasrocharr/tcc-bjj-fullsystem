import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// ===== Tipos espelhando os DTOs do backend (Fase 3) =====
export interface AlunoRef { id: number; nome: string; email: string; }
export interface TurmaRef { id: number; nome: string; diaSemana: string; horaInicio: string; horaFim: string; }
export type OrigemCheckIn = 'SELF' | 'PROFESSOR';

export interface CheckIn {
  id: number;
  aluno: AlunoRef;
  turma: TurmaRef;
  data: string;
  dataHora: string;
  origem: OrigemCheckIn;
}

export interface Frequencia {
  alunoId: number;
  totalCheckIns: number;
  checkInsNoMes: number;
  diasDistintos: number;
  ultimoCheckIn?: string;
  streakDias: number;
}

/**
 * Cliente HTTP do modulo de frequencia (check-in, chamada e indicadores).
 * O JWT e anexado automaticamente pelo AuthInterceptor.
 */
@Injectable({ providedIn: 'root' })
export class FrequenciaService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  checkIn(alunoId: number, turmaId: number): Observable<CheckIn> {
    return this.http.post<CheckIn>(`${this.api}/frequencia/check-in`, { alunoId, turmaId });
  }

  chamada(turmaId: number, alunoIds: number[], data?: string): Observable<CheckIn[]> {
    return this.http.post<CheckIn[]>(`${this.api}/frequencia/chamada`, { turmaId, data, alunoIds });
  }

  indicadores(alunoId: number): Observable<Frequencia> {
    return this.http.get<Frequencia>(`${this.api}/frequencia/aluno/${alunoId}`);
  }

  historico(alunoId: number): Observable<CheckIn[]> {
    return this.http.get<CheckIn[]>(`${this.api}/frequencia/aluno/${alunoId}/historico`);
  }

  alertasBaixa(): Observable<AlunoRef[]> {
    return this.http.get<AlunoRef[]>(`${this.api}/frequencia/alertas-baixa`);
  }
}
