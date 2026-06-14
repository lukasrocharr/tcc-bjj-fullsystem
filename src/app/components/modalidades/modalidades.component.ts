import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modalidades',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modalidades.component.html',
  styleUrls: ['./modalidades.component.css']
})
export class ModalidadesComponent {
  modalidades = [
    {
      tag: 'A partir de 16 anos',
      title: 'ADULTO',
      description: 'Treinamento completo para adultos com foco em técnica, condicionamento e competição.'
    },
    {
      tag: '4 a 15 anos',
      title: 'KIDS',
      description: 'Programa especial para crianças desenvolverem disciplina, respeito e confiança.'
    },
    {
      tag: 'Nível avançado',
      title: 'COMPETITION',
      description: 'Preparação específica para atletas que buscam resultados em alto nível.'
    }
  ];
}
