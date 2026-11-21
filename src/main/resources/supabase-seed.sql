-- Dados iniciais para o ambiente de homologação no Supabase.
-- Execute somente depois de schema.sql e apenas uma vez em um banco vazio.
-- A senha inicial dos funcionários abaixo é: 123456

INSERT INTO categoria (id, nome) VALUES
    (1, 'Roupas'),
    (2, 'Calçados');

INSERT INTO funcionario (id, nome, cpf, telefone, login, senha_hash, perfil, ativo) VALUES
    (1, 'Sandra Uchikado', '11144477735', '61999990001', 'sandra',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'PROPRIETARIA', TRUE),
    (2, 'Luiz Uchikado', '52998224725', '61999990002', 'luiz',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'PROPRIETARIA', TRUE),
    (3, 'Fernanda Moreira', '12345678909', '61999990003', 'fernanda',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'VENDEDOR', TRUE),
    (4, 'Carla Mendes', '93541134780', '61999990004', 'carla',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'VENDEDOR', TRUE);

INSERT INTO fornecedor (id, razao_social, nome_fantasia, cnpj, telefone, email, ativo) VALUES
    (1, 'Confecções Pequenos Passos Ltda', 'Pequenos Passos Kids',
     '11222333000181', '1133334444', 'contato@pequenospassos.com.br', TRUE);

INSERT INTO cliente (id, nome, cpf, telefone, ativo) VALUES
    (1, 'Ana Paula Costa', '39053344705', '61988887777', TRUE);

INSERT INTO produto
    (id, nome, categoria_id, tamanho, cor, codigo_barras, preco_venda, estoque_atual, fornecedor_id, ativo)
VALUES
    (1, 'Body Manga Curta Algodão', 1, 'M', 'Azul Bebê',
     '7890000000011', 39.90, 15, 1, TRUE),
    (2, 'Conjunto Moletom Infantil', 1, '4', 'Rosa',
     '7890000000028', 89.90, 8, 1, TRUE),
    (3, 'Tênis Infantil Velcro', 2, '2', 'Branco',
     '7890000000035', 119.90, 5, 1, TRUE);

-- Historico de inclusao dos dados iniciais.
-- usuario_id 1 representa Sandra, a proprietaria responsavel pela carga inicial.
INSERT INTO historico_funcionario
    (funcionario_id, nome, cpf, telefone, login, perfil, ativo, timestamp_alteracao,
     tipo_atualizacao, usuario_id)
VALUES
    (1, 'Sandra Uchikado', '11144477735', '61999990001', 'sandra', 'PROPRIETARIA', TRUE,
     CURRENT_TIMESTAMP, 'I', 1),
    (2, 'Luiz Uchikado', '52998224725', '61999990002', 'luiz', 'PROPRIETARIA', TRUE,
     CURRENT_TIMESTAMP, 'I', 1),
    (3, 'Fernanda Moreira', '12345678909', '61999990003', 'fernanda', 'VENDEDOR', TRUE,
     CURRENT_TIMESTAMP, 'I', 1),
    (4, 'Carla Mendes', '93541134780', '61999990004', 'carla', 'VENDEDOR', TRUE,
     CURRENT_TIMESTAMP, 'I', 1);

INSERT INTO historico_fornecedor
    (fornecedor_id, razao_social, nome_fantasia, cnpj, telefone, email, ativo,
     timestamp_alteracao, tipo_atualizacao, usuario_id)
VALUES
    (1, 'Confecções Pequenos Passos Ltda', 'Pequenos Passos Kids',
     '11222333000181', '1133334444', 'contato@pequenospassos.com.br', TRUE,
     CURRENT_TIMESTAMP, 'I', 1);

INSERT INTO historico_cliente
    (cliente_id, nome, cpf, telefone, ativo, timestamp_alteracao,
     tipo_atualizacao, usuario_id)
VALUES
    (1, 'Ana Paula Costa', '39053344705', '61988887777', TRUE,
     CURRENT_TIMESTAMP, 'I', 1);

INSERT INTO historico_produto
    (produto_id, nome, categoria_id, tamanho, cor, codigo_barras, preco_venda,
     estoque_atual, fornecedor_id, ativo, timestamp_alteracao,
     tipo_atualizacao, usuario_id)
VALUES
    (1, 'Body Manga Curta Algodão', 1, 'M', 'Azul Bebê', '7890000000011',
     39.90, 15, 1, TRUE, CURRENT_TIMESTAMP, 'I', 1),
    (2, 'Conjunto Moletom Infantil', 1, '4', 'Rosa', '7890000000028',
     89.90, 8, 1, TRUE, CURRENT_TIMESTAMP, 'I', 1),
    (3, 'Tênis Infantil Velcro', 2, '2', 'Branco', '7890000000035',
     119.90, 5, 1, TRUE, CURRENT_TIMESTAMP, 'I', 1);

SELECT setval(pg_get_serial_sequence('categoria', 'id'),
              (SELECT MAX(id) FROM categoria));
SELECT setval(pg_get_serial_sequence('funcionario', 'id'),
              (SELECT MAX(id) FROM funcionario));
SELECT setval(pg_get_serial_sequence('fornecedor', 'id'),
              (SELECT MAX(id) FROM fornecedor));
SELECT setval(pg_get_serial_sequence('cliente', 'id'),
              (SELECT MAX(id) FROM cliente));
SELECT setval(pg_get_serial_sequence('produto', 'id'),
              (SELECT MAX(id) FROM produto));
