import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * Anexa o JWT a cada requisicao e, em caso de 401, tenta renovar o token
 * via refresh token uma unica vez antes de redirecionar ao login
 * (diretriz 10 do prompt mestre).
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshSubject = new BehaviorSubject<string | null>(null);

  constructor(private authService: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authReq = this.addToken(req, this.authService.getToken());

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !this.isAuthEndpoint(req.url)) {
          return this.handle401(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(req: HttpRequest<any>, token: string | null): HttpRequest<any> {
    if (!token) {
      return req;
    }
    return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  private isAuthEndpoint(url: string): boolean {
    return url.includes('/auth/login')
      || url.includes('/auth/refresh')
      || url.includes('/auth/register');
  }

  private handle401(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.isRefreshing) {
      // Aguarda o refresh em andamento e reenvia a requisicao com o novo token.
      return this.refreshSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => next.handle(this.addToken(req, token)))
      );
    }

    this.isRefreshing = true;
    this.refreshSubject.next(null);

    return this.authService.refresh().pipe(
      switchMap(response => {
        this.isRefreshing = false;
        this.refreshSubject.next(response.accessToken);
        return next.handle(this.addToken(req, response.accessToken));
      }),
      catchError(err => {
        this.isRefreshing = false;
        this.authService.clearSession();
        this.router.navigate(['/admin-login']);
        return throwError(() => err);
      })
    );
  }
}
