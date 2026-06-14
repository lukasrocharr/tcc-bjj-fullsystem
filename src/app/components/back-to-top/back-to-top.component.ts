import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScrollService } from '../../services/scroll.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-back-to-top',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './back-to-top.component.html',
  styleUrls: ['./back-to-top.component.css']
})
export class BackToTopComponent implements OnInit, OnDestroy {
  isVisible = false;
  private destroy$ = new Subject<void>();

  constructor(private scrollService: ScrollService) {}

  ngOnInit() {
    this.scrollService.scrollPosition$
      .pipe(takeUntil(this.destroy$))
      .subscribe(scrollY => {
        this.isVisible = scrollY > 400;
      });
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
