-- =====================================================================
-- V9 - Seed de exemplo do e-commerce (RF-011): categorias, produtos,
-- variacoes (com estoque) e um cupom. FKs resolvidas por subconsulta.
-- =====================================================================

INSERT INTO categoria (nome, ativo) VALUES
    ('Kimonos', TRUE),
    ('Acessorios', TRUE);

INSERT INTO produto (nome, descricao, categoria_id, preco, ativo, created_at, updated_at)
SELECT 'Kimono Trancado A2', 'Kimono trancado profissional', c.id, 350.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categoria c WHERE c.nome = 'Kimonos';

INSERT INTO produto (nome, descricao, categoria_id, preco, ativo, created_at, updated_at)
SELECT 'Faixa Oficial', 'Faixa de algodao reforcada', c.id, 80.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categoria c WHERE c.nome = 'Acessorios';

INSERT INTO produto (nome, descricao, categoria_id, preco, ativo, created_at, updated_at)
SELECT 'Protetor Bucal', 'Protetor bucal moldavel', c.id, 35.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM categoria c WHERE c.nome = 'Acessorios';

INSERT INTO produto_imagem (produto_id, url)
SELECT p.id, '/assets/images/kimono-1.jpg' FROM produto p WHERE p.nome = 'Kimono Trancado A2';

-- Variacoes (estoque na variacao)
INSERT INTO variacao_produto (produto_id, sku, tamanho, cor, preco_adicional, estoque, created_at, updated_at)
SELECT p.id, 'KIM-A2-BR', 'A2', 'Branco', 0.00, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM produto p WHERE p.nome = 'Kimono Trancado A2';

INSERT INTO variacao_produto (produto_id, sku, tamanho, cor, preco_adicional, estoque, created_at, updated_at)
SELECT p.id, 'KIM-A2-AZ', 'A2', 'Azul', 20.00, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM produto p WHERE p.nome = 'Kimono Trancado A2';

INSERT INTO variacao_produto (produto_id, sku, tamanho, cor, preco_adicional, estoque, created_at, updated_at)
SELECT p.id, 'FX-PRETA', 'A3', 'Preta', 0.00, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM produto p WHERE p.nome = 'Faixa Oficial';

INSERT INTO variacao_produto (produto_id, sku, tamanho, cor, preco_adicional, estoque, created_at, updated_at)
SELECT p.id, 'PB-UNICO', 'Unico', NULL, 0.00, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM produto p WHERE p.nome = 'Protetor Bucal';

INSERT INTO cupom (codigo, tipo, valor, min_subtotal, validade, ativo) VALUES
    ('BEMVINDO10', 'PERCENTUAL', 10.00, 100.00, NULL, TRUE);
