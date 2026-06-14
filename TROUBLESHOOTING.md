# 🔧 Troubleshooting Guide

## Erros Comuns e Soluções

### 1. "ng: command not found"

**Problema**: Angular CLI não está instalado globalmente

**Solução**:
```bash
npm install -g @angular/cli@18
```

ou use npx:
```bash
npx ng serve
```

---

### 2. "Module not found: Can't resolve '@angular/core'"

**Problema**: Dependências não instaladas

**Solução**:
```bash
npm install
# ou limpar cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

---

### 3. Port 4200 Already in Use

**Problema**: Porta 4200 está ocupada

**Solução**:
```bash
# Use uma porta diferente
ng serve --port 4201

# ou mate o processo usando a porta
# Windows
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :4200
kill -9 <PID>
```

---

### 4. "ERROR: An error occurred using @schematics/angular"

**Problema**: Versão do Angular não compatível

**Solução**:
```bash
# Verifique a versão
ng version

# Atualize se necessário
npm install -g @angular/cli@latest
ng update @angular/cli
ng update @angular/core
```

---

### 5. TypeScript Compilation Errors

**Problema**: Erros de compilação TypeScript

**Solução**:
```bash
# Limpe o cache
rm -rf .angular/cache

# Recompile
ng build
```

**Verifique no `tsconfig.json`**:
```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true
  }
}
```

---

### 6. CSS/Styles Not Loading

**Problema**: Estilos não aparecem

**Soluções**:

1. Verifique o caminho do arquivo:
```typescriptangular
styleUrls: ['./component.css']  // ✅ Correto
styleurls: ['./component.css']  // ❌ Errado
```

2. Certifique-se que o arquivo existe:
```bash
ls -la src/app/components/navbar/navbar.component.css
```

3. Importar estilos globais no `angular.json`:
```json
{
  "styles": [
    "src/styles.css"
  ]
}
```

---

### 7. Browser Not Auto-Refreshing

**Problema**: Mudanças não aparecem automaticamente (Hot Reload)

**Solução**:
```bash
# Tente reiniciar
ng serve --poll=1000
```

ou edite `angular.json`:
```json
{
  "serve": {
    "options": {
      "poll": 1000
    }
  }
}
```

---

### 8. "RxJS version mismatch"

**Problema**: Versão do RxJS incompatível

**Solução**:
```bash
npm install rxjs@7.8.0 --save
npm install
```

Verifique no `package.json`:
```json
{
  "dependencies": {
    "rxjs": "^7.8.0"
  }
}
```

---

### 9. "Cannot find module 'zone.js'"

**Problema**: Zone.js não instalado

**Solução**:
```bash
npm install zone.js
# ou
npm install
```

---

### 10. Build Too Slow

**Problema**: Compilação lenta

**Soluções**:

1. Aumente a memória Node:
```bash
NODE_OPTIONS=--max_old_space_size=4096 ng build
```

2. Use build differential:
```bash
ng build --configuration=production
```

3. Ative cache:
```bash
ng cache clean  # limpe cache antigo
ng build --cache # ative novo cache
```

---

### 11. "Cannot bind to 'ngModel' since it isn't a known property"

**Problema**: FormModule não importado

**Solução** (em cada componente):
```typescript
import { FormsModule } from '@angular/forms';

@Component({
  imports: [FormsModule]  // ✅ Adicione isto
})
```

---

### 12. "NullInjectorError: No provider for Service"

**Problema**: Serviço não fornecido/injetado

**Solução**:

Opção 1 - ProvidedIn root:
```typescript
@Injectable({
  providedIn: 'root'  // ✅ Isto
})
export class MyService {}
```

Opção 2 - Importar no componente:
```typescript
import { MyService } from './services/my.service';

@Component({
  providers: [MyService]  // ✅ ou isto
})
```

---

### 13. "Cannot find name 'environment'"

**Problema**: Arquivo environment não existe

**Solução**:
```bash
# Crie os arquivos
touch src/environments/environment.ts
touch src/environments/environment.prod.ts
```

Adicione conteúdo:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:3000/api'
};
```

---

### 14. "Production build larger than expected"

**Problema**: Bundle muito grande

**Soluções**:

1. Analise o bundle:
```bash
npm install -D webpack-bundle-analyzer
ng build --configuration=production --stats-json
```

2. Ative lazy loading:
```typescript
const routes = [
  {
    path: 'galeria',
    loadComponent: () => import('./galeria.component')
      .then(m => m.GaleriaComponent)
  }
];
```

3. Configure produção:
```typescript
// environment.prod.ts
export const enableDebugTools = false;
```

---

### 15. "Git conflicts in package-lock.json"

**Problema**: Conflitos ao fazer merge

**Solução**:
```bash
git accept ours
npm install
git add package-lock.json
git commit -m "resolve package-lock conflict"
```

---

## Ferramentas de Debug

### 1. Angular DevTools Extension
```
Chrome -> Extensions -> Angular DevTools
```

### 2. Redux DevTools (se usar NgRx)
```bash
npm install @ngrx/store-devtools
```

### 3. Console Debugging
```typescript
console.log('Valor:', this.value);
console.table(this.data);
console.error('Erro:', error);
```

### 4. Breakpoints
No VS Code:
1. Pressione `F5` para debug
2. Clique na linha para adicionar breakpoint
3. A execução pausará ali

---

## Checklist de Resolução

Quando algo não funciona:

- [ ] Limpe cache e reinstale? `rm -rf node_modules && npm install`
- [ ] Reiniciou o servidor dev? `npm start`
- [ ] Verificou o console do navegador? `F12`
- [ ] Verificou o terminal? Há erros?
- [ ] Versões compatíveis? `npm list`
- [ ] Arquivo existe? `ls -la`
- [ ] Importação correta? Check path
- [ ] Sintaxe TypeScript? `ng build`

---

## Recursos Úteis

- [Angular Docs](https://angular.io)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/angular)
- [Angular GitHub Issues](https://github.com/angular/angular/issues)
- [RxJS Documentation](https://rxjs.dev)

---

**Precisa de mais ajuda? Consulte a documentação oficial!** 📚
