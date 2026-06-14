import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>🥋 Admin BJJ</h1>
          <p>Sistema de Gestão da Academia</p>
        </div>

        <form (ngSubmit)="fazerLogin()" class="login-form">
          <div class="form-group">
            <label for="email">E-mail</label>
            <input
              id="email"
              type="email"
              [(ngModel)]="email"
              name="email"
              placeholder="admin@bjj.local"
              required
              [disabled]="isLoading"
            />
          </div>

          <div class="form-group">
            <label for="password">Senha</label>
            <input
              id="password"
              type="password"
              [(ngModel)]="password"
              name="password"
              placeholder="••••••••"
              required
              [disabled]="isLoading"
            />
          </div>

          <div *ngIf="erro" class="error-message">
            ⚠️ {{ erro }}
          </div>

          <button
            type="submit"
            class="btn-login"
            [disabled]="isLoading || !email || !password"
          >
            <span *ngIf="!isLoading">Entrar</span>
            <span *ngIf="isLoading">Carregando...</span>
          </button>
        </form>

        <div class="login-footer">
          <p>Credenciais de teste:</p>
          <p class="credentials">
            E-mail: <strong>admin&#64;bjj.local</strong><br />
            Senha: <strong>Admin&#64;123</strong>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%);
      padding: 1rem;
    }

    .login-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
      max-width: 400px;
      width: 100%;
      padding: 2.5rem;
      animation: slideUp 0.5s ease-out;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .login-header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .login-header h1 {
      font-size: 1.75rem;
      color: #c9a84c;
      margin: 0 0 0.5rem;
      font-weight: 700;
    }

    .login-header p {
      color: #888;
      margin: 0;
      font-size: 0.9rem;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .form-group label {
      font-weight: 600;
      color: #0a0a0a;
      font-size: 0.9rem;
    }

    .form-group input {
      padding: 0.75rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      font-size: 1rem;
      transition: all 0.3s ease;
      font-family: inherit;
    }

    .form-group input:focus {
      outline: none;
      border-color: #c9a84c;
      box-shadow: 0 0 0 3px rgba(201, 168, 76, 0.1);
    }

    .form-group input:disabled {
      background: #f5f5f5;
      cursor: not-allowed;
    }

    .error-message {
      padding: 0.75rem 1rem;
      background: #fee2e2;
      border: 1px solid #fecaca;
      border-radius: 8px;
      color: #dc2626;
      font-size: 0.9rem;
      animation: shake 0.4s ease-in-out;
    }

    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-5px); }
      75% { transform: translateX(5px); }
    }

    .btn-login {
      padding: 0.75rem 1.5rem;
      background: linear-gradient(135deg, #c9a84c 0%, #b8971e 100%);
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .btn-login:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(201, 168, 76, 0.4);
    }

    .btn-login:active:not(:disabled) {
      transform: translateY(0);
    }

    .btn-login:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .login-footer {
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #e0e0e0;
      text-align: center;
    }

    .login-footer p {
      margin: 0.5rem 0;
      font-size: 0.85rem;
      color: #666;
    }

    .credentials {
      font-family: 'Courier New', monospace;
      background: #f5f5f5;
      padding: 0.75rem;
      border-radius: 6px;
      color: #0a0a0a;
    }

    @media (max-width: 480px) {
      .login-card {
        padding: 1.5rem;
      }

      .login-header h1 {
        font-size: 1.5rem;
      }
    }
  `]
})
export class AdminLoginComponent {
  email = '';
  password = '';
  isLoading = false;
  erro: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  fazerLogin(): void {
    if (!this.email || !this.password) {
      this.erro = 'Preencha e-mail e senha';
      return;
    }

    this.isLoading = true;
    this.erro = null;

    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.isLoading = false;
        const redirect = this.route.snapshot.queryParamMap.get('redirect') || '/admin';
        this.router.navigateByUrl(redirect);
      },
      error: (error) => {
        this.isLoading = false;
        this.erro = this.mensagemErro(error);
        this.password = '';
      }
    });
  }

  private mensagemErro(error: any): string {
    if (error?.status === 0) {
      return 'Nao foi possivel conectar ao servidor. O backend esta no ar?';
    }
    return error?.error?.message || 'E-mail ou senha invalidos';
  }
}
