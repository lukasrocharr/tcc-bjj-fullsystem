import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Aula {
  id: number;
  titulo: string;
  professor: string;
  modalidade: string;
  dataHora: string;
  duracao: number;
  alunos: number;
  status: 'ativa' | 'cancelada' | 'concluida';
}

export interface Transacao {
  id: number;
  tipo: 'receita' | 'despesa';
  descricao: string;
  valor: number;
  data: string;
  categoria: string;
  status: 'pendente' | 'confirmado' | 'cancelado';
}

export interface RelatorioGeral {
  receitas: number;
  despesas: number;
  saldo: number;
  transacoes: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:3000/api';

  constructor(private http: HttpClient) {}

  // ====== AULAS ======
  getAulas(): Observable<Aula[]> {
    return this.http.get<Aula[]>(`${this.apiUrl}/aulas`).pipe(
      catchError(error => {
        console.error('Erro ao buscar aulas:', error);
        return throwError(() => error);
      })
    );
  }

  getAula(id: number): Observable<Aula> {
    return this.http.get<Aula>(`${this.apiUrl}/aulas/${id}`).pipe(
      catchError(error => {
        console.error('Erro ao buscar aula:', error);
        return throwError(() => error);
      })
    );
  }

  adicionarAula(aula: Omit<Aula, 'id'>): Observable<Aula> {
    return this.http.post<Aula>(`${this.apiUrl}/aulas`, aula).pipe(
      tap(novaAula => console.log('Aula criada:', novaAula)),
      catchError(error => {
        console.error('Erro ao criar aula:', error);
        return throwError(() => error);
      })
    );
  }

  atualizarAula(id: number, aula: Partial<Aula>): Observable<Aula> {
    return this.http.put<Aula>(`${this.apiUrl}/aulas/${id}`, aula).pipe(
      tap(aulaAtualizada => console.log('Aula atualizada:', aulaAtualizada)),
      catchError(error => {
        console.error('Erro ao atualizar aula:', error);
        return throwError(() => error);
      })
    );
  }

  deletarAula(id: number): Observable<Aula> {
    return this.http.delete<Aula>(`${this.apiUrl}/aulas/${id}`).pipe(
      tap(aulaDeletada => console.log('Aula deletada:', aulaDeletada)),
      catchError(error => {
        console.error('Erro ao deletar aula:', error);
        return throwError(() => error);
      })
    );
  }

  // ====== TRANSAÇÕES ======
  getTransacoes(): Observable<Transacao[]> {
    return this.http.get<Transacao[]>(`${this.apiUrl}/transacoes`).pipe(
      catchError(error => {
        console.error('Erro ao buscar transações:', error);
        return throwError(() => error);
      })
    );
  }

  getTransacao(id: number): Observable<Transacao> {
    return this.http.get<Transacao>(`${this.apiUrl}/transacoes/${id}`).pipe(
      catchError(error => {
        console.error('Erro ao buscar transação:', error);
        return throwError(() => error);
      })
    );
  }

  adicionarTransacao(transacao: Omit<Transacao, 'id'>): Observable<Transacao> {
    return this.http.post<Transacao>(`${this.apiUrl}/transacoes`, transacao).pipe(
      tap(novaTransacao => console.log('Transação criada:', novaTransacao)),
      catchError(error => {
        console.error('Erro ao criar transação:', error);
        return throwError(() => error);
      })
    );
  }

  atualizarTransacao(id: number, transacao: Partial<Transacao>): Observable<Transacao> {
    return this.http.put<Transacao>(`${this.apiUrl}/transacoes/${id}`, transacao).pipe(
      tap(transacaoAtualizada => console.log('Transação atualizada:', transacaoAtualizada)),
      catchError(error => {
        console.error('Erro ao atualizar transação:', error);
        return throwError(() => error);
      })
    );
  }

  deletarTransacao(id: number): Observable<Transacao> {
    return this.http.delete<Transacao>(`${this.apiUrl}/transacoes/${id}`).pipe(
      tap(transacaoDeletada => console.log('Transação deletada:', transacaoDeletada)),
      catchError(error => {
        console.error('Erro ao deletar transação:', error);
        return throwError(() => error);
      })
    );
  }

  // ====== RELATÓRIOS ======
  getRelatorioGeral(): Observable<RelatorioGeral> {
    return this.http.get<RelatorioGeral>(`${this.apiUrl}/relatorios/geral`).pipe(
      catchError(error => {
        console.error('Erro ao buscar relatório geral:', error);
        return throwError(() => error);
      })
    );
  }

  getRelatorioMensal(mes: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/relatorios/mensal/${mes}`).pipe(
      catchError(error => {
        console.error('Erro ao buscar relatório mensal:', error);
        return throwError(() => error);
      })
    );
  }

  // ====== ESTATÍSTICAS (DEPRECATED - usar getRelatorioGeral) ======
  getTotalReceitas(): Observable<number> {
    return new Observable(observer => {
      this.getRelatorioGeral().subscribe(
        relatorio => {
          observer.next(relatorio.receitas);
          observer.complete();
        },
        error => observer.error(error)
      );
    });
  }

  getTotalDespesas(): Observable<number> {
    return new Observable(observer => {
      this.getRelatorioGeral().subscribe(
        relatorio => {
          observer.next(relatorio.despesas);
          observer.complete();
        },
        error => observer.error(error)
      );
    });
  }

  getSaldoMensal(): Observable<number> {
    return new Observable(observer => {
      this.getRelatorioGeral().subscribe(
        relatorio => {
          observer.next(relatorio.saldo);
          observer.complete();
        },
        error => observer.error(error)
      );
    });
  }
}
