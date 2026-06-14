-- =====================================================================
-- V6 - Seed do dominio de faixas (RF-068). Configuravel: pode ser editado
-- por endpoints de gestao em fases futuras.
-- =====================================================================

INSERT INTO faixa (nome, categoria, ordem, graus_max, ativo) VALUES
    ('Branca',  'ADULTO', 1, 4, TRUE),
    ('Azul',    'ADULTO', 2, 4, TRUE),
    ('Roxa',    'ADULTO', 3, 4, TRUE),
    ('Marrom',  'ADULTO', 4, 4, TRUE),
    ('Preta',   'ADULTO', 5, 6, TRUE),
    ('Branca',         'INFANTIL', 1, 4, TRUE),
    ('Cinza',          'INFANTIL', 2, 4, TRUE),
    ('Amarela',        'INFANTIL', 3, 4, TRUE),
    ('Laranja',        'INFANTIL', 4, 4, TRUE),
    ('Verde',          'INFANTIL', 5, 4, TRUE);
