import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sobre',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sobre.component.html',
  styleUrls: ['./sobre.component.css']
})
export class SobreComponent implements OnInit, OnDestroy {
  images = [
    'assets/landing/1781405392735.webp',
    'assets/landing/1781405408037.webp',
    'assets/landing/a_cuzati_1781405345116.jpeg',
    'assets/landing/geovane.ssouza_1781405356243.jpeg',
    'assets/landing/geovane.ssouza_1781405378762.jpeg',
    'assets/landing/mikalkenasjiujitsu_1781405336351.jpeg',
    'assets/landing/thiagodantas.fotop_1781405368518.jpeg'
  ];

  currentIndex = 0;
  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnInit(): void {
    this.startAutoSlide();
  }

  ngOnDestroy(): void {
    this.clearAutoSlide();
  }

  startAutoSlide(): void {
    this.clearAutoSlide();
    this.intervalId = setInterval(() => this.nextSlide(), 7000);
  }

  clearAutoSlide(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  nextSlide(): void {
    this.currentIndex = (this.currentIndex + 1) % this.images.length;
  }

  goToSlide(index: number): void {
    this.currentIndex = index;
    this.startAutoSlide();
  }

  pauseSlide(): void {
    this.clearAutoSlide();
  }

  resumeSlide(): void {
    this.startAutoSlide();
  }
}
