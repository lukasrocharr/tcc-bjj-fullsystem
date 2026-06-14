import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Restringe rotas por papel (RBAC no frontend). Use em `data.roles`:
 *   { path: 'admin', canActivate: [authGuard, roleGuard], data: { roles: ['ADMIN','SUPER_ADMIN'] } }
 *
 * Esta e uma camada de UX; a autorizacao efetiva e garantida no backend
 * (@PreAuthorize), conforme criterio de aceitacao do SRS.
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/admin-login'], {
      queryParams: { redirect: state.url }
    });
  }

  const roles = (route.data?.['roles'] as string[]) ?? [];
  if (roles.length === 0 || authService.hasAnyRole(roles)) {
    return true;
  }

  // Autenticado mas sem permissao: volta para a home publica.
  return router.createUrlTree(['/']);
};
