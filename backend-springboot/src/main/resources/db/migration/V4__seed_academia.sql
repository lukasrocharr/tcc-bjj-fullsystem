-- =====================================================================
-- V4 - Seed de exemplo: modalidades e planos (RF-043, RF-044)
-- Turmas/alunos/matriculas de exemplo sao criados pelo DataInitializer
-- (dependem de IDs gerados e de horarios), mantendo o seed estatico enxuto.
-- =====================================================================

INSERT INTO modalidade (nome, descricao, ativo) VALUES
    ('Jiu-Jitsu Adulto',   'Treino de BJJ para adultos (gi)',            TRUE),
    ('Jiu-Jitsu Kids',     'Treino de BJJ para criancas',               TRUE),
    ('No-Gi',              'Treino sem kimono (submission grappling)',  TRUE),
    ('Competicao',         'Turma focada em competidores',              TRUE);

INSERT INTO plano (nome, descricao, valor, periodicidade, aulas_por_semana, ativo) VALUES
    ('Mensal 2x',     'Ate 2 aulas por semana',        149.90, 'MENSAL',     2, TRUE),
    ('Mensal Livre',  'Aulas ilimitadas no mes',       219.90, 'MENSAL',     5, TRUE),
    ('Trimestral',    'Plano trimestral com desconto',  599.90, 'TRIMESTRAL', 5, TRUE),
    ('Kids Mensal',   'Plano infantil mensal',          129.90, 'MENSAL',     2, TRUE);
