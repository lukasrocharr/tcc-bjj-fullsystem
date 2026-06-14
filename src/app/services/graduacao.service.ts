import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AlunoRef } from './frequencia.service';

// ===== Tipos espelhando os DTOs do backend (Fase 3) =====
export type CategoriaFaixa = 'ADULTO' | 'INFANTIL';

export interface Faixa {
  id: number;
  nome: string;
  categoria: CategoriaFaixa;
  ordem: number;
  grausMax: number;
  ativo: boolean;
}

export interface FaixaRef { id: number; nome: string; }
export interface ProfessorRef { id: number; nome: string; }

export interface Graduacao {
  id: number;
  aluno: AlunoRef;
  faixa: FaixaRef;
  graus: number;
  data: string;
  professor?: ProfessorRef;
  observacao?: string;
}

export interface FaixaAtual {
  alunoId: number;
  faixa?: string;
  graus: number;
  desde?: string;
  diasNaFaixa: number;
}

/**
 * Cliente HTTP do modulo de graduacao (faixas, promocoes, certificado).
 */
@Injectable({ providedIn: 'root' })
export class GraduacaoService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  faixas(): Observable<Faixa[]> {
    return this.http.get<Faixa[]>(`${this.api}/graduacoes/faixas`);
  }

  registrar(body: { alunoId: number; faixaId: number; graus: number; data?: string; professorId?: number; observacao?: string }): Observable<Graduacao> {
    return this.http.post<Graduacao>(`${this.api}/graduacoes`, body);
  }

  historico(alunoId: number): Observable<Graduacao[]> {
    return this.http.get<Graduacao[]>(`${this.api}/graduacoes/aluno/${alunoId}`);
  }

  faixaAtual(alunoId: number): Observable<FaixaAtual> {
    return this.http.get<FaixaAtual>(`${this.api}/graduacoes/aluno/${alunoId}/faixa-atual`);
  }

  elegiveis(): Observable<AlunoRef[]> {
    return this.http.get<AlunoRef[]>(`${this.api}/graduacoes/elegiveis`);
  }

  certificadoBlob(id: number): Observable<Blob> {
    return this.http.get(`${this.api}/graduacoes/${id}/certificado`, { responseType: 'blob' });
  }
}
