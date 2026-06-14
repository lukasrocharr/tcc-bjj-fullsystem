import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

/**
 * Casca raiz da aplicacao. Apenas hospeda o <router-outlet>; cada area
 * (home, loja, portal, admin) e renderizada por suas proprias rotas.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  template: `<router-outlet></router-outlet>`,
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'BJJ Landing Page';
}
