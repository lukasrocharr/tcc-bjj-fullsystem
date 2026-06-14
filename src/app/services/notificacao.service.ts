import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageResponse } from './academia.service';

export interface Notificacao {
  id: number;
  titulo: string;
  mensagem: string;
  lida: boolean;
  criadoEm: string;
}

export type PapelAlvo = 'ALUNO' | 'PROFESSOR' | 'ADMIN' | 'SUPER_ADMIN';

export interface ComunicadoRequest {
  titulo: string;
  mensagem: string;
  papel?: PapelAlvo | null;
  enviarEmail: boolean;
}

/**
 * Cliente HTTP de notificacoes in-app (RF-103) e comunicados em massa (RF-104).
 */
@Injectable({ providedIn: 'root' })
export class NotificacaoService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 20): Observable<PageResponse<Notificacao>> {
    return this.http.get<PageResponse<Notificacao>>(`${this.api}/notificacoes`, { params: { page, size } as any });
  }

  contarNaoLidas(): Observable<{ naoLidas: number }> {
    return this.http.get<{ naoLidas: number }>(`${this.api}/notificacoes/nao-lidas/contagem`);
  }

  marcarLida(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/notificacoes/${id}/lida`, {});
  }

  marcarTodasLidas(): Observable<{ atualizadas: number }> {
    return this.http.patch<{ atualizadas: number }>(`${this.api}/notificacoes/lidas`, {});
  }

  enviarComunicado(req: ComunicadoRequest): Observable<{ destinatarios: number }> {
    return this.http.post<{ destinatarios: number }>(`${this.api}/notificacoes/comunicados`, req);
  }
}
