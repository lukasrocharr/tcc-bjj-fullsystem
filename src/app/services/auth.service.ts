import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface UsuarioInfo {
  id: number;
  nome: string;
  email: string;
  papeis: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  usuario: UsuarioInfo;
}

export interface AuthState {
  isLoggedIn: boolean;
  token: string | null;
  usuario: UsuarioInfo | null;
}

const TOKEN_KEY = 'auth_token';
const REFRESH_KEY = 'auth_refresh';
const USER_KEY = 'auth_usuario';

/**
 * Servico de autenticacao integrado ao backend Spring Boot (/api/v1/auth).
 * Faz login por e-mail + senha, guarda access/refresh tokens e expoe o estado
 * de autenticacao via BehaviorSubject (diretriz 10 do prompt mestre).
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  private authState$ = new BehaviorSubject<AuthState>({
    isLoggedIn: false,
    token: null,
    usuario: null
  });

  constructor(private http: HttpClient) {
    this.loadAuthState();
  }

  private loadAuthState(): void {
    const token = localStorage.getItem(TOKEN_KEY);
    const usuario = localStorage.getItem(USER_KEY);

    if (token && usuario) {
      this.authState$.next({
        isLoggedIn: true,
        token,
        usuario: JSON.parse(usuario)
      });
    }
  }

  private persist(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.accessToken);
    localStorage.setItem(REFRESH_KEY, response.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(response.usuario));
    this.authState$.next({
      isLoggedIn: true,
      token: response.accessToken,
      usuario: response.usuario
    });
  }

  login(email: string, senha: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, senha }).pipe(
      tap(response => this.persist(response)),
      catchError(error => {
        console.error('Erro de login:', error);
        return throwError(() => error);
      })
    );
  }

  register(nome: string, email: string, senha: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, { nome, email, senha }).pipe(
      tap(response => this.persist(response))
    );
  }

  /** Troca o refresh token por um novo par; atualiza o storage. */
  refresh(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('Sem refresh token'));
    }
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => this.persist(response))
    );
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      // Revoga o refresh token no servidor (best-effort).
      this.http.post(`${this.apiUrl}/logout`, { refreshToken }).subscribe({
        error: () => { /* logout local segue mesmo se a chamada falhar */ }
      });
    }
    this.clearSession();
  }

  clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.authState$.next({ isLoggedIn: false, token: null, usuario: null });
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getAuthState(): Observable<AuthState> {
    return this.authState$.asObservable();
  }

  getCurrentUser(): UsuarioInfo | null {
    const usuario = localStorage.getItem(USER_KEY);
    return usuario ? JSON.parse(usuario) : null;
  }

  getPapeis(): string[] {
    return this.getCurrentUser()?.papeis ?? [];
  }

  hasRole(papel: string): boolean {
    return this.getPapeis().includes(papel);
  }

  hasAnyRole(papeis: string[]): boolean {
    const meus = this.getPapeis();
    return papeis.some(p => meus.includes(p));
  }
}
