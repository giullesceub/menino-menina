-- Dados iniciais para o ambiente de homologação no Supabase.
-- Execute somente depois de schema.sql e apenas uma vez em um banco vazio.
-- A senha inicial dos funcionários abaixo é: 123456

INSERT INTO categoria (id, nome) VALUES
    (1, 'Roupas'),
    (2, 'Calçados');

INSERT INTO funcionario (id, nome, cpf, telefone, login, senha_hash, perfil, ativo) VALUES
    (1, 'Mariana Souza', '11144477735', '61999990001', 'mariana',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'PROPRIETARIA', TRUE),
    (2, 'Lucas Almeida', '52998224725', '61999990002', 'lucas',
     '$2a$10$E7qat97t9mNZzjEtoP/cQuCtxjcvbiV1lv4HTszenmrewxb/N.8b.', 'VENDEDOR', TRUE),
    (3, 'Fernanda Lima', '12345678909', '61999990003', 'fernanda',
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
