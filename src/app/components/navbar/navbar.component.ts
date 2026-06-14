import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScrollService } from '../../services/scroll.service';
import { LojaApiService } from '../../services/loja-api.service';
import { ModalCarrinhoService } from '../../services/modal-carrinho.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  isScrolled = false;
  isMenuOpen = false;
  carrinhoQuantidade = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private scrollService: ScrollService,
    private loja: LojaApiService,
    private modalCarrinho: ModalCarrinhoService
  ) {}

  ngOnInit() {
    this.scrollService.scrollPosition$
      .pipe(takeUntil(this.destroy$))
      .subscribe(scrollY => {
        this.isScrolled = scrollY > 60;
      });

    // Observar quantidade de itens no carrinho (estado reativo do Spring)
    this.loja.totalItens$
      .pipe(takeUntil(this.destroy$))
      .subscribe(total => {
        this.carrinhoQuantidade = total;
      });

    // Carrega o carrinho inicial.
    this.loja.verCarrinho().subscribe({ error: () => {} });
  }

  abrirCarrinho(): void {
    this.modalCarrinho.abrirModal();
  }

  toggleMobileMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    if (this.isMenuOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }

  closeMobileMenu() {
    this.isMenuOpen = false;
    document.body.style.overflow = '';
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    document.body.style.overflow = '';
  }
}
