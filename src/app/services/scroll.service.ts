import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScrollService {
  private scrollPositionSubject = new BehaviorSubject<number>(0);
  public scrollPosition$: Observable<number> = this.scrollPositionSubject.asObservable();

  constructor(private ngZone: NgZone) {
    this.ngZone.runOutsideAngular(() => {
      window.addEventListener('scroll', () => {
        this.ngZone.run(() => {
          this.scrollPositionSubject.next(window.scrollY);
        });
      });
    });
  }
}
