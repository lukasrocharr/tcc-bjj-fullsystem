/**
 * Estilos compartilhados das telas de CRUD do admin (tema dourado #c9a84c),
 * consistentes com as telas legadas. Importado no array `styles` dos componentes.
 */
export const ADMIN_CRUD_STYLES = `
  .page { padding: 0; }
  .page-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; gap: 1rem; flex-wrap: wrap;
  }
  .page-header h1 { font-size: 2rem; color: #0a0a0a; margin: 0; font-weight: 700; }
  .page-header p { color: #666; margin: 0.5rem 0 0; }
  .toolbar { display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap; }
  .search-input { padding: 0.6rem 0.9rem; border: 1px solid #ddd; border-radius: 8px; font-size: 0.95rem; min-width: 220px; }
  .btn-novo { background: #c9a84c; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all .3s ease; }
  .btn-novo:hover { background: #b39539; transform: translateY(-2px); }

  .form-section { background: white; border-radius: 12px; padding: 2rem; margin-bottom: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
  .form-section h2 { font-size: 1.4rem; color: #0a0a0a; margin: 0 0 1.5rem; font-weight: 700; }
  .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem; }
  .form-group { display: flex; flex-direction: column; }
  .form-group.full { grid-column: 1 / -1; }
  .form-group label { font-weight: 600; margin-bottom: .5rem; color: #333; font-size: .9rem; }
  .form-group input, .form-group select, .form-group textarea {
    padding: .7rem; border: 1px solid #ddd; border-radius: 6px; font-size: .95rem; font-family: inherit;
  }
  .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
    outline: none; border-color: #c9a84c; box-shadow: 0 0 0 3px rgba(201,168,76,.1);
  }
  .checks { display: flex; flex-wrap: wrap; gap: .75rem; }
  .checks label { display: flex; gap: .4rem; align-items: center; font-weight: 500; }
  .form-actions { display: flex; gap: 1rem; margin-top: 1rem; }
  .btn-salvar { background: #22c55e; color: white; border: none; padding: .75rem 1.5rem; border-radius: 6px; font-weight: 600; cursor: pointer; }
  .btn-salvar:hover:not(:disabled) { background: #16a34a; }
  .btn-salvar:disabled { opacity: .5; cursor: not-allowed; }
  .btn-cancelar { background: #ef4444; color: white; border: none; padding: .75rem 1.5rem; border-radius: 6px; font-weight: 600; cursor: pointer; }
  .btn-cancelar:hover { background: #dc2626; }

  .tabela-section { background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,.08); overflow-x: auto; }
  table { width: 100%; border-collapse: collapse; }
  thead { background: #f0f0f0; border-bottom: 2px solid #ddd; }
  th { padding: 1rem; text-align: left; font-weight: 700; color: #333; font-size: .8rem; text-transform: uppercase; letter-spacing: .5px; }
  td { padding: 1rem; border-bottom: 1px solid #eee; color: #333; font-size: .95rem; }
  tbody tr:hover { background: #f9f9f9; }
  .empty { padding: 2rem; text-align: center; color: #999; }

  .badge { display: inline-block; padding: .35rem .75rem; border-radius: 20px; font-size: .8rem; font-weight: 600; }
  .badge.ok { background: #dcfce7; color: #166534; }
  .badge.warn { background: #fef9c3; color: #854d0e; }
  .badge.off { background: #fee2e2; color: #991b1b; }
  .badge.info { background: #dbeafe; color: #0c4a6e; }

  .acoes { display: flex; gap: .5rem; }
  .icon-btn { background: none; border: none; font-size: 1.1rem; cursor: pointer; padding: .4rem; border-radius: 4px; transition: background .3s ease; }
  .icon-btn:hover { background: #f0f0f0; }
  .icon-btn.del:hover { background: #fee2e2; }

  .alert { padding: .75rem 1rem; border-radius: 8px; margin-bottom: 1rem; font-size: .9rem; }
  .alert.error { background: #fee2e2; border: 1px solid #fecaca; color: #dc2626; }

  .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
  .modal { background: white; border-radius: 12px; padding: 2rem; max-width: 420px; box-shadow: 0 10px 30px rgba(0,0,0,.3); }
  .modal h3 { margin: 0 0 1rem; color: #0a0a0a; }
  .modal p { color: #666; margin: 0 0 1.5rem; }
  .modal-actions { display: flex; gap: 1rem; }
  .btn-confirmar { flex: 1; background: #ef4444; color: white; border: none; padding: .75rem; border-radius: 6px; font-weight: 600; cursor: pointer; }

  @media (max-width: 768px) { .form-row { grid-template-columns: 1fr; } th, td { padding: .75rem .5rem; } }
`;
