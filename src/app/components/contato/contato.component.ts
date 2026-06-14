import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contato',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contato.component.html',
  styleUrls: ['./contato.component.css']
})
export class ContatoComponent {
  formData = {
    nome: '',
    telefone: '',
    modalidade: '',
    mensagem: ''
  };
  submitButtonText = 'Enviar mensagem →';
  isSubmitted = false;

  handleForm(e: Event) {
    e.preventDefault();
    this.submitButtonText = '✓ Mensagem enviada!';
    this.isSubmitted = true;
    
    setTimeout(() => {
      this.submitButtonText = 'Enviar mensagem →';
      this.isSubmitted = false;
      this.formData = {
        nome: '',
        telefone: '',
        modalidade: '',
        mensagem: ''
      };
    }, 3000);
  }
}
