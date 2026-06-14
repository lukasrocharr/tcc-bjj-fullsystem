import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-divider-marquee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './divider-marquee.component.html',
  styleUrls: ['./divider-marquee.component.css']
})
export class DividerMarqueeComponent {
  items = ['Jiu-Jitsu', 'Disciplina', 'Arte', 'Resistência', 'Técnica', 'Respeito', 'Superação', 'Camaradagem'];
  displayItems = [...this.items, ...this.items];
}
