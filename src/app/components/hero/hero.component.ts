import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScrollService } from '../../services/scroll.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent implements OnInit, OnDestroy {
  scrollY = 0;
  private destroy$ = new Subject<void>();
  private slideInterval?: number;
  currentIndex = 0;
  activeLayer = 0;
  frontIndex = 0;
  backIndex = 1;
  images = [
    '/assets/instagram/hero.jpg',
    '/assets/instagram/717665460_18418971751178589_7033975350436337710_n.webp',
    '/assets/instagram/717709160_18418971769178589_641381650599257327_n.webp',
    '/assets/instagram/718277506_18418971826178589_3250801109187314587_n.webp',
    '/assets/instagram/720049931_18130721026549081_5786044281235134173_n.webp',
    '/assets/instagram/722447866_17893668369526069_9181906916421157468_n.webp',
    '/assets/instagram/anamikalkenas.personal_1781392852909.webp',
    '/assets/instagram/th_eiji_1781392907078.jpeg'
  ];

  constructor(private scrollService: ScrollService) {}

  ngOnInit() {
    this.scrollService.scrollPosition$
      .pipe(takeUntil(this.destroy$))
      .subscribe(scrollY => {
        this.scrollY = scrollY;
      });

    this.startSlideShow();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.slideInterval) {
      window.clearInterval(this.slideInterval);
    }
  }

  private nextSlide(): void {
    const nextIndex = (this.currentIndex + 1) % this.images.length;
    this.prepareNextLayer(nextIndex);
    this.currentIndex = nextIndex;
  }

  goToSlide(index: number): void {
    const slide = index % this.images.length;
    this.prepareNextLayer(slide);
    this.currentIndex = slide;
    this.restartSlideShow();
  }

  pauseSlide(): void {
    if (this.slideInterval) {
      window.clearInterval(this.slideInterval);
      this.slideInterval = undefined;
    }
  }

  resumeSlide(): void {
    if (!this.slideInterval) {
      this.startSlideShow();
    }
  }

  backgroundForLayer(layer: number): string {
    const index = layer === 0 ? this.frontIndex : this.backIndex;
    return this.buildBackground(this.images[index]);
  }

  private prepareNextLayer(nextIndex: number): void {
    this.activeLayer = 1 - this.activeLayer;
    if (this.activeLayer === 0) {
      this.frontIndex = nextIndex;
    } else {
      this.backIndex = nextIndex;
    }
  }

  private startSlideShow(): void {
    this.slideInterval = window.setInterval(() => {
      this.nextSlide();
    }, 10000);
  }

  private restartSlideShow(): void {
    this.pauseSlide();
    this.startSlideShow();
  }

  private buildBackground(imageUrl: string): string {
    return `linear-gradient(135deg, rgba(10,10,10,.92) 0%, rgba(10,10,10,.6) 50%, rgba(10,10,10,.88) 100%), url('${imageUrl}') center/cover no-repeat`;
  }
}
