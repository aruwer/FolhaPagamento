BEGIN;

UPDATE colaboradores
SET matricula = CASE matricula
        WHEN 'DEMO001' THEN 'FP001'
        WHEN 'DEMO002' THEN 'FP002'
        WHEN 'DEMO003' THEN 'FP003'
        WHEN 'DEMO004' THEN 'FP004'
        WHEN 'DEMO005' THEN 'FP005'
        WHEN 'DEMO006' THEN 'FP006'
        WHEN 'DEMO007' THEN 'FP007'
        WHEN 'DEMO008' THEN 'FP008'
        WHEN 'DEMO009' THEN 'FP009'
        WHEN 'DEMO010' THEN 'FP010'
    END,
    nome = CASE matricula
        WHEN 'DEMO001' THEN U&'Jo\00E3o Almeida'
        WHEN 'DEMO002' THEN 'Maria Oliveira'
        WHEN 'DEMO003' THEN U&'J\00FAlia Santos'
        WHEN 'DEMO004' THEN 'Gabriel Costa'
        WHEN 'DEMO005' THEN 'Ana Beatriz Ferreira'
        WHEN 'DEMO006' THEN 'Lucas Rodrigues'
        WHEN 'DEMO007' THEN 'Beatriz Martins'
        WHEN 'DEMO008' THEN 'Pedro Souza'
        WHEN 'DEMO009' THEN 'Camila Ribeiro'
        WHEN 'DEMO010' THEN 'Rafael Carvalho'
    END
WHERE matricula IN (
    'DEMO001', 'DEMO002', 'DEMO003', 'DEMO004', 'DEMO005',
    'DEMO006', 'DEMO007', 'DEMO008', 'DEMO009', 'DEMO010'
);

UPDATE colaboradores
SET nome = CASE matricula
        WHEN 'FP001' THEN U&'Jo\00E3o Almeida'
        WHEN 'FP002' THEN 'Maria Oliveira'
        WHEN 'FP003' THEN U&'J\00FAlia Santos'
        WHEN 'FP004' THEN 'Gabriel Costa'
        WHEN 'FP005' THEN 'Ana Beatriz Ferreira'
        WHEN 'FP006' THEN 'Lucas Rodrigues'
        WHEN 'FP007' THEN 'Beatriz Martins'
        WHEN 'FP008' THEN 'Pedro Souza'
        WHEN 'FP009' THEN 'Camila Ribeiro'
        WHEN 'FP010' THEN 'Rafael Carvalho'
    END
WHERE matricula IN (
    'FP001', 'FP002', 'FP003', 'FP004', 'FP005',
    'FP006', 'FP007', 'FP008', 'FP009', 'FP010'
);

INSERT INTO colaboradores (
    matricula,
    nome,
    tipo_colaborador,
    salario_base,
    valor_vendas,
    percentual_comissao,
    quantidade_produzida,
    valor_por_unidade
) VALUES
    ('FP001', U&'Jo\00E3o Almeida',   'PADRAO',       3200.00, NULL,      NULL,  NULL,      NULL),
    ('FP002', 'Maria Oliveira',        'COMISSIONADO', 2500.00, 14000.00,  5.00, NULL,      NULL),
    ('FP003', U&'J\00FAlia Santos',   'PRODUCAO',     2200.00, NULL,      NULL,  850.000,  1.50),
    ('FP004', 'Gabriel Costa',         'PADRAO',       4200.00, NULL,      NULL,  NULL,      NULL),
    ('FP005', 'Ana Beatriz Ferreira',  'COMISSIONADO', 3000.00, 21750.00,  7.50, NULL,      NULL),
    ('FP006', 'Lucas Rodrigues',       'PRODUCAO',     2450.00, NULL,      NULL, 1200.500,  0.80),
    ('FP007', 'Beatriz Martins',       'PADRAO',       2800.00, NULL,      NULL,  NULL,      NULL),
    ('FP008', 'Pedro Souza',           'COMISSIONADO', 2700.00, 18500.00,  4.00, NULL,      NULL),
    ('FP009', 'Camila Ribeiro',        'PRODUCAO',     2600.00, NULL,      NULL,  975.250,  1.20),
    ('FP010', 'Rafael Carvalho',       'PADRAO',       3500.00, NULL,      NULL,  NULL,      NULL)
ON CONFLICT (matricula) DO NOTHING
RETURNING matricula;

COMMIT;
