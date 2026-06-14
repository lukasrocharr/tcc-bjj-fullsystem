import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageResponse } from './academia.service';

// ===== Dashboard =====
export interface PontoSerie { competencia: string; valor: number; }
export interface Dashboard {
  alunosAtivos: number;
  novasMatriculasMes: number;
  turmas: number;
  produtos: number;
  pedidosPendentes: number;
  riscoEvasao: number;
  receitaMensalidadesMes: number;
  receitaLojaMes: number;
  receitaTotalMes: number;
  inadimplenciaValor: number;
  inadimplenciaQtd: number;
  serieReceita: PontoSerie[];
}

// ===== Financeiro =====
export type StatusMensalidade = 'PENDENTE' | 'PAGA' | 'ATRASADA' | 'CANCELADA';
export type MetodoPagamento = 'PIX' | 'BOLETO' | 'CARTAO' | 'DINHEIRO';

export interface Mensalidade {
  id: number;
  aluno: { id: number; nome: string; email: string };
  plano: string;
  ano: number;
  mes: number;
  valor: number;
  multa: number;
  juros: number;
  valorTotal: number;
  valorPago?: number;
  dataVencimento: string;
  dataPagamento?: string;
  status: StatusMensalidade;
}

export interface RelatorioFinanceiro {
  ano?: number;
  mes?: number;
  totalRecebido: number;
  totalPendente: number;
  totalAtrasado: number;
  qtdPagas: number;
  qtdPendentes: number;
  qtdAtrasadas: number;
  totalLoja: number;
  totalGeral: number;
}

export interface GerarResultado { ano: number; mes: number; geradas: number; ignoradas: number; }

// ===== Auditoria =====
export interface Auditoria {
  id: number;
  usuarioEmail: string;
  metodo: string;
  caminho: string;
  status: number;
  criadoEm: string;
}

/**
 * Cliente HTTP dos modulos de gestao do backend Spring (Fases 4 e 6):
 * dashboard, financeiro, auditoria e notificacoes.
 */
@Injectable({ providedIn: 'root' })
export class GestaoService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Dashboard
  dashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.api}/dashboard`);
  }

  // Financeiro
  listarMensalidades(alunoId?: number, status?: StatusMensalidade, page = 0, size = 50): Observable<PageResponse<Mensalidade>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (alunoId) params = params.set('alunoId', alunoId);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Mensalidade>>(`${this.api}/financeiro/mensalidades`, { params });
  }

  // Financeiro — self-service do aluno (portal)
  minhasMensalidades(page = 0, size = 50): Observable<PageResponse<Mensalidade>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Mensalidade>>(`${this.api}/financeiro/me/mensalidades`, { params });
  }

  meuReciboBlob(id: number): Observable<Blob> {
    return this.http.get(`${this.api}/financeiro/me/mensalidades/${id}/recibo`, { responseType: 'blob' });
  }

  gerarMensalidades(ano: number, mes: number): Observable<GerarResultado> {
    return this.http.post<GerarResultado>(`${this.api}/financeiro/mensalidades/gerar`, { ano, mes });
  }

  pagar(id: number, metodo: MetodoPagamento): Observable<unknown> {
    return this.http.post(`${this.api}/financeiro/mensalidades/${id}/pagar`, { metodo });
  }

  cancelarMensalidade(id: number): Observable<unknown> {
    return this.http.post(`${this.api}/financeiro/mensalidades/${id}/cancelar`, {});
  }

  atualizarAtrasadas(): Observable<{ processadas: number }> {
    return this.http.post<{ processadas: number }>(`${this.api}/financeiro/atualizar-atrasadas`, {});
  }

  relatorioFinanceiro(ano?: number, mes?: number): Observable<RelatorioFinanceiro> {
    let params = new HttpParams();
    if (ano) params = params.set('ano', ano);
    if (mes) params = params.set('mes', mes);
    return this.http.get<RelatorioFinanceiro>(`${this.api}/financeiro/relatorio`, { params });
  }

  /** URL para baixar o recibo PDF (abrir em nova aba; o interceptor anexa o JWT em chamadas HttpClient,
   *  mas para download direto use o metodo abaixo que retorna um Blob). */
  reciboBlob(id: number): Observable<Blob> {
    return this.http.get(`${this.api}/financeiro/mensalidades/${id}/recibo`, { responseType: 'blob' });
  }

  // Auditoria
  listarAuditoria(page = 0, size = 30): Observable<PageResponse<Auditoria>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Auditoria>>(`${this.api}/auditoria`, { params });
  }

  // Relatorios CSV (download)
  exportarCsv(tipo: 'alunos' | 'mensalidades'): Observable<Blob> {
    return this.http.get(`${this.api}/relatorios/${tipo}.csv`, { responseType: 'blob' });
  }
}
