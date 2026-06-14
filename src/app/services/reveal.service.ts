import { Injectable, NgZone } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RevealService {
  constructor(private ngZone: NgZone) {}

  initRevealAnimation() {
    this.ngZone.runOutsideAngular(() => {
      const observer = new IntersectionObserver((entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            this.ngZone.run(() => {
              e.target.classList.add('visible');
            });
            observer.unobserve(e.target);
          }
        });
      }, { threshold: 0.12 });

      setTimeout(() => {
        document.querySelectorAll('.reveal').forEach((el) => {
          observer.observe(el);
        });
      }, 100);
    });
  }
}
