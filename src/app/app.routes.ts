import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AdminLayoutComponent } from './components/admin-layout/admin-layout.component';
import { AdminLoginComponent } from './components/admin-login/admin-login.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AdminAulasComponent } from './components/admin-aulas/admin-aulas.component';
import { AdminFinanceiroComponent } from './components/admin-financeiro/admin-financeiro.component';
import { AdminTurmasComponent } from './components/admin-turmas/admin-turmas.component';
import { AdminAlunosComponent } from './components/admin-alunos/admin-alunos.component';
import { AdminMatriculasComponent } from './components/admin-matriculas/admin-matriculas.component';
import { AdminFrequenciaComponent } from './components/admin-frequencia/admin-frequencia.component';
import { AdminGraduacaoComponent } from './components/admin-graduacao/admin-graduacao.component';
import { AdminPedidosComponent } from './components/admin-pedidos/admin-pedidos.component';
import { AdminComunicadosComponent } from './components/admin-comunicados/admin-comunicados.component';
import { AdminAuditoriaComponent } from './components/admin-auditoria/admin-auditoria.component';
import { PortalLayoutComponent } from './components/portal-layout/portal-layout.component';
import { PortalInicioComponent } from './components/portal-inicio/portal-inicio.component';
import { PortalCheckinComponent } from './components/portal-checkin/portal-checkin.component';
import { PortalGraduacoesComponent } from './components/portal-graduacoes/portal-graduacoes.component';
import { PortalFinanceiroComponent } from './components/portal-financeiro/portal-financeiro.component';
import { PortalNotificacoesComponent } from './components/portal-notificacoes/portal-notificacoes.component';
import { StoreLayoutComponent } from './components/store-layout/store-layout.component';
import { StoreCatalogoComponent } from './components/store-catalogo/store-catalogo.component';
import { StoreCarrinhoComponent } from './components/store-carrinho/store-carrinho.component';
import { StoreCheckoutComponent } from './components/store-checkout/store-checkout.component';
import { StorePedidosComponent } from './components/store-pedidos/store-pedidos.component';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'admin-login',
    component: AdminLoginComponent
  },
  // ===== Loja publica (Spring) =====
  {
    path: 'loja',
    component: StoreLayoutComponent,
    children: [
      { path: '', component: StoreCatalogoComponent },
      { path: 'carrinho', component: StoreCarrinhoComponent },
      { path: 'checkout', component: StoreCheckoutComponent },
      { path: 'meus-pedidos', component: StorePedidosComponent, canActivate: [authGuard] }
    ]
  },
  // ===== Portal do aluno =====
  {
    path: 'portal',
    component: PortalLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'inicio', pathMatch: 'full' },
      { path: 'inicio', component: PortalInicioComponent },
      { path: 'check-in', component: PortalCheckinComponent },
      { path: 'graduacoes', component: PortalGraduacoesComponent },
      { path: 'financeiro', component: PortalFinanceiroComponent },
      { path: 'notificacoes', component: PortalNotificacoesComponent }
    ]
  },
  // ===== Administracao =====
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'turmas', component: AdminTurmasComponent },
      { path: 'alunos', component: AdminAlunosComponent },
      { path: 'matriculas', component: AdminMatriculasComponent },
      { path: 'frequencia', component: AdminFrequenciaComponent },
      { path: 'graduacao', component: AdminGraduacaoComponent },
      { path: 'financeiro', component: AdminFinanceiroComponent },
      { path: 'pedidos', component: AdminPedidosComponent },
      { path: 'comunicados', component: AdminComunicadosComponent },
      { path: 'auditoria', component: AdminAuditoriaComponent },
      // Legado (backend Node :3000):
      { path: 'aulas', component: AdminAulasComponent }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
