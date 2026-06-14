# VS Code Recomendações

## Extensões Recomendadas

1. **Angular Language Service** - Suporte completo para Angular
2. **Angular Schematics** - Geração de componentes
3. **Prettier** - Formatter de código
4. **ESLint** - Linting
5. **Thunder Client** ou **REST Client** - Testar APIs
6. **GitLens** - Git insights
7. **Visual Studio Code Extensions by Chrome** - DevTools

## Instalando Extensões

```bash
code --install-extension Angular.ng-template
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
```

## Configurações Recomendadas

Adicione ao `.vscode/settings.json`:

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnPaste": true,
  "editor.tabSize": 2,
  "editor.insertSpaces": true,
  "files.autoSave": "afterDelay",
  "files.autoSaveDelay": 1000,
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[html]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[css]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "angular.tsdk": "./node_modules/@angular/language-service",
  "explorer.excludeGitIgnore": true,
  "search.exclude": {
    "**/node_modules": true,
    "**/dist": true
  }
}
```

## Launch Configuration

Crie `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "ng serve",
      "type": "chrome",
      "request": "launch",
      "preLaunchTask": "ng: serve",
      "url": "http://localhost:4200",
      "webRoot": "${workspaceFolder}",
      "sourceMap": true
    }
  ],
  "compounds": []
}
```

## Atalhos Úteis

| Atalho | Função |
|--------|--------|
| `Ctrl+K Ctrl+0` | Fold all |
| `Ctrl+K Ctrl+J` | Unfold all |
| `Ctrl+/` | Toggle comment |
| `Alt+Shift+A` | Block comment |
| `F2` | Rename |
| `Ctrl+H` | Find and replace |

---

Copie estas configurações para `.vscode/settings.json` e `.vscode/launch.json`
