-- Registros de inclusao para dados que ja existem no Supabase.
-- Execute uma vez em bancos criados antes da carga historica ter sido incluida.

INSERT INTO historico_funcionario
    (funcionario_id, nome, cpf, telefone, login, perfil, ativo,
     timestamp_alteracao, tipo_atualizacao, usuario_id)
SELECT f.id, f.nome, f.cpf, f.telefone, f.login, f.perfil, f.ativo,
       CURRENT_TIMESTAMP, 'I',
       COALESCE((SELECT id FROM funcionario WHERE perfil = 'PROPRIETARIA' AND ativo = TRUE ORDER BY id LIMIT 1), f.id)
FROM funcionario f
WHERE NOT EXISTS (
    SELECT 1
    FROM historico_funcionario h
    WHERE h.funcionario_id = f.id
      AND h.tipo_atualizacao = 'I'
);

INSERT INTO historico_fornecedor
    (fornecedor_id, razao_social, nome_fantasia, cnpj, telefone, email, ativo,
     timestamp_alteracao, tipo_atualizacao, usuario_id)
SELECT f.id, f.razao_social, f.nome_fantasia, f.cnpj, f.telefone, f.email, f.ativo,
       CURRENT_TIMESTAMP, 'I',
       (SELECT id FROM funcionario WHERE perfil = 'PROPRIETARIA' AND ativo = TRUE ORDER BY id LIMIT 1)
FROM fornecedor f
WHERE NOT EXISTS (
    SELECT 1
    FROM historico_fornecedor h
    WHERE h.fornecedor_id = f.id
      AND h.tipo_atualizacao = 'I'
);

INSERT INTO historico_cliente
    (cliente_id, nome, cpf, telefone, ativo,
     timestamp_alteracao, tipo_atualizacao, usuario_id)
SELECT c.id, c.nome, c.cpf, c.telefone, c.ativo,
       CURRENT_TIMESTAMP, 'I',
       (SELECT id FROM funcionario WHERE perfil = 'PROPRIETARIA' AND ativo = TRUE ORDER BY id LIMIT 1)
FROM cliente c
WHERE NOT EXISTS (
    SELECT 1
    FROM historico_cliente h
    WHERE h.cliente_id = c.id
      AND h.tipo_atualizacao = 'I'
);

INSERT INTO historico_produto
    (produto_id, nome, categoria_id, tamanho, cor, codigo_barras, preco_venda,
     estoque_atual, fornecedor_id, ativo, timestamp_alteracao,
     tipo_atualizacao, usuario_id)
SELECT p.id, p.nome, p.categoria_id, p.tamanho, p.cor, p.codigo_barras,
       p.preco_venda, p.estoque_atual, p.fornecedor_id, p.ativo,
       CURRENT_TIMESTAMP, 'I',
       (SELECT id FROM funcionario WHERE perfil = 'PROPRIETARIA' AND ativo = TRUE ORDER BY id LIMIT 1)
FROM produto p
WHERE NOT EXISTS (
    SELECT 1
    FROM historico_produto h
    WHERE h.produto_id = p.id
      AND h.tipo_atualizacao = 'I'
);
