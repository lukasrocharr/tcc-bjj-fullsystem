import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalCarrinhoService {
  private mostrarModalSubject = new BehaviorSubject<boolean>(false);
  public mostrarModal$ = this.mostrarModalSubject.asObservable();

  abrirModal(): void {
    this.mostrarModalSubject.next(true);
    document.body.style.overflow = 'hidden';
  }

  fecharModal(): void {
    this.mostrarModalSubject.next(false);
    document.body.style.overflow = '';
  }

  obterEstado(): boolean {
    return this.mostrarModalSubject.value;
  }
}
