/**
 * Estilos compartilhados da loja publica (tema dourado #c9a84c sobre fundo claro).
 */
export const STORE_STYLES = `
  .store-page { max-width: 1100px; margin: 0 auto; padding: 1rem; }
  .store-title { font-size: 2rem; color: #0a0a0a; font-weight: 800; margin: 0 0 .25rem; }
  .store-sub { color: #777; margin: 0 0 2rem; }
  .alert { padding: .75rem 1rem; border-radius: 8px; margin-bottom: 1rem; font-size: .9rem; }
  .alert.error { background: #fee2e2; border: 1px solid #fecaca; color: #dc2626; }
  .alert.ok { background: #dcfce7; border: 1px solid #bbf7d0; color: #166534; }

  .filtros { display: flex; gap: .5rem; flex-wrap: wrap; margin-bottom: 1.5rem; }
  .chip { background: #fff; border: 1px solid #ddd; border-radius: 20px; padding: .45rem 1.1rem; cursor: pointer; font-weight: 600; color: #555; }
  .chip.active { background: #c9a84c; border-color: #c9a84c; color: #fff; }

  .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(230px, 1fr)); gap: 1.5rem; }
  .prod { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.08); display: flex; flex-direction: column; }
  .prod .img { height: 170px; background: #f0f0f0 center/cover no-repeat; display: flex; align-items: center; justify-content: center; font-size: 3rem; color: #ccc; }
  .prod .info { padding: 1rem; display: flex; flex-direction: column; gap: .5rem; flex: 1; }
  .prod h3 { margin: 0; font-size: 1.05rem; color: #0a0a0a; }
  .prod .cat { font-size: .72rem; color: #999; text-transform: uppercase; font-weight: 600; }
  .prod .preco { font-size: 1.3rem; font-weight: 800; color: #c9a84c; }
  .prod .desc { color: #777; font-size: .85rem; flex: 1; }
  .vars { display: flex; flex-wrap: wrap; gap: .35rem; }
  .var { border: 1px solid #ddd; background: #fff; border-radius: 6px; padding: .3rem .6rem; cursor: pointer; font-size: .8rem; }
  .var.sel { border-color: #c9a84c; background: #fdf6e3; color: #b39539; font-weight: 700; }
  .var:disabled { opacity: .4; cursor: not-allowed; text-decoration: line-through; }

  .btn { background: #c9a84c; color: #fff; border: none; padding: .7rem 1.2rem; border-radius: 8px; font-weight: 700; cursor: pointer; transition: all .3s ease; }
  .btn:hover:not(:disabled) { background: #b39539; }
  .btn:disabled { opacity: .5; cursor: not-allowed; }
  .btn.ghost { background: #fff; color: #555; border: 1px solid #ddd; }
  .btn.danger { background: #ef4444; }

  table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.06); }
  th { text-align: left; padding: 1rem; background: #f0f0f0; font-size: .78rem; text-transform: uppercase; color: #444; }
  td { padding: 1rem; border-bottom: 1px solid #eee; }
  .empty { padding: 2rem; text-align: center; color: #999; }

  .badge { display: inline-block; padding: .3rem .7rem; border-radius: 20px; font-size: .78rem; font-weight: 700; }
  .badge.ok { background: #dcfce7; color: #166534; }
  .badge.warn { background: #fef9c3; color: #854d0e; }
  .badge.off { background: #fee2e2; color: #991b1b; }
  .badge.info { background: #dbeafe; color: #0c4a6e; }

  .resumo { background: #fff; border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 10px rgba(0,0,0,.06); }
  .linha { display: flex; justify-content: space-between; padding: .4rem 0; color: #555; }
  .linha.total { font-size: 1.2rem; font-weight: 800; color: #0a0a0a; border-top: 2px solid #eee; margin-top: .5rem; padding-top: .75rem; }

  .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
  .fg { display: flex; flex-direction: column; }
  .fg.full { grid-column: 1 / -1; }
  .fg label { font-weight: 600; font-size: .85rem; margin-bottom: .35rem; color: #444; }
  .fg input, .fg select { padding: .65rem; border: 1px solid #ddd; border-radius: 6px; font-size: .95rem; }
  .fg input:focus, .fg select:focus { outline: none; border-color: #c9a84c; }
  .qty { width: 64px; padding: .4rem; border: 1px solid #ddd; border-radius: 6px; }

  @media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }
`;
