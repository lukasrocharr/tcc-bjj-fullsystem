import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { LoaderComponent } from '../loader/loader.component';
import { BackToTopComponent } from '../back-to-top/back-to-top.component';
import { HeroComponent } from '../hero/hero.component';
import { DividerMarqueeComponent } from '../divider-marquee/divider-marquee.component';
import { SobreComponent } from '../sobre/sobre.component';
import { ModalidadesComponent } from '../modalidades/modalidades.component';
import { ProfessorComponent } from '../professor/professor.component';
import { ContatoComponent } from '../contato/contato.component';
import { LojaComponent } from '../loja/loja.component';
import { ModalCarrinhoComponent } from '../modal-carrinho/modal-carrinho.component';
import { FooterComponent } from '../footer/footer.component';
import { RevealService } from '../../services/reveal.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavbarComponent,
    LoaderComponent,
    BackToTopComponent,
    HeroComponent,
    DividerMarqueeComponent,
    SobreComponent,
    ModalidadesComponent,
    ProfessorComponent,
    ContatoComponent,
    LojaComponent,
    ModalCarrinhoComponent,
    FooterComponent
  ],
  template: `
    <app-navbar></app-navbar>
    <app-loader></app-loader>
    <app-back-to-top></app-back-to-top>
    <app-hero></app-hero>
    <app-divider-marquee></app-divider-marquee>
    <app-sobre></app-sobre>
    <app-modalidades></app-modalidades>
    <app-professor></app-professor>
    <app-contato></app-contato>
    <app-loja></app-loja>
    <app-modal-carrinho></app-modal-carrinho>
    <app-footer></app-footer>
  `,
  styles: [':host { display: block; }']
})
export class HomeComponent implements OnInit {
  constructor(private revealService: RevealService) {}

  ngOnInit(): void {
    setTimeout(() => this.revealService.initRevealAnimation(), 100);
  }
}
