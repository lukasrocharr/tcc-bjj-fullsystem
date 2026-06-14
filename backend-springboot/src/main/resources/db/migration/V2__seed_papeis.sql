-- =====================================================================
-- V2 - Dados de referencia: papeis do RBAC (RF-098)
-- Usuarios de teste sao criados (com hash BCrypt) pelo DataInitializer.
-- =====================================================================

INSERT INTO papel (nome, descricao) VALUES
    ('ALUNO',       'Aluno matriculado / cliente da loja'),
    ('PROFESSOR',   'Professor responsavel por turmas'),
    ('ADMIN',       'Administrador da academia'),
    ('SUPER_ADMIN', 'Administrador com acesso total ao sistema');
